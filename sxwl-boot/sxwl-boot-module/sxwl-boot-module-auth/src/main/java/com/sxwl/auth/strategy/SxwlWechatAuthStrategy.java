package com.sxwl.auth.strategy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sxwl.auth.config.SxwlWechatProperties;
import com.sxwl.auth.mapper.PlaUserInfoMapper;
import com.sxwl.auth.model.entity.PlaUserInfo;
import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.security.model.SxwlLoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.Set;

/**
 * 微信登录策略
 *
 * <p>处理微信小程序一键登录（首次自动注册）：</p>
 * <ol>
 *   <li>携带 code 调用微信 {@code jscode2session} 接口换取 openid</li>
 *   <li>按 openid 查询 {@code pla_user_info}，不存在则自动注册</li>
 *   <li>构建 {@link SxwlLoginUser} 供统一 Token 签发使用</li>
 * </ol>
 *
 * <p>注意：与后台登录策略（密码/短信）不同，微信登录请求体不匹配
 * {@code SxwlAuthenticationStrategy} 的 {@code SxwlLoginRequest} 模型，故独立实现。</p>
 *
 * @author shitianyang
 * @date 2026/7/7
 * @since 0.1.0
 */
@Component
public class SxwlWechatAuthStrategy {

    private static final Logger log = LoggerFactory.getLogger(SxwlWechatAuthStrategy.class);

    /** 微信小程序 code2session 接口 */
    private static final String CODETOSESSION_URL = "https://api.weixin.qq.com/sns/jscode2session";

    private final SxwlWechatProperties properties;
    private final PlaUserInfoMapper plaUserInfoMapper;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public SxwlWechatAuthStrategy(SxwlWechatProperties properties,
                                  PlaUserInfoMapper plaUserInfoMapper,
                                  ObjectMapper objectMapper) {
        this.properties = properties;
        this.plaUserInfoMapper = plaUserInfoMapper;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    /**
     * 微信认证：code 换 openid → 查/建用户 → 构建登录用户
     *
     * @param code      uni.login 获取的临时凭证
     * @param nickname  微信昵称（可为空）
     * @param avatarUrl 微信头像 URL（可为空）
     * @return 登录用户（roles/perms 为空，C 端无后台权限体系）
     */
    public SxwlLoginUser authenticate(String code, String nickname, String avatarUrl) {
        if (code == null || code.isBlank()) {
            throw new SxwlBusinessException(400, "微信登录失败：code 不能为空");
        }

        // 1. code 换 openid
        Map<String, Object> session = code2session(code);
        String openId = (String) session.get("openid");
        if (openId == null || openId.isBlank()) {
            log.warn("code2session 未返回 openid: {}", session);
            throw new SxwlBusinessException(400, "微信登录失败：未获取到用户标识");
        }

        // 2. 查/建用户
        PlaUserInfo user = plaUserInfoMapper.selectByOpenId(openId);
        if (user == null) {
            user = registerUser(openId, nickname, avatarUrl);
        } else {
            updateUserLoginInfo(user, nickname, avatarUrl);
        }

        // 3. 构建登录用户（C 端无角色/权限体系）
        SxwlLoginUser loginUser = new SxwlLoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(openId);
        loginUser.setNickname(user.getNickname() != null ? user.getNickname() : "微信用户");
        loginUser.setStatus(user.getStatus() != null ? user.getStatus() : 1);
        loginUser.setRoles(Set.of());
        loginUser.setPerms(Set.of());
        loginUser.setDataScope(0);
        loginUser.setDataScopeOrgIds(Set.of());
        return loginUser;
    }

    /**
     * 调用微信 code2session 接口换取 openid/session_key
     */
    private Map<String, Object> code2session(String code) {
        String appid = properties.getAppid();
        String secret = properties.getSecret();
        if (appid == null || appid.isBlank() || secret == null || secret.isBlank()) {
            throw new SxwlBusinessException(500, "微信登录未配置，请联系管理员（sxwl.wechat.appid/secret）");
        }

        String url = CODETOSESSION_URL
                + "?appid=" + appid
                + "&secret=" + secret
                + "&js_code=" + code
                + "&grant_type=authorization_code";

        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .GET()
                    .timeout(Duration.ofSeconds(5))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                log.error("code2session HTTP 异常: status={}", response.statusCode());
                throw new SxwlBusinessException(500, "微信服务调用失败");
            }

            Map<String, Object> result = objectMapper.readValue(response.body(), new TypeReference<>() {
            });
            Integer errcode = (Integer) result.get("errcode");
            if (errcode != null && errcode != 0) {
                log.warn("code2session 失败: errcode={}, errmsg={}", errcode, result.get("errmsg"));
                throw new SxwlBusinessException(400, "微信登录失败：" + result.get("errmsg"));
            }
            return result;
        } catch (SxwlBusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("code2session 调用异常", e);
            throw new SxwlBusinessException(500, "微信服务调用失败");
        }
    }

    /**
     * 自动注册微信用户
     * <p>create_by/create_org 置 0（系统注册），审计字段由拦截器填充。</p>
     */
    private PlaUserInfo registerUser(String openId, String nickname, String avatarUrl) {
        PlaUserInfo user = new PlaUserInfo();
        user.setWxOpenId(openId);
        user.setNickname(nickname != null && !nickname.isBlank() ? nickname : "微信用户");
        user.setAvatar(avatarUrl);
        user.setRegisterSource("wechat");
        user.setLoginType("wechat");
        user.setStatus(1);
        user.setCreateBy(0L);
        user.setCreateOrg(0L);

        int result = plaUserInfoMapper.insertUser(user);
        if (result != 1) {
            log.error("微信用户注册失败: openid={}", openId);
            throw new SxwlBusinessException(500, "微信用户注册失败");
        }
        log.info("微信用户注册成功: id={}", user.getId());
        return user;
    }

    /**
     * 老用户登录：更新登录方式/时间，同步微信最新资料
     */
    private void updateUserLoginInfo(PlaUserInfo user, String nickname, String avatarUrl) {
        plaUserInfoMapper.updateLoginInfo(user.getId());
        if (nickname != null && !nickname.isBlank()) {
            plaUserInfoMapper.updateProfile(user.getId(), nickname, avatarUrl);
            user.setNickname(nickname);
            user.setAvatar(avatarUrl);
        }
    }
}

package com.sxwl.auth.strategy;

import com.sxwl.auth.mapper.SysAuthUserMapper;
import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.redis.helper.SxwlRedisHelper;
import com.sxwl.security.captcha.SxwlCaptchaValidator;
import com.sxwl.security.model.SxwlLoginRequest;
import com.sxwl.security.model.SxwlLoginUser;
import com.sxwl.security.spi.SxwlAuthenticationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 短信登录策略
 * <p>
 * 验证手机号 + 短信验证码，成功后通过 {@code SysAuthUserMapper} 查询用户并构建 {@link SxwlLoginUser}。
 * </p>
 *
 * @author shitianyang
 * @date 2026/7/7
 * @since 0.1.0
 */
@Component
public class SxwlSmsAuthStrategy implements SxwlAuthenticationStrategy {

    private static final Logger log = LoggerFactory.getLogger(SxwlSmsAuthStrategy.class);

    /** 短信登录方式标识 */
    public static final String LOGIN_TYPE_SMS = "sms";

    private final SysAuthUserMapper sysAuthUserMapper;
    private final SxwlCaptchaValidator captchaValidator;
    private final SxwlRedisHelper redisHelper;

    public SxwlSmsAuthStrategy(SysAuthUserMapper sysAuthUserMapper, SxwlCaptchaValidator captchaValidator, SxwlRedisHelper redisHelper) {
        this.sysAuthUserMapper = sysAuthUserMapper;
        this.captchaValidator = captchaValidator;
        this.redisHelper = redisHelper;
    }

    @Override
    public String getType() {
        return LOGIN_TYPE_SMS;
    }

    /**
     * 短信登录认证
     * <p>
     * 认证流程：
     * <ol>
     *   <li>校验图形验证码（如启用）</li>
     *   <li>校验短信验证码（从 Redis 获取，一次性使用）</li>
     *   <li>根据手机号查询用户</li>
     *   <li>检查用户状态（是否禁用）</li>
     *   <li>构建 {@link SxwlLoginUser} 并返回</li>
     * </ol>
     * </p>
     *
     * @param request 登录请求（包含 phone、smsCode、captchaUuid、captchaCode）
     * @return 登录用户信息
     * @throws SxwlBusinessException 验证码错误、用户不存在或状态异常时抛出
     */
    @Override
    public SxwlLoginUser authenticate(SxwlLoginRequest request) {
        // 1. 参数校验
        String phone = request.getPhone();
        String smsCode = request.getSmsCode();
        if (phone == null || phone.isEmpty()) {
            throw new SxwlBusinessException("手机号不能为空");
        }
        if (smsCode == null || smsCode.isEmpty()) {
            throw new SxwlBusinessException("短信验证码不能为空");
        }

        // 2. 校验图形验证码（如需要）
        try {
            captchaValidator.validateImageCaptcha(request.getCaptchaUuid(), request.getCaptchaCode());
        } catch (SxwlBusinessException e) {
            log.warn("图形验证码校验失败: {}", e.getMessage());
            throw e;
        }

        // 3. 校验短信验证码（从 Redis 获取，一次性使用）
        try {
            captchaValidator.validateSmsCaptcha(phone, smsCode);
        } catch (SxwlBusinessException e) {
            log.warn("短信验证码校验失败: phone={}, error={}", phone, e.getMessage());
            throw new SxwlBusinessException("短信验证码错误");
        }

        // 4. 根据手机号查询用户
        Map<String, Object> userInfo = sysAuthUserMapper.findByPhone(phone);

        if (userInfo == null || userInfo.isEmpty()) {
            log.error("手机号对应用户不存在: phone={}", phone);
            throw new SxwlBusinessException("用户不存在，请先注册");
        }

        // 5. 检查用户状态
        Object statusObj = userInfo.get("status");
        if (statusObj != null) {
            int status = Integer.parseInt(statusObj.toString());
            if (status != 1) {
                log.warn("用户已被禁用: phone={}, status={}", phone, status);
                throw new SxwlBusinessException("用户已被禁用，请联系管理员");
            }
        }

        // 6. 构建 SxwlLoginUser
        Long userId = ((Number) userInfo.get("id")).longValue();
        String username = userInfo.get("username").toString();
        String nickname = userInfo.getOrDefault("nickname", username).toString();
        String createOrg = userInfo.get("create_org") != null ? userInfo.get("create_org").toString() : null;

        SxwlLoginUser loginUser = new SxwlLoginUser();
        loginUser.setUserId(userId);
        loginUser.setUsername(username);
        loginUser.setNickname(nickname);
        loginUser.setClientType("admin");
        loginUser.setDeviceId(request.getDeviceId());
        // TODO: 角色和权限数据需要后续加载
        loginUser.setRoles(java.util.Collections.emptyList());
        loginUser.setPermissions(java.util.Collections.emptySet());
        loginUser.setDataScope(createOrg); // 暂时用组织代替数据权限

        log.info("短信登录成功: userId={}, phone={}", userId, phone);
        return loginUser;
    }
}

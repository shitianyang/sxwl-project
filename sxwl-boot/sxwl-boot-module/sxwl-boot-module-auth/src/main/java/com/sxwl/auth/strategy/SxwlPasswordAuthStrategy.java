package com.sxwl.auth.strategy;

import com.sxwl.auth.crypto.SxwlPasswordDecryptor;
import com.sxwl.auth.mapper.SysUserMapper;
import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.security.model.SxwlLoginRequest;
import com.sxwl.security.model.SxwlLoginUser;
import com.sxwl.security.spi.SxwlAuthenticationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 密码登录策略
 * <p>
 * 实现 {@link SxwlAuthenticationStrategy}，处理密码登录请求。
 * auth 自己查 sys_user 表做认证，不通过 SPI 委托 system。
 * </p>
 *
 * <h3>认证流程</h3>
 * <ol>
 *   <li>SM2 解密前端传来的加密密码 → 明文</li>
 *   <li>查 sys_user_info 表获取密码哈希 + 账号状态</li>
 *   <li>校验账号状态（status != 0）</li>
 *   <li>SM3 比对密码</li>
 *   <li>构建 SxwlLoginUser 返回</li>
 * </ol>
 *
 * @author shitianyang
 * @date 2026/7/7
 * @since 0.1.0
 */
@Component
public class SxwlPasswordAuthStrategy implements SxwlAuthenticationStrategy {

    private static final Logger log = LoggerFactory.getLogger(SxwlPasswordAuthStrategy.class);

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final SxwlPasswordDecryptor passwordDecryptor;

    public SxwlPasswordAuthStrategy(SysUserMapper sysUserMapper,
                                    PasswordEncoder passwordEncoder,
                                    SxwlPasswordDecryptor passwordDecryptor) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.passwordDecryptor = passwordDecryptor;
    }

    @Override
    public SxwlLoginUser authenticate(SxwlLoginRequest request) {
        String username = request.getUsername();
        if (username == null || username.isBlank()) {
            throw new SxwlBusinessException(400, "用户名不能为空");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new SxwlBusinessException(400, "密码不能为空");
        }

        // 1. SM2 解密
        String plainPassword = passwordDecryptor.decrypt(request.getPassword());
        if (plainPassword.isBlank()) {
            throw new SxwlBusinessException(400, "密码解密后为空");
        }

        // 2. 查库（auth 自己查，不通过 SPI）
        Map<String, Object> userRow;
        try {
            userRow = sysUserMapper.selectByUsername(username);
        } catch (Exception e) {
            log.warn("查用户失败: username={}", username, e);
            throw new SxwlBusinessException(401, "用户名或密码错误");
        }
        if (userRow == null || userRow.isEmpty()) {
            log.warn("用户不存在: username={}", username);
            throw new SxwlBusinessException(401, "用户名或密码错误");
        }

        // 3. 校验账号状态
        Integer status = (Integer) userRow.get("status");
        if (status == null || status == 0) {
            log.warn("账号已被禁用: username={}", username);
            throw new SxwlBusinessException(403, "账号已被禁用，请联系管理员");
        }

        // 4. 比对密码
        String encodedPassword = (String) userRow.get("password");
        if (encodedPassword == null || !passwordEncoder.matches(plainPassword, encodedPassword)) {
            log.warn("密码错误: username={}", username);
            throw new SxwlBusinessException(401, "用户名或密码错误");
        }

        // 5. 构建 SxwlLoginUser（角色/权限/数据范围后续完善）
        Long userId = ((Number) userRow.get("id")).longValue();
        String nickname = (String) userRow.get("nickname");
        Long createOrg = userRow.get("create_org") != null
                ? ((Number) userRow.get("create_org")).longValue() : null;

        SxwlLoginUser loginUser = new SxwlLoginUser();
        loginUser.setUserId(userId);
        loginUser.setUsername(username);
        loginUser.setNickname(nickname != null ? nickname : username);
        loginUser.setStatus(status);
        loginUser.setCreateOrg(createOrg);
        // dataScopeOrgIds = null 表示全部数据（不限制），后续由角色体系计算后填充

        log.info("密码认证成功: userId={}, username={}", userId, username);
        return loginUser;
    }
}

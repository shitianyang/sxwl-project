package com.sxwl.auth.strategy;

import com.sxwl.auth.mapper.SysAuthUserMapper;
import com.sxwl.common.constants.SxwlSystemConstants;
import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.redis.helper.SxwlRedisHelper;
import com.sxwl.security.captcha.SxwlCaptchaValidator;
import com.sxwl.security.model.SxwlLoginRequest;
import com.sxwl.security.model.SxwlLoginUser;
import com.sxwl.security.spi.SxwlAuthenticationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private final SysAuthUserMapper sysAuthUserMapper;
    private final SxwlCaptchaValidator captchaValidator;
    private final SxwlRedisHelper redisHelper;

    public SxwlSmsAuthStrategy(SysAuthUserMapper sysAuthUserMapper, SxwlCaptchaValidator captchaValidator, SxwlRedisHelper redisHelper) {
        this.sysAuthUserMapper = sysAuthUserMapper;
        this.captchaValidator = captchaValidator;
        this.redisHelper = redisHelper;
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
        String username = (String) userInfo.get("username");
        String nickname = (String) userInfo.getOrDefault("nickname", username);
        Long createOrg = userInfo.get("create_org") != null
                ? ((Number) userInfo.get("create_org")).longValue() : null;

        SxwlLoginUser loginUser = new SxwlLoginUser();
        loginUser.setUserId(userId);
        loginUser.setUsername(username);
        loginUser.setNickname(nickname != null ? nickname : username);
        loginUser.setStatus(1);
        loginUser.setCreateOrg(createOrg);

        // 7. 填充授权信息（roles / perms / dataScope）
        fillAuthorization(loginUser);

        log.info("短信登录成功: userId={}, phone={}", userId, phone);
        return loginUser;
    }

    /**
     * 填充用户授权信息（roles、perms、dataScope）
     * <p>
     * 与 {@link SxwlPasswordAuthStrategy#fillAuthorization} 逻辑一致，抽取为公共方法。
     * </p>
     *
     * @param loginUser 登录用户对象
     */
    private void fillAuthorization(SxwlLoginUser loginUser) {
        List<Map<String, Object>> roleRows = sysAuthUserMapper.selectRolesByUserId(loginUser.getUserId());

        Set<String> roles = new HashSet<>();
        Set<Long> scopedOrgIds = new HashSet<>();
        boolean allData = false;
        Integer strongestScope = null;

        for (Map<String, Object> row : roleRows) {
            String roleCode = (String) row.get("role_code");
            if (roleCode != null && !roleCode.isBlank()) {
                roles.add(roleCode);
            }

            Long roleId = toLong(row.get("id"));
            Integer dataScope = toInteger(row.get("data_scope"));
            if (dataScope == null) {
                continue;
            }
            boolean isProtectedSuperAdmin = SxwlSystemConstants.ADMIN_USERNAME.equals(loginUser.getUsername())
                    && SxwlSystemConstants.ADMIN_ROLE_CODE.equals(roleCode);
            if (dataScope == 1 && !isProtectedSuperAdmin) {
                continue;
            }
            strongestScope = strongestScope == null ? dataScope : Math.min(strongestScope, dataScope);

            if (dataScope == 1) {
                allData = true;
                break;
            }
            if (dataScope == 2) {
                addIfNotNull(scopedOrgIds, loginUser.getCreateOrg());
            } else if (dataScope == 3) {
                addIfNotNull(scopedOrgIds, loginUser.getCreateOrg());
                if (loginUser.getCreateOrg() != null) {
                    scopedOrgIds.addAll(sysAuthUserMapper.selectSelfAndChildOrgIds(loginUser.getCreateOrg()));
                }
            } else if (dataScope == 4) {
                loginUser.setDataScopeSelf(true);
            } else if (dataScope == 5 && roleId != null) {
                scopedOrgIds.addAll(sysAuthUserMapper.selectCustomDataScopeOrgIds(roleId));
            }
        }

        Set<String> perms = new HashSet<>(sysAuthUserMapper.selectPermissionsByUserId(loginUser.getUserId()));
        if (allData) {
            perms.add("*:*:*");
        }

        loginUser.setRoles(roles);
        loginUser.setPerms(perms);
        loginUser.setDataScope(allData ? 1 : (strongestScope != null ? strongestScope : 0));
        loginUser.setDataScopeOrgIds(allData ? null : scopedOrgIds);
    }

    private void addIfNotNull(Set<Long> values, Long value) {
        if (value != null) {
            values.add(value);
        }
    }

    private Long toLong(Object value) {
        return value instanceof Number ? ((Number) value).longValue() : null;
    }

    private Integer toInteger(Object value) {
        return value instanceof Number ? ((Number) value).intValue() : null;
    }
}

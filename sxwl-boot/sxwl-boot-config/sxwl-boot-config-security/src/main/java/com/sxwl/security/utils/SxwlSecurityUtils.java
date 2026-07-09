package com.sxwl.security.utils;

import com.sxwl.common.utils.SxwlPrincipalUtils;
import com.sxwl.security.model.SxwlLoginUser;

import java.util.Optional;

/**
 * Security 工具类
 * <p>
 * 提供获取当前登录用户 {@link SxwlLoginUser} 的便捷方法，
 * 底层委托给 common 模块的 {@link SxwlPrincipalUtils}。
 * </p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public final class SxwlSecurityUtils {

    private SxwlSecurityUtils() {
        throw new UnsupportedOperationException("SxwlSecurityUtils 工具类，不允许实例化");
    }

    /**
     * 获取当前登录用户（强转为 SxwlLoginUser）
     *
     * @return 当前登录用户；未登录时返回 empty
     */
    public static Optional<SxwlLoginUser> getCurrentUser() {
        return SxwlPrincipalUtils.getCurrentPrincipal()
                .filter(principal -> principal instanceof SxwlLoginUser)
                .map(principal -> (SxwlLoginUser) principal);
    }

    /**
     * 获取当前登录用户 ID
     *
     * @return 用户 ID；未登录时返回 null
     */
    public static Long getCurrentUserId() {
        return getCurrentUser().map(SxwlLoginUser::getUserId).orElse(null);
    }

    /**
     * 获取当前登录用户名
     *
     * @return 用户名；未登录时返回 null
     */
    public static String getCurrentUsername() {
        return getCurrentUser().map(SxwlLoginUser::getUsername).orElse(null);
    }
}

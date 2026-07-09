package com.sxwl.common.utils;

import com.sxwl.common.principal.SxwlPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * 从 Spring Security 上下文获取当前登录用户身份的工具类
 *
 * <p>封装了从 {@link SecurityContextHolder} 读取 Authentication 并安全转换为
 * {@link SxwlPrincipal} 的逻辑。mybatis 拦截器、redis 模块、security 模块、auth 模块
 * 等所有需要获取当前用户的场景均可复用。</p>
 *
 * <p><b>注意：</b>本工具类放在 common 模块（而非 mybatis 或 security），
 * 使所有模块无需额外依赖即可获取当前用户身份。</p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public final class SxwlPrincipalUtils {

    private SxwlPrincipalUtils() {
        throw new UnsupportedOperationException("SxwlPrincipalUtils 工具类，不允许实例化");
    }

    /**
     * 从 Spring Security 上下文中获取当前登录用户身份
     *
     * @return 当前用户身份；未登录 / 非 SxwlPrincipal 实例时返回 empty
     */
    public static Optional<SxwlPrincipal> getCurrentPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            return Optional.empty();
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof SxwlPrincipal p) {
            return Optional.of(p);
        }
        return Optional.empty();
    }
}

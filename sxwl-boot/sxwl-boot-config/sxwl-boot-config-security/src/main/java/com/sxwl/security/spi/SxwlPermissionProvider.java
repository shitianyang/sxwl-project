package com.sxwl.security.spi;

import com.sxwl.security.model.SxwlLoginUser;

/**
 * 权限校验 SPI 接口
 * <p>
 * 由 module-system 实现（SysPermissionProviderImpl），负责判断当前用户是否拥有指定权限。
 * </p>
 *
 * <h3>使用场景</h3>
 * <ul>
 *   <li>{@code @PreAuthorize("@perm.has('system:user:list')")} → 调用 {@link #hasPermission}</li>
 *   <li>超管绕过逻辑 → 调用 {@link #isSuperAdmin}</li>
 * </ul>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public interface SxwlPermissionProvider {

    /**
     * 判断当前用户是否拥有指定权限
     *
     * @param loginUser  当前登录用户
     * @param permission 权限标识（如 "system:user:list"）
     * @return true=有权限
     */
    boolean hasPermission(SxwlLoginUser loginUser, String permission);

    /**
     * 判断当前用户是否为超级管理员
     *
     * @param loginUser 当前登录用户
     * @return true=超管（绕过所有权限检查）
     */
    boolean isSuperAdmin(SxwlLoginUser loginUser);
}

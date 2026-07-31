package com.sxwl.common.entity;

import java.util.Set;

/**
 * 当前登录用户的权限 + 角色 VO
 *
 * <p>返回给前端用于按钮级权限校验。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public class SxwlUserPermissionVO {

    /** 权限标识集合（如 system:user:add, system:user:edit） */
    private Set<String> permissions;

    /** 角色编码集合（如 admin） */
    private Set<String> roles;

    public SxwlUserPermissionVO() {
    }

    public SxwlUserPermissionVO(Set<String> permissions, Set<String> roles) {
        this.permissions = permissions;
        this.roles = roles;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}

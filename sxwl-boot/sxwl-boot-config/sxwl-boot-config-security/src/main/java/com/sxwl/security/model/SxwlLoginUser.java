package com.sxwl.security.model;

import com.sxwl.common.principal.SxwlPrincipal;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * 认证用户对象
 * <p>
 * 存入 SecurityContext 的用户主体，实现 {@link SxwlPrincipal} 接口。
 * <b>不含密码</b>——防止序列化到 Redis 或日志时意外泄露。
 * </p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public class SxwlLoginUser implements SxwlPrincipal, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 用户 ID */
    private Long userId;

    /** 用户名（登录账号） */
    private String username;

    /** 用户昵称 */
    private String nickname;

    /** 账号状态：0=禁用 1=启用 */
    private Integer status;

    /** 角色编码集合 */
    private Set<String> roles;

    /** 权限标识集合 */
    private Set<String> perms;

    /** 所属主组织 ID */
    private Long createOrg;

    /** 数据范围类型 */
    private Integer dataScope;

    /** 数据范围可见组织 ID 集合 */
    private Set<Long> dataScopeOrgIds;

    public SxwlLoginUser() {
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Set<String> getPerms() {
        return perms;
    }

    public void setPerms(Set<String> perms) {
        this.perms = perms;
    }

    @Override
    public Long getOrgId() {
        return createOrg;
    }

    public Long getCreateOrg() {
        return createOrg;
    }

    public void setCreateOrg(Long createOrg) {
        this.createOrg = createOrg;
    }

    public Integer getDataScope() {
        return dataScope;
    }

    public void setDataScope(Integer dataScope) {
        this.dataScope = dataScope;
    }

    public Set<Long> getDataScopeOrgIds() {
        return dataScopeOrgIds;
    }

    public void setDataScopeOrgIds(Set<Long> dataScopeOrgIds) {
        this.dataScopeOrgIds = dataScopeOrgIds;
    }
}

package com.sxwl.system.model.entity;

import com.sxwl.common.entity.SxwlBasicField;

/**
 * 系统角色实体
 *
 * <p>对应数据库 {@code sys_role_info} 表。</p>
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
public class SysRole extends SxwlBasicField {

    /** 角色编码 */
    private String roleCode;

    /** 角色名称 */
    private String roleName;

    /** 数据权限范围：1=全部 2=本组织 3=本组织及下级 4=仅本人 5=自定义 */
    private Integer dataScope;

    /** 排序号 */
    private Integer sort;

    /** 状态：0=禁用 1=启用 */
    private Integer status;

    /** 描述说明 */
    private String description;

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Integer getDataScope() {
        return dataScope;
    }

    public void setDataScope(Integer dataScope) {
        this.dataScope = dataScope;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

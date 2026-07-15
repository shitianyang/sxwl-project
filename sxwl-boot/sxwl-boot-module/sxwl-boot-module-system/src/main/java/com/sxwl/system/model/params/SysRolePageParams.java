package com.sxwl.system.model.params;

import com.sxwl.common.entity.SxwlPageField;

/**
 * 系统角色-分页查询参数
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
public class SysRolePageParams extends SxwlPageField {

    /** 角色编码（模糊匹配） */
    private String roleCode;

    /** 角色名称（模糊匹配） */
    private String roleName;

    /** 状态：0=禁用 1=启用 */
    private Integer status;

    public SysRolePageParams() {
        setCurrent(1);
        setPageSize(10);
    }

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

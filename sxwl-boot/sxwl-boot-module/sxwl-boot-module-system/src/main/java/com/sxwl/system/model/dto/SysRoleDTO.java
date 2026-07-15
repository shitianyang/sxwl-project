package com.sxwl.system.model.dto;

/**
 * 系统角色 DTO（统一请求/响应）
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
public class SysRoleDTO {

    /** 角色 ID */
    private Long id;

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

    /** 创建时间（仅列表返回时填充） */
    private String createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}

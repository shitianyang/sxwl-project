package com.sxwl.system.model.entity;

import com.sxwl.common.entity.SxwlBasicField;

/**
 * 系统组织实体
 *
 * <p>对应数据库 {@code sys_organization_info} 表。</p>
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
public class SysOrganization extends SxwlBasicField {

    /** 组织编码 */
    private String orgCode;

    /** 组织名称 */
    private String orgName;

    /** 父组织 ID（根组织为 0） */
    private Long parentId;

    /** 祖先路径 */
    private String ancestors;

    /** 层级：1=公司 2=部门 3=小组 */
    private Integer orgLevel;

    /** 组织类型（关联字典 detail_value） */
    private String orgType;

    /** 负责人 ID */
    private Long leaderId;

    /** 组织联系电话 */
    private String phone;

    /** 排序号 */
    private Integer sort;

    /** 状态：0=禁用 1=启用 */
    private Integer status;

    /** 描述说明 */
    private String description;

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getAncestors() {
        return ancestors;
    }

    public void setAncestors(String ancestors) {
        this.ancestors = ancestors;
    }

    public Integer getOrgLevel() {
        return orgLevel;
    }

    public void setOrgLevel(Integer orgLevel) {
        this.orgLevel = orgLevel;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public Long getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(Long leaderId) {
        this.leaderId = leaderId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

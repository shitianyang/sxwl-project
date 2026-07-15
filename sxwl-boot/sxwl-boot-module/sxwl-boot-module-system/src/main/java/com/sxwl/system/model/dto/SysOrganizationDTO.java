package com.sxwl.system.model.dto;

import com.sxwl.common.entity.SxwlTreeNode;

import java.util.List;

/**
 * 系统组织 DTO（统一请求/响应 + 树形结构）
 *
 * <p>实现 {@link SxwlTreeNode} 接口，可使用 {@link com.sxwl.common.utils.SxwlTreeUtils#buildTree(List)}
 * 构建树结构。</p>
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
public class SysOrganizationDTO implements SxwlTreeNode<SysOrganizationDTO> {

    /** 组织 ID */
    private Long id;

    /** 组织编码 */
    private String orgCode;

    /** 组织名称 */
    private String orgName;

    /** 父组织 ID */
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

    /** 子组织列表（树结构） */
    private List<SysOrganizationDTO> children;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    @Override
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

    @Override
    public Integer getSortValue() {
        return sort != null ? sort : 0;
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

    @Override
    public List<SysOrganizationDTO> getChildren() {
        return children;
    }

    @Override
    public void setChildren(List<SysOrganizationDTO> children) {
        this.children = children;
    }
}

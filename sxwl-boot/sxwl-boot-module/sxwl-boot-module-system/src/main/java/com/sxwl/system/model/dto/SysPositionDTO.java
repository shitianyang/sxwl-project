package com.sxwl.system.model.dto;

/**
 * 系统岗位 DTO（统一请求/响应）
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
public class SysPositionDTO {

    /** 岗位 ID */
    private Long id;

    /** 岗位编码 */
    private String positionCode;

    /** 岗位名称 */
    private String positionName;

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

    public String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }

    public String getPositionName() {
        return positionName;
    }

    public void setPositionName(String positionName) {
        this.positionName = positionName;
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

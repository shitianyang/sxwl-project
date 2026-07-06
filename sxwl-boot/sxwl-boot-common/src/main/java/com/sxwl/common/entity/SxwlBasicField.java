package com.sxwl.common.entity;

import com.sxwl.common.constants.SxwlSystemConstants;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 统一基础字段
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public class SxwlBasicField implements Serializable {

    @Serial
    private static final long serialVersionUID = SxwlSystemConstants.SERIAL_VERSION_UID;

    /**
     * 唯一ID，主键
     */
    private Long id;

    /**
     * 创建人标识
     */
    private Long createdBy;

    /**
     * 创建人所属组织标识
     */
    private Long createOrg;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新人标识
     */
    private Long updateBy;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 删除标志：0=正常 1=已删除
     */
    private Integer deleteFlag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getCreateOrg() {
        return createOrg;
    }

    public void setCreateOrg(Long createOrg) {
        this.createOrg = createOrg;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Long updateBy) {
        this.updateBy = updateBy;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(Integer deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public SxwlBasicField() {

    }

    public SxwlBasicField(Long id, Long createdBy, Long createOrg, LocalDateTime createTime, Long updateBy, LocalDateTime updateTime, Integer deleteFlag) {
        this.id = id;
        this.createdBy = createdBy;
        this.createOrg = createOrg;
        this.createTime = createTime;
        this.updateBy = updateBy;
        this.updateTime = updateTime;
        this.deleteFlag = deleteFlag;
    }

    @Override
    public String toString() {
        return "SxwlBasicField{" +
                "id=" + id +
                ", createdBy=" + createdBy +
                ", createOrg=" + createOrg +
                ", createTime=" + createTime +
                ", updateBy=" + updateBy +
                ", updateTime=" + updateTime +
                ", deleteFlag=" + deleteFlag +
                '}';
    }
}

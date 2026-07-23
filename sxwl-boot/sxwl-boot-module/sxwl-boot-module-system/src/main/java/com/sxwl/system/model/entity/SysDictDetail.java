package com.sxwl.system.model.entity;

import com.sxwl.common.entity.SxwlBasicField;

/**
 * 系统字典明细实体
 *
 * <p>对应数据库 {@code sys_dict_detail_info} 表。</p>
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
public class SysDictDetail extends SxwlBasicField {

    /** 所属字典 ID */
    private Long dictId;

    /** 字典项值 */
    private String detailValue;

    /** 字典项标签 */
    private String detailLabel;

    /** 描述说明 */
    private String description;

    /** 排序号 */
    private Integer sort;

    /** 状态：0=禁用 1=启用 */
    private Integer status;

    /** 是否默认选中：0=否 1=是 */
    private Integer isDefault;

    public Long getDictId() {
        return dictId;
    }

    public void setDictId(Long dictId) {
        this.dictId = dictId;
    }

    public String getDetailValue() {
        return detailValue;
    }

    public void setDetailValue(String detailValue) {
        this.detailValue = detailValue;
    }

    public String getDetailLabel() {
        return detailLabel;
    }

    public void setDetailLabel(String detailLabel) {
        this.detailLabel = detailLabel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }
}

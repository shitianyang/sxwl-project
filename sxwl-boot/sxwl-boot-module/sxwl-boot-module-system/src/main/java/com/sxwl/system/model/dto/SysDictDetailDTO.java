package com.sxwl.system.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 系统字典明细 DTO（统一请求/响应）
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
public class SysDictDetailDTO {

    /** 明细 ID */
    private Long id;

    /** 所属字典 ID */
    @NotNull(message = "所属字典不能为空")
    private Long dictId;

    /** 字典项值 */
    @NotBlank(message = "字典项值不能为空")
    private String detailValue;

    /** 字典项标签 */
    @NotBlank(message = "字典项标签不能为空")
    private String detailLabel;

    /** 描述说明 */
    private String description;

    /** 排序号 */
    private Integer sort;

    /** 状态：0=禁用 1=启用 */
    @NotNull(message = "字典明细状态不能为空")
    private Integer status;

    /** 是否默认选中：0=否 1=是 */
    private Integer isDefault;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

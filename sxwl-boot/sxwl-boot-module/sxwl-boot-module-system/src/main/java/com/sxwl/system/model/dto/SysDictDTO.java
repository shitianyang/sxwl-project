package com.sxwl.system.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 系统字典 DTO（统一请求/响应）
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
public class SysDictDTO {

    /** 字典 ID */
    private Long id;

    /** 字典编码 */
    @NotBlank(message = "字典编码不能为空")
    private String dictCode;

    /** 字典名称 */
    @NotBlank(message = "字典名称不能为空")
    private String dictName;

    /** 描述说明 */
    private String description;

    /** 状态：0=禁用 1=启用 */
    @NotNull(message = "字典状态不能为空")
    private Integer status;

    /** 创建时间（仅列表返回时填充） */
    private String createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDictCode() {
        return dictCode;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}

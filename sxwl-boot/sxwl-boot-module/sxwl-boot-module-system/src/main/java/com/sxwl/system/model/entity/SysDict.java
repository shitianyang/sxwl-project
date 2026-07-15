package com.sxwl.system.model.entity;

import com.sxwl.common.entity.SxwlBasicField;

/**
 * 系统字典实体
 *
 * <p>对应数据库 {@code sys_dict_info} 表。</p>
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
public class SysDict extends SxwlBasicField {

    /** 字典编码 */
    private String dictCode;

    /** 字典名称 */
    private String dictName;

    /** 描述说明 */
    private String description;

    /** 状态：0=禁用 1=启用 */
    private Integer status;

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
}

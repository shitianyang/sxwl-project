package com.sxwl.system.model.params;

import com.sxwl.common.entity.SxwlPageField;

/**
 * 系统字典-分页查询参数
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
public class SysDictPageParams extends SxwlPageField {

    /** 字典编码（模糊匹配） */
    private String dictCode;

    /** 字典名称（模糊匹配） */
    private String dictName;

    /** 状态：0=禁用 1=启用 */
    private Integer status;

    public SysDictPageParams() {
        setCurrent(1);
        setPageSize(10);
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

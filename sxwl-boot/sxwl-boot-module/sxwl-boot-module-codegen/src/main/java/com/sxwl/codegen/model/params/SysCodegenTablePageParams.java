package com.sxwl.codegen.model.params;

import com.sxwl.common.entity.SxwlPageField;

/**
 * 代码生成-表配置分页查询参数
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public class SysCodegenTablePageParams extends SxwlPageField {

    /** 表名（模糊匹配） */
    private String tableName;

    /** 业务中文名（模糊匹配） */
    private String bizNameCn;

    /** 状态：0=禁用 1=启用 */
    private Integer status;

    public SysCodegenTablePageParams() {
        setCurrent(1);
        setPageSize(10);
    }

    public String getTableName() { return tableName; }

    public void setTableName(String tableName) { this.tableName = tableName; }

    public String getBizNameCn() { return bizNameCn; }

    public void setBizNameCn(String bizNameCn) { this.bizNameCn = bizNameCn; }

    public Integer getStatus() { return status; }

    public void setStatus(Integer status) { this.status = status; }
}

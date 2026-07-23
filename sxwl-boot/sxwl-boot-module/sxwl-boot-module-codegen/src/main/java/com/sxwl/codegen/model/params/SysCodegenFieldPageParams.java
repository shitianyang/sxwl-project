package com.sxwl.codegen.model.params;

import com.sxwl.common.entity.SxwlPageField;

/**
 * 代码生成-字段配置分页查询参数
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public class SysCodegenFieldPageParams extends SxwlPageField {

    /** 关联表 ID */
    private Long tableId;

    /** 字段名（模糊匹配） */
    private String columnName;

    public SysCodegenFieldPageParams() {
        setCurrent(1);
        setPageSize(50);
    }

    public Long getTableId() { return tableId; }

    public void setTableId(Long tableId) { this.tableId = tableId; }

    public String getColumnName() { return columnName; }

    public void setColumnName(String columnName) { this.columnName = columnName; }
}

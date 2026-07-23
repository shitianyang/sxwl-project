package com.sxwl.codegen.model.dto;

import java.util.List;

/**
 * 代码生成-表信息配置 DTO
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public class SysCodegenTableDTO {

    /** 表配置 ID */
    private Long id;

    /** 待生成的数据库表名 */
    private String tableName;

    /** 模块前缀 */
    private String modulePrefix;

    /** 业务名（英文单数） */
    private String bizName;

    /** 业务中文名 */
    private String bizNameCn;

    /** 业务名（英文复数） */
    private String bizNamePlural;

    /** 表注释 */
    private String tableComment;

    /** 包名 */
    private String packageName;

    /** 作者 */
    private String author;

    /** 生成类型：crud / tree */
    private String genType;

    /** 状态：0=禁用 1=启用 */
    private Integer status;

    /** 创建时间 */
    private String createTime;

    /** 字段配置列表 */
    private List<SysCodegenFieldDTO> fields;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getTableName() { return tableName; }

    public void setTableName(String tableName) { this.tableName = tableName; }

    public String getModulePrefix() { return modulePrefix; }

    public void setModulePrefix(String modulePrefix) { this.modulePrefix = modulePrefix; }

    public String getBizName() { return bizName; }

    public void setBizName(String bizName) { this.bizName = bizName; }

    public String getBizNameCn() { return bizNameCn; }

    public void setBizNameCn(String bizNameCn) { this.bizNameCn = bizNameCn; }

    public String getBizNamePlural() { return bizNamePlural; }

    public void setBizNamePlural(String bizNamePlural) { this.bizNamePlural = bizNamePlural; }

    public String getTableComment() { return tableComment; }

    public void setTableComment(String tableComment) { this.tableComment = tableComment; }

    public String getPackageName() { return packageName; }

    public void setPackageName(String packageName) { this.packageName = packageName; }

    public String getAuthor() { return author; }

    public void setAuthor(String author) { this.author = author; }

    public String getGenType() { return genType; }

    public void setGenType(String genType) { this.genType = genType; }

    public Integer getStatus() { return status; }

    public void setStatus(Integer status) { this.status = status; }

    public String getCreateTime() { return createTime; }

    public void setCreateTime(String createTime) { this.createTime = createTime; }

    public List<SysCodegenFieldDTO> getFields() { return fields; }

    public void setFields(List<SysCodegenFieldDTO> fields) { this.fields = fields; }
}

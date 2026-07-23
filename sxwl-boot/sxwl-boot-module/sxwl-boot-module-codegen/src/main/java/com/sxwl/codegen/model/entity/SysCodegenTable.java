package com.sxwl.codegen.model.entity;

import com.sxwl.common.entity.SxwlBasicField;

/**
 * 代码生成-表信息配置
 *
 * <p>记录用户手动配置的待生成表的基本信息。</p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public class SysCodegenTable extends SxwlBasicField {

    /** 待生成的数据库表名，如 sys_role_info */
    private String tableName;

    /** 模块前缀，如 system */
    private String modulePrefix;

    /** 业务名（英文单数），如 Role */
    private String bizName;

    /** 业务中文名，如 角色 */
    private String bizNameCn;

    /** 业务名（英文复数），如 Roles */
    private String bizNamePlural;

    /** 表注释 */
    private String tableComment;

    /** 包名，如 com.sxwl.system */
    private String packageName;

    /** 作者 */
    private String author;

    /** 生成类型：crud / tree */
    private String genType;

    /** 状态：0=禁用 1=启用 */
    private Integer status;

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
}

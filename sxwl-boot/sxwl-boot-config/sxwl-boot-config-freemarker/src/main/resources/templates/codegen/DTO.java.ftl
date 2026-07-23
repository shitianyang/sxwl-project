package ${packageName}.model.dto;

/**
 * ${tableComment} DTO
 *
 * <p>代码生成器自动创建</p>
 *
 * @author ${author}
 * @since 0.1.0
 */
public class ${bizName}DTO {

<#list fields as field>
    /** ${field.columnComment} */
    private ${field.javaType} ${field.javaFieldName};

</#list>
    /** 创建时间 */
    private String createTime;

<#list fields as field>
    public ${field.javaType} get${field.javaFieldName?cap_first}() { return ${field.javaFieldName}; }

    public void set${field.javaFieldName?cap_first}(${field.javaType} ${field.javaFieldName}) { this.${field.javaFieldName} = ${field.javaFieldName}; }

</#list>
    public String getCreateTime() { return createTime; }

    public void setCreateTime(String createTime) { this.createTime = createTime; }
}

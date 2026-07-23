package ${packageName}.model.entity;

import com.sxwl.common.entity.SxwlBasicField;

/**
 * ${tableComment}
 *
 * <p>代码生成器自动创建</p>
 *
 * @author ${author}
 * @since 0.1.0
 */
public class ${bizName} extends SxwlBasicField {

<#list fields as field>
<#if !field.isPk?? || field.isPk == 0>
    /** ${field.columnComment} */
    private ${field.javaType} ${field.javaFieldName};

</#if>
</#list>
<#list fields as field>
<#if !field.isPk?? || field.isPk == 0>
    public ${field.javaType} get${field.javaFieldName?cap_first}() { return ${field.javaFieldName}; }

    public void set${field.javaFieldName?cap_first}(${field.javaType} ${field.javaFieldName}) { this.${field.javaFieldName} = ${field.javaFieldName}; }

</#if>
</#list>
}

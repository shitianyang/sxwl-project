package ${packageName}.model.params;

import com.sxwl.common.entity.SxwlPageField;

/**
 * ${tableComment} 分页查询参数
 *
 * <p>代码生成器自动创建</p>
 *
 * @author ${author}
 * @since 0.1.0
 */
public class ${bizName}PageParams extends SxwlPageField {

<#list fields as field>
<#if field.isQuery?? && field.isQuery == 1>
    /** ${field.columnComment} */
    private ${field.javaType} ${field.javaFieldName};

</#if>
</#list>
    public ${bizName}PageParams() {
        setCurrent(1);
        setPageSize(10);
    }

<#list fields as field>
<#if field.isQuery?? && field.isQuery == 1>
    public ${field.javaType} get${field.javaFieldName?cap_first}() { return ${field.javaFieldName}; }

    public void set${field.javaFieldName?cap_first}(${field.javaType} ${field.javaFieldName}) { this.${field.javaFieldName} = ${field.javaFieldName}; }

</#if>
</#list>
}

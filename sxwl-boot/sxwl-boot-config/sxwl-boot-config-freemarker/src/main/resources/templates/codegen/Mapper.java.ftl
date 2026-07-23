package ${packageName}.mapper;

import ${packageName}.model.dto.${bizName}DTO;
import ${packageName}.model.entity.${bizName};
import ${packageName}.model.params.${bizName}PageParams;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ${tableComment} Mapper
 *
 * <p>代码生成器自动创建</p>
 *
 * @author ${author}
 * @since 0.1.0
 */
@Mapper
public interface ${bizName}Mapper {

    /**
     * 分页查询列表
     */
    List<${bizName}DTO> get${bizNamePlural}PageByParams(${bizName}PageParams params);

    /**
     * 根据 ID 查询
     */
    ${bizName}DTO get${bizName}ById(@Param("id") Long id);

<#list fields as field>
<#if field.isUnique?? && field.isUnique == 1>
    /**
     * 校验${field.columnComment}是否唯一
     */
    int check${field.javaFieldName?cap_first}Unique(@Param("${field.javaFieldName}") ${field.javaType} ${field.javaFieldName},
                                                      @Param("excludeId") Long excludeId);

</#if>
</#list>
    /**
     * 新增
     */
    int insert${bizName}(${bizName} entity);

    /**
     * 修改
     */
    int update${bizName}(${bizName} entity);

    /**
     * 逻辑删除
     */
    int delete${bizName}ById(@Param("id") Long id);
}

<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${packageName}.mapper.${bizName}Mapper">

    <!-- 通用查询列 -->
    <sql id="select${bizName}Columns">
<#list fields as field>
<#if field.isPk?? && field.isPk == 1>
        ${field.columnName},
</#if>
</#list>
<#list fields as field>
<#if !field.isPk?? || field.isPk == 0>
        ${field.columnName},
</#if>
</#list>
        TO_CHAR(create_time, 'YYYY-MM-DD HH24:MI:SS') AS createTime
    </sql>

    <!-- 分页查询 -->
    <select id="get${bizNamePlural}PageByParams" resultType="${packageName}.model.dto.${bizName}DTO">
        SELECT <include refid="select${bizName}Columns"/>
        FROM ${tableName}
        WHERE delete_flag = 0
<#list fields as field>
<#if field.isQuery?? && field.isQuery == 1 && field.queryType == 'like'>
        <if test="${field.javaFieldName} != null and ${field.javaFieldName} != ''">
            AND ${field.columnName} LIKE CONCAT('%', #{${field.javaFieldName}}, '%')
        </if>
<#elseif field.isQuery?? && field.isQuery == 1 && field.queryType == 'eq'>
        <if test="${field.javaFieldName} != null">
            AND ${field.columnName} = #{${field.javaFieldName}}
        </if>
<#elseif field.isQuery?? && field.isQuery == 1 && field.queryType == 'between'>
        <if test="${field.javaFieldName} != null">
            AND ${field.columnName} = #{${field.javaFieldName}}
        </if>
</#if>
</#list>
        ORDER BY create_time DESC
    </select>

    <!-- 根据 ID 查询 -->
    <select id="get${bizName}ById" resultType="${packageName}.model.dto.${bizName}DTO">
        SELECT <include refid="select${bizName}Columns"/>
        FROM ${tableName}
        WHERE id = #{id}
        AND delete_flag = 0
    </select>

<#list fields as field>
<#if field.isUnique?? && field.isUnique == 1>
    <!-- 校验${field.columnComment}唯一 -->
    <select id="check${field.javaFieldName?cap_first}Unique" resultType="int">
        SELECT COUNT(1)
        FROM ${tableName}
        WHERE ${field.columnName} = #{${field.javaFieldName}}
        AND delete_flag = 0
        <if test="excludeId != null">
            AND id != #{excludeId}
        </if>
    </select>

</#if>
</#list>
    <!-- 新增 -->
    <insert id="insert${bizName}" parameterType="${packageName}.model.entity.${bizName}">
        INSERT INTO ${tableName} (
<#list fields as field>
            ${field.columnName},
</#list>
            create_by, create_org, create_time, delete_flag
        ) VALUES (
<#list fields as field>
            #{${field.javaFieldName}},
</#list>
            #{createBy}, #{createOrg}, #{createTime}, #{deleteFlag}
        )
    </insert>

    <!-- 修改 -->
    <update id="update${bizName}" parameterType="${packageName}.model.entity.${bizName}">
        UPDATE ${tableName}
        SET
<#list fields as field>
<#if field.isPk?? && field.isPk == 1>
            ${field.columnName} = #{${field.javaFieldName}},
</#if>
</#list>
<#list fields as field>
<#if !field.isPk?? || field.isPk == 0>
            ${field.columnName} = #{${field.javaFieldName}},
</#if>
</#list>
            update_by   = #{updateBy},
            update_time = #{updateTime}
        WHERE id = #{id}
    </update>

    <!-- 逻辑删除 -->
    <update id="delete${bizName}ById">
        UPDATE ${tableName}
        SET delete_flag = 1,
            update_by   = #{updateBy, jdbcType=BIGINT},
            update_time = #{updateTime, jdbcType=TIMESTAMP}
        WHERE id = #{id}
    </update>

</mapper>

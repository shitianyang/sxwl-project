<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${packageName}.mapper.${bizName}Mapper">

    <!--
        通用查询列
        <p>主键字段优先排列，其余字段次之，createTime 使用 TO_CHAR 格式化（PostgreSQL）。</p>
    -->
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

    <!--
        分页查询列表
        <p>包含数据权限过滤（@SxwlDataScope），逻辑删除过滤（delete_flag = 0）。</p>
    -->
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

    <!--
        根据 ID 查询（编辑回显）
        <p>不包含敏感信息（如密码），返回完整${bizNameCn}信息。</p>
    -->
    <select id="get${bizName}ById" resultType="${packageName}.model.dto.${bizName}DTO">
        SELECT <include refid="select${bizName}Columns"/>
        FROM ${tableName}
        WHERE id = #{id}
        AND delete_flag = 0
    </select>

<#list fields as field>
<#if field.isUnique?? && field.isUnique == 1>
    <!--
        校验${field.columnComment}唯一性
        <p>修改时使用 excludeId 排除自身，新增时 excludeId 为 null。</p>
    -->
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
    <!--
        新增${bizNameCn}
        <p>审计字段（createBy, createOrg, createTime）由 MyBatis SxwlAutoFillInterceptor 自动填充，deleteFlag 默认 0。</p>
    -->
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

    <!--
        修改${bizNameCn}
        <p>仅更新非主键字段，updateBy / updateTime 由 SxwlAutoFillInterceptor 自动填充。</p>
    -->
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

    <!--
        逻辑删除${bizNameCn}
        <p>设置 delete_flag = 1，updateBy / updateTime 由 SxwlAutoFillInterceptor 自动填充。</p>
    -->
    <update id="delete${bizName}ById">
        UPDATE ${tableName}
        SET delete_flag = 1,
            update_by   = #{updateBy, jdbcType=BIGINT},
            update_time = #{updateTime, jdbcType=TIMESTAMP}
        WHERE id = #{id}
    </update>

    <!--
        批量逻辑删除${bizNameCn}
        <p>一次性更新多个记录的 delete_flag = 1，updateBy / updateTime 由 SxwlAutoFillInterceptor 自动填充。</p>
    -->
    <update id="batchDelete${bizName}ByIds">
        UPDATE ${tableName}
        SET delete_flag = 1,
            update_by   = #{updateBy, jdbcType=BIGINT},
            update_time = #{updateTime, jdbcType=TIMESTAMP}
        WHERE id IN
        <foreach collection="ids" item="id" open="(" separator="," close=")">
            #{id}
        </foreach>
    </update>

</mapper>

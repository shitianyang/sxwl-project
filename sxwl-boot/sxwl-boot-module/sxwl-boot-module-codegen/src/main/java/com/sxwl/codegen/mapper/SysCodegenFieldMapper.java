package com.sxwl.codegen.mapper;

import com.sxwl.codegen.model.dto.SysCodegenFieldDTO;
import com.sxwl.codegen.model.entity.SysCodegenField;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 代码生成-字段配置 Mapper
 *
 * <p>所有 SQL 定义在 {@code mapper/SysCodegenFieldMapper.xml} 中。</p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@Mapper
public interface SysCodegenFieldMapper {

    /**
     * 根据表 ID 查询字段配置列表
     */
    List<SysCodegenFieldDTO> getFieldsByTableId(@Param("tableId") Long tableId);

    /**
     * 批量新增字段配置
     */
    int batchInsertFields(@Param("fields") List<SysCodegenField> fields);

    /**
     * 根据表 ID 删除所有字段配置
     */
    int deleteFieldsByTableId(@Param("tableId") Long tableId);
}

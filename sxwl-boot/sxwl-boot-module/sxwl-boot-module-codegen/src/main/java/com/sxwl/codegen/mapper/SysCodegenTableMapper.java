package com.sxwl.codegen.mapper;

import com.sxwl.codegen.model.dto.SysCodegenTableDTO;
import com.sxwl.codegen.model.entity.SysCodegenTable;
import com.sxwl.codegen.model.params.SysCodegenTablePageParams;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 代码生成-表配置 Mapper
 *
 * <p>所有 SQL 定义在 {@code mapper/SysCodegenTableMapper.xml} 中。</p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@Mapper
public interface SysCodegenTableMapper {

    /**
     * 分页查询表配置列表
     */
    List<SysCodegenTableDTO> getTablePageByParams(SysCodegenTablePageParams params);

    /**
     * 根据 ID 查询表配置
     */
    SysCodegenTableDTO getTableById(@Param("id") Long id);

    /**
     * 校验表名是否唯一
     */
    int checkTableNameUnique(@Param("tableName") String tableName,
                             @Param("excludeId") Long excludeId);

    /**
     * 新增表配置
     */
    int insertTable(SysCodegenTable entity);

    /**
     * 修改表配置
     */
    int updateTable(SysCodegenTable entity);

    /**
     * 逻辑删除表配置
     */
    int deleteTableById(@Param("id") Long id);
}

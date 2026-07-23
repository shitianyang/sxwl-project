package com.sxwl.system.mapper;

import com.sxwl.common.annotation.SxwlDataScope;
import com.sxwl.system.model.dto.SysDictDTO;
import com.sxwl.system.model.entity.SysDict;
import com.sxwl.system.model.params.SysDictPageParams;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统字典 Mapper
 *
 * <p>所有 SQL 定义在 {@code mapper/SysDictMapper.xml} 中。</p>
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
@Mapper
public interface SysDictMapper {

    /**
     * 根据 ID 查询字典
     *
     * @param id 字典 ID
     * @return 字典 DTO
     */
    SysDictDTO getDictById(@Param("id") Long id);

    /**
     * 分页查询字典列表
     *
     * @param params 分页查询参数
     * @return 字典 DTO 列表
     */
    @SxwlDataScope
    List<SysDictDTO> getDictPageByParams(SysDictPageParams params);

    /**
     * 校验字典编码是否唯一（排除指定 ID）
     *
     * @param dictCode  字典编码
     * @param excludeId 需要排除的字典 ID
     * @return >0 表示已存在
     */
    int checkDictCodeUnique(@Param("dictCode") String dictCode,
                            @Param("excludeId") Long excludeId);

    /**
     * 新增字典
     *
     * @param entity 字典实体
     * @return 影响行数
     */
    int insertDict(SysDict entity);

    /**
     * 修改字典
     *
     * @param entity 字典实体
     * @return 影响行数
     */
    int updateDict(SysDict entity);

    /**
     * 逻辑删除字典
     *
     * @param id 字典 ID
     * @return 影响行数
     */
    int deleteDictById(@Param("id") Long id);
}

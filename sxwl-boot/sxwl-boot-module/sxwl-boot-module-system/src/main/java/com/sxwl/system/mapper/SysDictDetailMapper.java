package com.sxwl.system.mapper;

import com.sxwl.system.model.dto.SysDictDetailDTO;
import com.sxwl.system.model.entity.SysDictDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统字典明细 Mapper
 *
 * <p>所有 SQL 定义在 {@code mapper/SysDictDetailMapper.xml} 中。</p>
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
@Mapper
public interface SysDictDetailMapper {

    /**
     * 根据 ID 查询明细
     *
     * @param id 明细 ID
     * @return 明细 DTO
     */
    SysDictDetailDTO getDetailById(@Param("id") Long id);

    /**
     * 根据字典 ID 查询所有明细（不分页，按 sort 升序）
     *
     * @param dictId 字典 ID
     * @return 明细 DTO 列表
     */
    List<SysDictDetailDTO> getDetailListByDictId(@Param("dictId") Long dictId);

    /**
     * 校验明细值是否唯一（排除指定 ID）
     *
     * @param detailValue 明细值
     * @param excludeId   需要排除的明细 ID
     * @return >0 表示已存在
     */
    int checkDetailValueUnique(@Param("detailValue") String detailValue,
                               @Param("excludeId") Long excludeId);

    /**
     * 新增明细
     *
     * @param entity 明细实体
     * @return 影响行数
     */
    int insertDetail(SysDictDetail entity);

    /**
     * 修改明细
     *
     * @param entity 明细实体
     * @return 影响行数
     */
    int updateDetail(SysDictDetail entity);

    /**
     * 逻辑删除明细
     *
     * @param id 明细 ID
     * @return 影响行数
     */
    int deleteDetailById(@Param("id") Long id);
}

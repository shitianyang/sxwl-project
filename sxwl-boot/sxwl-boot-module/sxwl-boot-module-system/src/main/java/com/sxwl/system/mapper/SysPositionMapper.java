package com.sxwl.system.mapper;

import com.sxwl.common.annotation.SxwlDataScope;
import com.sxwl.system.model.dto.SysPositionDTO;
import com.sxwl.system.model.entity.SysPosition;
import com.sxwl.system.model.params.SysPositionPageParams;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统岗位 Mapper
 *
 * <p>所有 SQL 定义在 {@code mapper/SysPositionMapper.xml} 中。</p>
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
@Mapper
public interface SysPositionMapper {

    /**
     * 根据 ID 查询岗位
     *
     * @param id 岗位 ID
     * @return 岗位 DTO
     */
    SysPositionDTO getPositionById(@Param("id") Long id);

    /**
     * 分页查询岗位列表
     *
     * @param params 分页查询参数
     * @return 岗位 DTO 列表
     */
    @SxwlDataScope
    List<SysPositionDTO> getPositionPageByParams(SysPositionPageParams params);

    /**
     * 校验岗位编码是否唯一（排除指定 ID）
     *
     * @param positionCode 岗位编码
     * @param excludeId    需要排除的岗位 ID（修改时传自身 ID，新增传 null）
     * @return >0 表示已存在
     */
    int checkPositionCodeUnique(@Param("positionCode") String positionCode,
                                @Param("excludeId") Long excludeId);

    /**
     * 新增岗位
     *
     * @param entity 岗位实体
     * @return 影响行数
     */
    int insertPosition(SysPosition entity);

    /**
     * 修改岗位
     *
     * @param entity 岗位实体
     * @return 影响行数
     */
    int updatePosition(SysPosition entity);

    /**
     * 逻辑删除岗位
     *
     * @param id 岗位 ID
     * @return 影响行数
     */
    int deletePositionById(@Param("id") Long id);

    /**
     * 批量逻辑删除岗位
     *
     * @param ids 岗位 ID 列表
     * @return 影响行数
     */
    int batchDeletePositionByIds(@Param("ids") List<Long> ids);
}

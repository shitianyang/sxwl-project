package com.sxwl.system.service;

import com.github.pagehelper.PageInfo;
import com.sxwl.system.model.dto.SysPositionDTO;
import com.sxwl.system.model.params.SysPositionPageParams;

import java.util.List;

/**
 * 系统岗位 Service 接口
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
public interface SysPositionService {

    /**
     * 根据 ID 查询岗位
     *
     * @param id 岗位 ID
     * @return 岗位 DTO，不存在返回 10004 异常
     */
    SysPositionDTO getPositionById(Long id);

    /**
     * 分页查询岗位列表
     *
     * @param params 分页查询参数
     * @return 分页岗位列表
     */
    PageInfo<SysPositionDTO> getPositionPageByParams(SysPositionPageParams params);

    /**
     * 新增岗位
     *
     * @param dto 岗位信息
     * @return 影响行数
     */
    int createPosition(SysPositionDTO dto);

    /**
     * 修改岗位
     *
     * @param dto 岗位信息（含 id）
     * @return 影响行数
     */
    int updatePosition(SysPositionDTO dto);

    /**
     * 删除岗位（逻辑删除）
     *
     * @param id 岗位 ID
     * @return 影响行数
     */
    int deletePositionById(Long id);

    /**
     * 批量删除岗位（逻辑删除）
     *
     * @param ids 岗位 ID 列表
     * @return 影响行数
     */
    int batchDeletePositionByIds(List<Long> ids);
}

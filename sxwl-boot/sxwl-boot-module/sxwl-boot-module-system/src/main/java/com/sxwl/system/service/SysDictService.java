package com.sxwl.system.service;

import com.github.pagehelper.PageInfo;
import com.sxwl.system.model.dto.SysDictDTO;
import com.sxwl.system.model.dto.SysDictDetailDTO;
import com.sxwl.system.model.params.SysDictPageParams;

import java.util.List;

/**
 * 系统字典 Service 接口
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
public interface SysDictService {

    // ==================== 字典主表 ====================

    /**
     * 根据 ID 查询字典
     *
     * @param id 字典 ID
     * @return 字典 DTO，不存在返回 10004 异常
     */
    SysDictDTO getDictById(Long id);

    /**
     * 分页查询字典列表
     *
     * @param params 分页查询参数
     * @return 分页字典列表
     */
    PageInfo<SysDictDTO> getDictPageByParams(SysDictPageParams params);

    /**
     * 新增字典
     *
     * @param dto 字典信息
     * @return 影响行数
     */
    int createDict(SysDictDTO dto);

    /**
     * 修改字典
     *
     * @param dto 字典信息（含 id）
     * @return 影响行数
     */
    int updateDict(SysDictDTO dto);

    /**
     * 删除字典（逻辑删除）
     *
     * @param id 字典 ID
     * @return 影响行数
     */
    int deleteDictById(Long id);

    // ==================== 字典明细 ====================

    /**
     * 根据字典 ID 查询所有明细
     *
     * @param dictId 字典 ID
     * @return 明细列表
     */
    List<SysDictDetailDTO> getDetailListByDictId(Long dictId);

    /**
     * 新增明细
     *
     * @param dto 明细信息
     * @return 影响行数
     */
    int createDetail(SysDictDetailDTO dto);

    /**
     * 修改明细
     *
     * @param dto 明细信息（含 id）
     * @return 影响行数
     */
    int updateDetail(SysDictDetailDTO dto);

    /**
     * 删除明细
     *
     * @param id 明细 ID
     * @return 影响行数
     */
    int deleteDetailById(Long id);
}

package com.sxwl.system.service;

import com.sxwl.system.model.dto.SysOrganizationDTO;

import java.util.List;

/**
 * 系统组织 Service 接口
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
public interface SysOrganizationService {

    /**
     * 根据 ID 查询组织
     *
     * @param id 组织 ID
     * @return 组织 DTO，不存在返回 10004 异常
     */
    SysOrganizationDTO getOrganizationById(Long id);

    /**
     * 查询组织树（不分页）
     *
     * @return 树形组织列表
     */
    List<SysOrganizationDTO> getOrganizationTree();

    /**
     * 查询所有组织（平铺列表）
     *
     * @return 平铺组织列表
     */
    List<SysOrganizationDTO> getAllOrganizationList();

    /**
     * 新增组织
     *
     * @param dto 组织信息
     * @return 影响行数
     */
    int createOrganization(SysOrganizationDTO dto);

    /**
     * 修改组织
     *
     * @param dto 组织信息（含 id）
     * @return 影响行数
     */
    int updateOrganization(SysOrganizationDTO dto);

    /**
     * 删除组织（逻辑删除）
     *
     * @param id 组织 ID
     * @return 影响行数
     */
    int deleteOrganizationById(Long id);
}

package com.sxwl.system.service;

import com.github.pagehelper.PageInfo;
import com.sxwl.system.model.dto.SysRoleDTO;
import com.sxwl.system.model.params.SysRolePageParams;

import java.util.List;

/**
 * 系统角色 Service 接口
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
public interface SysRoleService {

    // ==================== CRUD ====================

    /**
     * 根据 ID 查询角色
     *
     * @param id 角色 ID
     * @return 角色 DTO，不存在返回 10004 异常
     */
    SysRoleDTO getRoleById(Long id);

    /**
     * 分页查询角色列表
     *
     * @param params 分页查询参数
     * @return 分页角色列表
     */
    PageInfo<SysRoleDTO> getRolePageByParams(SysRolePageParams params);

    /**
     * 新增角色
     *
     * @param dto 角色信息
     * @return 影响行数
     */
    int createRole(SysRoleDTO dto);

    /**
     * 修改角色
     *
     * @param dto 角色信息（含 id）
     * @return 影响行数
     */
    int updateRole(SysRoleDTO dto);

    /**
     * 删除角色（逻辑删除）
     *
     * @param id 角色 ID
     * @return 影响行数
     */
    int deleteRoleById(Long id);

    // ==================== 菜单分配 ====================

    /**
     * 保存角色的菜单分配（先删后插）
     *
     * @param roleId  角色 ID
     * @param menuIds 菜单 ID 列表
     */
    void saveRoleMenus(Long roleId, List<Long> menuIds);

    /**
     * 查询角色已分配的菜单 ID 列表
     */
    List<Long> getMenuIdListByRoleId(Long roleId);

    // ==================== 数据权限 ====================

    /**
     * 保存角色的数据权限（先删后插）
     *
     * @param roleId 角色 ID
     * @param orgIds 组织 ID 列表
     */
    void saveRoleDataScope(Long roleId, List<Long> orgIds);

    /**
     * 查询角色已授权的组织 ID 列表
     */
    List<Long> getDataScopeOrgIdListByRoleId(Long roleId);
}

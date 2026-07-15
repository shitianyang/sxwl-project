package com.sxwl.system.mapper;

import com.sxwl.common.annotation.SxwlDataScope;
import com.sxwl.system.model.dto.SysRoleDTO;
import com.sxwl.system.model.entity.SysRole;
import com.sxwl.system.model.params.SysRolePageParams;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统角色 Mapper
 *
 * <p>所有 SQL 定义在 {@code mapper/SysRoleMapper.xml} 中。</p>
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
@Mapper
public interface SysRoleMapper {

    /**
     * 根据 ID 查询角色
     */
    SysRoleDTO getRoleById(@Param("id") Long id);

    /**
     * 分页查询角色列表
     */
    @SxwlDataScope
    List<SysRoleDTO> getRolePageByParams(SysRolePageParams params);

    /**
     * 校验角色编码是否唯一（排除指定 ID）
     */
    int checkRoleCodeUnique(@Param("roleCode") String roleCode,
                            @Param("excludeId") Long excludeId);

    /**
     * 新增角色
     */
    int insertRole(SysRole entity);

    /**
     * 修改角色
     */
    int updateRole(SysRole entity);

    /**
     * 逻辑删除角色
     */
    int deleteRoleById(@Param("id") Long id);

    // ==================== 角色-菜单关联 ====================

    /**
     * 查询角色已分配的菜单 ID 列表
     */
    List<Long> getMenuIdListByRoleId(@Param("roleId") Long roleId);

    /**
     * 删除角色所有菜单关联
     */
    int deleteRoleMenusByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量插入角色-菜单关联
     */
    int batchInsertRoleMenus(@Param("roleId") Long roleId,
                             @Param("menuIds") List<Long> menuIds,
                             @Param("ids") List<Long> ids,
                             @Param("createBy") Long createBy,
                             @Param("createOrg") Long createOrg,
                             @Param("createTime") java.util.Date createTime);

    // ==================== 角色-数据权限关联 ====================

    /**
     * 查询角色已授权的组织 ID 列表（数据权限）
     */
    List<Long> getDataScopeOrgIdListByRoleId(@Param("roleId") Long roleId);

    /**
     * 删除角色所有数据权限组织关联
     */
    int deleteRoleDataScopeByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量插入角色-数据权限组织关联
     */
    int batchInsertRoleDataScope(@Param("roleId") Long roleId,
                                  @Param("orgIds") List<Long> orgIds,
                                  @Param("ids") List<Long> ids,
                                  @Param("createBy") Long createBy,
                                  @Param("createOrg") Long createOrg,
                                  @Param("createTime") java.util.Date createTime);
}

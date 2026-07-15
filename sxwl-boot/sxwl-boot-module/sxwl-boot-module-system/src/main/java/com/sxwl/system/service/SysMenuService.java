package com.sxwl.system.service;

import com.sxwl.system.model.dto.SysMenuDTO;

import java.util.List;

/**
 * 系统菜单 Service 接口
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
public interface SysMenuService {

    /**
     * 根据 ID 查询菜单
     *
     * @param id 菜单 ID
     * @return 菜单 DTO，不存在返回 10004 异常
     */
    SysMenuDTO getMenuById(Long id);

    /**
     * 查询菜单树（不分页）
     *
     * @return 树形菜单列表
     */
    List<SysMenuDTO> getMenuTree();

    /**
     * 查询所有菜单（平铺列表）
     *
     * @return 平铺菜单列表
     */
    List<SysMenuDTO> getAllMenuList();

    /**
     * 新增菜单
     *
     * @param dto 菜单信息
     * @return 影响行数
     */
    int createMenu(SysMenuDTO dto);

    /**
     * 修改菜单
     *
     * @param dto 菜单信息（含 id）
     * @return 影响行数
     */
    int updateMenu(SysMenuDTO dto);

    /**
     * 删除菜单（逻辑删除）
     *
     * @param id 菜单 ID
     * @return 影响行数
     */
    int deleteMenuById(Long id);
}

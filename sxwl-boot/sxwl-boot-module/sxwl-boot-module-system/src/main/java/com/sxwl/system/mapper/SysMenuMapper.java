package com.sxwl.system.mapper;

import com.sxwl.system.model.dto.SysMenuDTO;
import com.sxwl.system.model.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统菜单 Mapper
 *
 * <p>所有 SQL 定义在 {@code mapper/SysMenuMapper.xml} 中。</p>
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
@Mapper
public interface SysMenuMapper {

    /**
     * 根据 ID 查询菜单
     *
     * @param id 菜单 ID
     * @return 菜单 DTO
     */
    SysMenuDTO getMenuById(@Param("id") Long id);

    /**
     * 查询所有菜单（按 sort 升序）
     *
     * @return 菜单 DTO 列表（平铺）
     */
    List<SysMenuDTO> selectAllMenus();

    /**
     * 统计指定父菜单下的子菜单数
     *
     * @param parentId 父菜单 ID
     * @return 子菜单数
     */
    int countChildrenByParentId(@Param("parentId") Long parentId);

    /**
     * 新增菜单
     *
     * @param entity 菜单实体
     * @return 影响行数
     */
    int insertMenu(SysMenu entity);

    /**
     * 修改菜单
     *
     * @param entity 菜单实体
     * @return 影响行数
     */
    int updateMenu(SysMenu entity);

    /**
     * 逻辑删除菜单
     *
     * @param id 菜单 ID
     * @return 影响行数
     */
    int deleteMenuById(@Param("id") Long id);
}

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
     * 校验权限标识是否唯一（排除指定 ID）
     *
     * @param perms     权限标识
     * @param excludeId 排除的菜单 ID（编辑时使用）
     * @return 冲突数量
     */
    int checkPermsUnique(@Param("perms") String perms,
                         @Param("excludeId") Long excludeId);

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

    /**
     * 根据用户 ID 查询有权限的菜单列表（按 sort 升序）
     * <p>通过角色-菜单关联获取当前用户可访问的所有菜单。</p>
     *
     * @param userId 用户 ID
     * @return 菜单 DTO 列表（平铺）
     */
    List<SysMenuDTO> selectMenusByUserId(@Param("userId") Long userId);
}

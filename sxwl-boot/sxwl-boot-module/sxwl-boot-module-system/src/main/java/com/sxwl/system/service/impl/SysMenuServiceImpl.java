package com.sxwl.system.service.impl;

import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.common.utils.SxwlDiffUtils;
import com.sxwl.common.utils.SxwlTreeUtils;
import com.sxwl.system.mapper.SysMenuMapper;
import com.sxwl.system.model.dto.SysMenuDTO;
import com.sxwl.system.model.entity.SysMenu;
import com.sxwl.system.service.SysMenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统菜单 Service 实现
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
@Service
public class SysMenuServiceImpl implements SysMenuService {

    private static final Logger log = LoggerFactory.getLogger(SysMenuServiceImpl.class);

    /** SysMenu Mapper */
    private final SysMenuMapper sysMenuMapper;

    public SysMenuServiceImpl(SysMenuMapper sysMenuMapper) {
        this.sysMenuMapper = sysMenuMapper;
    }

    /**
     * 根据 ID 查询菜单
     *
     * @param id 菜单 ID
     * @return 菜单 DTO，查不到抛 10004 异常
     */
    @Override
    public SysMenuDTO getMenuById(Long id) {
        SysMenuDTO dto = sysMenuMapper.getMenuById(id);
        if (dto == null) {
            throw new SxwlBusinessException(10004, "菜单不存在或已被删除");
        }
        return dto;
    }

    /**
     * 查询菜单树（不分页）
     *
     * @return 树形菜单列表
     */
    @Override
    public List<SysMenuDTO> getMenuTree() {
        List<SysMenuDTO> allMenus = sysMenuMapper.selectAllMenus();
        return SxwlTreeUtils.buildTree(allMenus);
    }

    /**
     * 查询所有菜单（平铺列表）
     *
     * @return 平铺菜单列表
     */
    @Override
    public List<SysMenuDTO> getAllMenuList() {
        return sysMenuMapper.selectAllMenus();
    }

    /**
     * 新增菜单
     *
     * @param dto 菜单 DTO
     * @return 影响行数
     * @throws SxwlBusinessException 父菜单不存在或新增失败时抛出
     */
    @Override
    public int createMenu(SysMenuDTO dto) {
        SysMenu entity = toEntity(dto);
        // 设置 ancestors
        if (dto.getParentId() != null && dto.getParentId() > 0) {
            SysMenuDTO parent = sysMenuMapper.getMenuById(dto.getParentId());
            if (parent == null) {
                throw new SxwlBusinessException(10004, "父菜单不存在");
            }
            entity.setAncestors(parent.getAncestors() + "," + dto.getParentId());
        } else {
            entity.setAncestors("0");
            entity.setParentId(0L);
        }

        // 校验权限标识唯一性
        if (entity.getPerms() != null && !entity.getPerms().isEmpty()) {
            int permsCount = sysMenuMapper.checkPermsUnique(entity.getPerms(), null);
            if (permsCount > 0) {
                throw new SxwlBusinessException(10001, "权限标识已存在");
            }
        }

        int result = sysMenuMapper.insertMenu(entity);
        if (result != 1) {
            log.error("新增菜单失败: menuName={}, result={}", dto.getMenuName(), result);
            throw new SxwlBusinessException(10001, "新增菜单失败");
        }
        log.info("新增菜单成功: menuName={}", dto.getMenuName());
        return result;
    }

    /**
     * 修改菜单
     * <p>如果 parentId 变了，自动更新 ancestors。</p>
     *
     * @param dto 菜单 DTO
     * @return 影响行数
     * @throws SxwlBusinessException 父菜单不存在或菜单不存在时抛出
     */
    @Override
    public int updateMenu(SysMenuDTO dto) {
        SysMenu entity = toEntity(dto);
        entity.setId(dto.getId());

        // 如果 parentId 变了，更新 ancestors
        SysMenuDTO old = sysMenuMapper.getMenuById(dto.getId());
        if (old != null && !java.util.Objects.equals(old.getParentId(), dto.getParentId())) {
            if (dto.getParentId() != null && dto.getParentId() > 0) {
                SysMenuDTO parent = sysMenuMapper.getMenuById(dto.getParentId());
                if (parent == null) {
                    throw new SxwlBusinessException(10004, "父菜单不存在");
                }
                entity.setAncestors(parent.getAncestors() + "," + dto.getParentId());
            } else {
                entity.setAncestors("0");
                entity.setParentId(0L);
            }
        } else if (old != null) {
            // parentId 没变，复用旧 ancestors，防止前端未传字段导致 SQL 写入 NULL
            entity.setAncestors(old.getAncestors());
        }

        // 计算字段级变更差异
        if (old != null) {
            SysMenu oldEntity = toEntity(old);
            String diffJson = SxwlDiffUtils.diff(oldEntity, entity);
            if (diffJson != null) {
                SxwlDiffUtils.setContextDiff(diffJson);
            }
        }

        // 校验权限标识唯一性
        if (entity.getPerms() != null && !entity.getPerms().isEmpty()) {
            int permsCount = sysMenuMapper.checkPermsUnique(entity.getPerms(), entity.getId());
            if (permsCount > 0) {
                throw new SxwlBusinessException(10001, "权限标识已存在");
            }
        }

        int result = sysMenuMapper.updateMenu(entity);
        if (result == 0) {
            throw new SxwlBusinessException(10004, "菜单不存在或已被删除");
        }
        log.info("修改菜单成功: id={}", dto.getId());
        return result;
    }

    /**
     * 删除菜单（逻辑删除）
     * <p>存在子菜单则不允许删除。</p>
     *
     * @param id 菜单 ID
     * @return 影响行数
     * @throws SxwlBusinessException 存在子菜单或菜单不存在时抛出
     */
    @Override
    public int deleteMenuById(Long id) {
        // 检查是否有子菜单
        int childCount = sysMenuMapper.countChildrenByParentId(id);
        if (childCount > 0) {
            throw new SxwlBusinessException(10001, "存在子菜单，不允许删除");
        }

        int affected = sysMenuMapper.deleteMenuById(id);
        if (affected == 0) {
            throw new SxwlBusinessException(10004, "菜单不存在或已被删除");
        }
        log.info("删除菜单成功: id={}", id);
        return affected;
    }

    /**
     * 查询当前用户有权访问的菜单树
     *
     * @param userId 用户 ID
     * @return 树形菜单列表
     */
    @Override
    public List<SysMenuDTO> getUserMenuTree(Long userId) {
        List<SysMenuDTO> allMenus = sysMenuMapper.selectMenusByUserId(userId);
        return SxwlTreeUtils.buildTree(allMenus);
    }

    // ==================== 私有方法 ====================

    /**
     * DTO 转实体
     *
     * @param dto 菜单 DTO
     * @return 菜单实体
     */
    private SysMenu toEntity(SysMenuDTO dto) {
        SysMenu entity = new SysMenu();
        entity.setMenuName(dto.getMenuName());
        entity.setParentId(dto.getParentId());
        entity.setAncestors(dto.getAncestors());
        entity.setMenuType(dto.getMenuType());
        entity.setPath(dto.getPath());
        entity.setComponent(dto.getComponent());
        entity.setPerms(dto.getPerms());
        entity.setIcon(dto.getIcon());
        entity.setIsFrame(dto.getIsFrame());
        entity.setIsCache(dto.getIsCache());
        entity.setSort(dto.getSort());
        entity.setVisible(dto.getVisible());
        entity.setStatus(dto.getStatus());
        entity.setDescription(dto.getDescription());
        return entity;
    }
}

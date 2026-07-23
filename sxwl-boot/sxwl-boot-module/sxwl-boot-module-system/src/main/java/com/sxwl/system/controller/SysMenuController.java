package com.sxwl.system.controller;

import com.sxwl.common.annotation.SxwlLog;
import com.sxwl.common.annotation.SxwlRepeatSubmit;
import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.system.model.dto.SysMenuDTO;
import com.sxwl.system.service.SysMenuService;
import com.sxwl.security.utils.SxwlSecurityUtils;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统菜单 Controller
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
@RestController
@RequestMapping("/system/menu")
public class SysMenuController {

    private final SysMenuService sysMenuService;

    public SysMenuController(SysMenuService sysMenuService) {
        this.sysMenuService = sysMenuService;
    }

    /**
     * 根据 ID 查询菜单（编辑回显）
     *
     * @param id 菜单 ID
     * @return 菜单信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:menu:query')")
    @SxwlLog(title = "菜单管理", description = "查询菜单详情[id=#{#id}]")
    public SysMenuDTO getMenuById(@PathVariable("id") Long id) {
        return sysMenuService.getMenuById(id);
    }

    /**
     * 查询菜单树（不分页）
     *
     * @return 树形菜单列表
     */
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:menu:list')")
    @SxwlLog(title = "菜单管理", description = "查询菜单树")
    public List<SysMenuDTO> getMenuTree() {
        return sysMenuService.getMenuTree();
    }

    /**
     * 查询所有菜单列表（平铺，用于上级菜单下拉框）
     *
     * @return 平铺菜单列表
     */
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:menu:list')")
    @SxwlLog(title = "菜单管理", description = "查询所有菜单")
    public List<SysMenuDTO> getAllMenuList() {
        return sysMenuService.getAllMenuList();
    }

    /**
     * 查询当前用户有权访问的菜单树（用于侧边栏渲染）
     * <p>无需特殊权限，任何已登录用户均可调用。</p>
     *
     * @return 树形菜单列表
     */
    @GetMapping("/user-tree")
    @SxwlLog(title = "菜单管理", description = "查询当前用户菜单树")
    public List<SysMenuDTO> getUserMenuTree() {
        Long userId = SxwlSecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new SxwlBusinessException(401, "未登录");
        }
        return sysMenuService.getUserMenuTree(userId);
    }

    /**
     * 新增菜单
     *
     * @param dto 菜单信息
     */
    @PostMapping
    @SxwlRepeatSubmit
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:menu:add')")
    @SxwlLog(title = "菜单管理", description = "新增菜单[#{#dto.menuName}]")
    public void createMenu(@Valid @RequestBody SysMenuDTO dto) {
        sysMenuService.createMenu(dto);
    }

    /**
     * 修改菜单
     *
     * @param dto 菜单信息（含 id）
     */
    @PutMapping
    @SxwlRepeatSubmit
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:menu:edit')")
    @SxwlLog(title = "菜单管理", description = "修改菜单[#{#dto.menuName}]")
    public void updateMenu(@Valid @RequestBody SysMenuDTO dto) {
        sysMenuService.updateMenu(dto);
    }

    /**
     * 删除菜单
     *
     * @param id 菜单 ID
     */
    @DeleteMapping("/{id}")
    @SxwlRepeatSubmit
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:menu:delete')")
    @SxwlLog(title = "菜单管理", description = "删除菜单[id=#{#id}]")
    public void deleteMenuById(@PathVariable("id") Long id) {
        sysMenuService.deleteMenuById(id);
    }
}

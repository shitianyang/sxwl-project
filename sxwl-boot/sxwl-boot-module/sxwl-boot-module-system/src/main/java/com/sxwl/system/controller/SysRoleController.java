package com.sxwl.system.controller;

import com.github.pagehelper.PageInfo;
import com.sxwl.common.annotation.SxwlLog;
import com.sxwl.system.model.dto.SysRoleDTO;
import com.sxwl.system.model.params.SysRolePageParams;
import com.sxwl.system.service.SysRoleService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 系统角色 Controller
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
@RestController
@RequestMapping("/system/role")
public class SysRoleController {

    private final SysRoleService sysRoleService;

    public SysRoleController(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

    // ==================== CRUD ====================

    /**
     * 根据 ID 查询角色（编辑回显）
     *
     * @param id 角色 ID
     * @return 角色信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:role:query')")
    @SxwlLog(title = "角色管理", description = "查询角色详情[id=#{#id}]")
    public SysRoleDTO getRoleById(@PathVariable("id") Long id) {
        return sysRoleService.getRoleById(id);
    }

    /**
     * 分页查询角色列表
     *
     * @param params 分页查询参数
     * @return 分页角色列表
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:role:list')")
    @SxwlLog(title = "角色管理", description = "查询角色列表")
    public PageInfo<SysRoleDTO> getRolePageByParams(@Valid SysRolePageParams params) {
        return sysRoleService.getRolePageByParams(params);
    }

    /**
     * 新增角色
     *
     * @param dto 角色信息
     */
    @PostMapping
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:role:add')")
    @SxwlLog(title = "角色管理", description = "新增角色[#{#dto.roleCode}]")
    public void createRole(@Valid @RequestBody SysRoleDTO dto) {
        sysRoleService.createRole(dto);
    }

    /**
     * 修改角色
     *
     * @param dto 角色信息（含 id）
     */
    @PutMapping
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:role:edit')")
    @SxwlLog(title = "角色管理", description = "修改角色[#{#dto.roleCode}]")
    public void updateRole(@Valid @RequestBody SysRoleDTO dto) {
        sysRoleService.updateRole(dto);
    }

    /**
     * 删除角色
     *
     * @param id 角色 ID
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:role:delete')")
    @SxwlLog(title = "角色管理", description = "删除角色[id=#{#id}]")
    public void deleteRoleById(@PathVariable("id") Long id) {
        sysRoleService.deleteRoleById(id);
    }

    // ==================== 菜单分配 ====================

    /**
     * 保存角色的菜单分配
     *
     * @param body { roleId, menuIds: Long[] }
     */
    @PostMapping("/{roleId}/menus")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:role:grant')")
    @SxwlLog(title = "角色管理", description = "分配菜单[roleId=#{#body.roleId}]")
    public void saveRoleMenus(@RequestBody Map<String, Object> body) {
        Long roleId = Long.valueOf(body.get("roleId").toString());
        @SuppressWarnings("unchecked")
        List<Long> menuIds = (List<Long>) body.get("menuIds");
        sysRoleService.saveRoleMenus(roleId, menuIds);
    }

    /**
     * 查询角色已分配的菜单 ID 列表
     *
     * @param roleId 角色 ID
     * @return 菜单 ID 列表
     */
    @GetMapping("/{roleId}/menus")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:role:query')")
    @SxwlLog(title = "角色管理", description = "查询角色菜单[roleId=#{#roleId}]")
    public List<Long> getMenuIdListByRoleId(@RequestParam("roleId") Long roleId) {
        return sysRoleService.getMenuIdListByRoleId(roleId);
    }

    // ==================== 数据权限 ====================

    /**
     * 保存角色的数据权限
     *
     * @param body { roleId, orgIds: Long[] }
     */
    @PostMapping("/{roleId}/data-scope")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:role:grant')")
    @SxwlLog(title = "角色管理", description = "配置数据权限[roleId=#{#body.roleId}]")
    public void saveRoleDataScope(@RequestBody Map<String, Object> body) {
        Long roleId = Long.valueOf(body.get("roleId").toString());
        @SuppressWarnings("unchecked")
        List<Long> orgIds = (List<Long>) body.get("orgIds");
        sysRoleService.saveRoleDataScope(roleId, orgIds);
    }

    /**
     * 查询角色已授权的组织 ID 列表（数据权限）
     *
     * @param roleId 角色 ID
     * @return 组织 ID 列表
     */
    @GetMapping("/{roleId}/data-scope")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:role:query')")
    @SxwlLog(title = "角色管理", description = "查询角色数据权限[roleId=#{#roleId}]")
    public List<Long> getDataScopeOrgIdListByRoleId(@PathVariable("roleId") Long roleId) {
        return sysRoleService.getDataScopeOrgIdListByRoleId(roleId);
    }
}

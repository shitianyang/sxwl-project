package com.sxwl.system.controller;

import com.sxwl.common.annotation.SxwlLog;
import com.sxwl.common.annotation.SxwlRepeatSubmit;
import com.sxwl.common.constant.SxwlPermConstant;
import com.sxwl.system.model.dto.SysOrganizationDTO;
import com.sxwl.system.service.SysOrganizationService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统组织 Controller
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
@RestController
@RequestMapping("/system/organization")
public class SysOrganizationController {

    private final SysOrganizationService sysOrganizationService;

    public SysOrganizationController(SysOrganizationService sysOrganizationService) {
        this.sysOrganizationService = sysOrganizationService;
    }

    /**
     * 根据 ID 查询组织（编辑回显）
     *
     * @param id 组织 ID
     * @return 组织信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('" + SxwlPermConstant.System.ORG_QUERY + "')")
    @SxwlLog(title = "组织管理", description = "查询组织详情[id=#{#id}]")
    public SysOrganizationDTO getOrganizationById(@PathVariable("id") Long id) {
        return sysOrganizationService.getOrganizationById(id);
    }

    /**
     * 查询组织树（不分页）
     *
     * @return 树形组织列表
     */
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('" + SxwlPermConstant.System.ORG_LIST + "')")
    @SxwlLog(title = "组织管理", description = "查询组织树")
    public List<SysOrganizationDTO> getOrganizationTree() {
        return sysOrganizationService.getOrganizationTree();
    }

    /**
     * 查询所有组织列表（平铺，用于上级组织下拉框）
     *
     * @return 平铺组织列表
     */
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('" + SxwlPermConstant.System.ORG_LIST + "')")
    @SxwlLog(title = "组织管理", description = "查询所有组织")
    public List<SysOrganizationDTO> getAllOrganizationList() {
        return sysOrganizationService.getAllOrganizationList();
    }

    /**
     * 新增组织
     *
     * @param dto 组织信息
     */
    @PostMapping
    @SxwlRepeatSubmit(interval = 3, message = "组织创建中，请稍候")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('" + SxwlPermConstant.System.ORG_ADD + "')")
    @SxwlLog(title = "组织管理", description = "新增组织[#{#dto.orgName}]")
    public void createOrganization(@Valid @RequestBody SysOrganizationDTO dto) {
        sysOrganizationService.createOrganization(dto);
    }

    /**
     * 修改组织
     *
     * @param dto 组织信息（含 id）
     */
    @PutMapping
    @SxwlRepeatSubmit(interval = 3, message = "组织修改中，请稍候")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('" + SxwlPermConstant.System.ORG_EDIT + "')")
    @SxwlLog(title = "组织管理", description = "修改组织[#{#dto.orgName}]")
    public void updateOrganization(@Valid @RequestBody SysOrganizationDTO dto) {
        sysOrganizationService.updateOrganization(dto);
    }

    /**
     * 删除组织
     *
     * @param id 组织 ID
     */
    @DeleteMapping("/{id}")
    @SxwlRepeatSubmit(interval = 3, message = "组织删除中，请稍候")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('" + SxwlPermConstant.System.ORG_DELETE + "')")
    @SxwlLog(title = "组织管理", description = "删除组织[id=#{#id}]")
    public void deleteOrganizationById(@PathVariable("id") Long id) {
        sysOrganizationService.deleteOrganizationById(id);
    }
}

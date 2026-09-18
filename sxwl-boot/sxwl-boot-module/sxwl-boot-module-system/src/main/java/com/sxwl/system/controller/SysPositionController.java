package com.sxwl.system.controller;

import com.github.pagehelper.PageInfo;
import com.sxwl.common.annotation.SxwlLog;
import com.sxwl.common.annotation.SxwlRepeatSubmit;
import com.sxwl.common.constant.SxwlPermConstant;
import com.sxwl.system.model.dto.SysPositionDTO;
import com.sxwl.system.model.params.SysPositionPageParams;
import com.sxwl.system.service.SysPositionService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统岗位 Controller
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
@RestController
@RequestMapping("/system/position")
public class SysPositionController {

    private final SysPositionService sysPositionService;

    public SysPositionController(SysPositionService sysPositionService) {
        this.sysPositionService = sysPositionService;
    }

    /**
     * 根据 ID 查询岗位（编辑回显）
     *
     * @param id 岗位 ID
     * @return 岗位信息
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority(" + SxwlPermConstant.System.POSITION_QUERY + ")")
    @SxwlLog(title = "岗位管理", description = "查询岗位详情[id=#{#id}]")
    public SysPositionDTO getPositionById(@PathVariable("id") Long id) {
        return sysPositionService.getPositionById(id);
    }

    /**
     * 分页查询岗位列表
     *
     * @param params 分页查询参数（岗位编码模糊匹配、状态筛选）
     * @return 分页岗位列表
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority(" + SxwlPermConstant.System.POSITION_LIST + ")")
    @SxwlLog(title = "岗位管理", description = "查询岗位列表")
    public PageInfo<SysPositionDTO> getPositionPageByParams(@Valid SysPositionPageParams params) {
        return sysPositionService.getPositionPageByParams(params);
    }

    /**
     * 新增岗位
     *
     * @param dto 岗位信息
     */
    @PostMapping
    @SxwlRepeatSubmit(interval = 3, message = "岗位创建中，请稍候")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority(" + SxwlPermConstant.System.POSITION_ADD + ")")
    @SxwlLog(title = "岗位管理", description = "新增岗位[#{#dto.positionCode}]")
    public void createPosition(@Valid @RequestBody SysPositionDTO dto) {
        sysPositionService.createPosition(dto);
    }

    /**
     * 修改岗位
     *
     * @param dto 岗位信息（含 id）
     */
    @PutMapping
    @SxwlRepeatSubmit(interval = 3, message = "岗位修改中，请稍候")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority(" + SxwlPermConstant.System.POSITION_EDIT + ")")
    @SxwlLog(title = "岗位管理", description = "修改岗位[#{#dto.positionCode}]")
    public void updatePosition(@Valid @RequestBody SysPositionDTO dto) {
        sysPositionService.updatePosition(dto);
    }

    /**
     * 删除岗位
     *
     * @param id 岗位 ID
     */
    @DeleteMapping("/{id}")
    @SxwlRepeatSubmit(interval = 3, message = "岗位删除中，请稍候")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority(" + SxwlPermConstant.System.POSITION_DELETE + ")")
    @SxwlLog(title = "岗位管理", description = "删除岗位[id=#{#id}]")
    public void deletePositionById(@PathVariable("id") Long id) {
        sysPositionService.deletePositionById(id);
    }

    /**
     * 批量删除岗位
     *
     * @param ids 岗位 ID 列表
     */
    @DeleteMapping("/batch")
    @SxwlRepeatSubmit(interval = 60, message = "批量删除岗位中，请稍候")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority(" + SxwlPermConstant.System.POSITION_DELETE + ")")
    @SxwlLog(title = "岗位管理", description = "批量删除岗位[ids=#{#ids}]")
    public void batchDeletePositionByIds(@RequestBody List<Long> ids) {
        sysPositionService.batchDeletePositionByIds(ids);
    }
}

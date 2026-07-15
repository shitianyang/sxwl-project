package com.sxwl.system.controller;

import com.github.pagehelper.PageInfo;
import com.sxwl.common.annotation.SxwlLog;
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
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:position:query')")
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
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:position:list')")
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
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:position:add')")
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
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:position:edit')")
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
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:position:delete')")
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
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:position:delete')")
    @SxwlLog(title = "岗位管理", description = "批量删除岗位[ids=#{#ids}]")
    public void batchDeletePositionByIds(@RequestBody List<Long> ids) {
        sysPositionService.batchDeletePositionByIds(ids);
    }
}

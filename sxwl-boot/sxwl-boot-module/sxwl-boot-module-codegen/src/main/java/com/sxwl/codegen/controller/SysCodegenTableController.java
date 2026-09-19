package com.sxwl.codegen.controller;

import com.sxwl.codegen.model.dto.CodegenConfigDTO;
import com.sxwl.common.constant.SxwlPermConstant;
import com.sxwl.codegen.model.dto.SysCodegenFieldDTO;
import com.sxwl.codegen.model.dto.SysCodegenTableDTO;
import com.sxwl.codegen.model.params.SysCodegenTablePageParams;
import com.sxwl.codegen.service.SysCodegenTableService;
import com.github.pagehelper.PageInfo;
import com.sxwl.common.entity.SxwlResult;
import com.sxwl.common.annotation.SxwlLog;
import com.sxwl.common.annotation.SxwlRepeatSubmit;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 代码生成-表配置控制器
 *
 * <p>提供表配置和字段配置的 CRUD API。</p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@RestController
@RequestMapping("/codegen/table")
public class SysCodegenTableController {

    private final SysCodegenTableService sysCodegenTableService;

    public SysCodegenTableController(SysCodegenTableService sysCodegenTableService) {
        this.sysCodegenTableService = sysCodegenTableService;
    }

    /**
     * 分页查询已配置的表
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('" + SxwlPermConstant.Codegen.TABLE_LIST + "')")
    public SxwlResult<PageInfo<SysCodegenTableDTO>> page(SysCodegenTablePageParams params) {
        return SxwlResult.success(sysCodegenTableService.page(params));
    }

    /**
     * 获取表详情（含字段列表）
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('" + SxwlPermConstant.Codegen.TABLE_QUERY + "')")
    public SxwlResult<SysCodegenTableDTO> getDetail(@PathVariable Long id) {
        return SxwlResult.success(sysCodegenTableService.getDetail(id));
    }

    /**
     * 新增表配置
     */
    @PostMapping
    @SxwlRepeatSubmit(interval = 5, message = "表配置创建中，请稍候")
    @SxwlLog(title = "代码生成", description = "新增表配置")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('" + SxwlPermConstant.Codegen.TABLE_ADD + "')")
    public SxwlResult<SysCodegenTableDTO> create(@RequestBody CodegenConfigDTO config) {
        return SxwlResult.success(sysCodegenTableService.create(config));
    }

    /**
     * 更新表配置
     */
    @PutMapping("/{id}")
    @SxwlRepeatSubmit(interval = 5, message = "表配置修改中，请稍候")
    @SxwlLog(title = "代码生成", description = "编辑表配置")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('" + SxwlPermConstant.Codegen.TABLE_EDIT + "')")
    public SxwlResult<Void> update(@PathVariable Long id, @RequestBody CodegenConfigDTO config) {
        sysCodegenTableService.update(id, config);
        return SxwlResult.success();
    }

    /**
     * 删除表配置（级联删除字段配置）
     */
    @DeleteMapping("/{id}")
    @SxwlRepeatSubmit(interval = 5, message = "表配置删除中，请稍候")
    @SxwlLog(title = "代码生成", description = "删除表配置")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('" + SxwlPermConstant.Codegen.TABLE_DELETE + "')")
    public SxwlResult<Void> delete(@PathVariable Long id) {
        sysCodegenTableService.delete(id);
        return SxwlResult.success();
    }

    /**
     * 保存字段配置
     */
    @PutMapping("/{id}/fields")
    @SxwlRepeatSubmit(interval = 10, message = "字段配置保存中，请稍候")
    @SxwlLog(title = "代码生成", description = "编辑字段配置")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('" + SxwlPermConstant.Codegen.TABLE_EDIT + "')")
    public SxwlResult<Void> saveFields(@PathVariable Long id, @RequestBody List<SysCodegenFieldDTO> fields) {
        sysCodegenTableService.saveFieldConfigs(id, fields);
        return SxwlResult.success();
    }
}

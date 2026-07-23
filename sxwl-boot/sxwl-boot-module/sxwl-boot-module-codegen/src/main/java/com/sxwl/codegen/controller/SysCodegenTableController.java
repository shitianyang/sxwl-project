package com.sxwl.codegen.controller;

import com.sxwl.codegen.model.dto.CodegenConfigDTO;
import com.sxwl.codegen.model.dto.SysCodegenFieldDTO;
import com.sxwl.codegen.model.dto.SysCodegenTableDTO;
import com.sxwl.codegen.model.params.SysCodegenTablePageParams;
import com.sxwl.codegen.service.SysCodegenTableService;
import com.github.pagehelper.PageInfo;
import com.sxwl.common.entity.SxwlResult;
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
    public SxwlResult<PageInfo<SysCodegenTableDTO>> page(SysCodegenTablePageParams params) {
        return SxwlResult.success(sysCodegenTableService.page(params));
    }

    /**
     * 获取表详情（含字段列表）
     */
    @GetMapping("/{id}")
    public SxwlResult<SysCodegenTableDTO> getDetail(@PathVariable Long id) {
        return SxwlResult.success(sysCodegenTableService.getDetail(id));
    }

    /**
     * 新增表配置
     */
    @PostMapping
    public SxwlResult<SysCodegenTableDTO> create(@RequestBody CodegenConfigDTO config) {
        return SxwlResult.success(sysCodegenTableService.create(config));
    }

    /**
     * 更新表配置
     */
    @PutMapping("/{id}")
    public SxwlResult<Void> update(@PathVariable Long id, @RequestBody CodegenConfigDTO config) {
        sysCodegenTableService.update(id, config);
        return SxwlResult.success();
    }

    /**
     * 删除表配置（级联删除字段配置）
     */
    @DeleteMapping("/{id}")
    public SxwlResult<Void> delete(@PathVariable Long id) {
        sysCodegenTableService.delete(id);
        return SxwlResult.success();
    }

    /**
     * 保存字段配置
     */
    @PutMapping("/{id}/fields")
    public SxwlResult<Void> saveFields(@PathVariable Long id, @RequestBody List<SysCodegenFieldDTO> fields) {
        sysCodegenTableService.saveFieldConfigs(id, fields);
        return SxwlResult.success();
    }
}

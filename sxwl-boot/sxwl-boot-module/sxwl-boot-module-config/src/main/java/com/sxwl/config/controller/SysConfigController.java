package com.sxwl.config.controller;

import com.github.pagehelper.PageInfo;
import com.sxwl.common.annotation.SxwlLog;
import com.sxwl.config.model.dto.SysConfigDTO;
import com.sxwl.config.model.params.SysConfigPageParams;
import com.sxwl.config.service.SysConfigInfoService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 系统参数配置 Controller
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@RestController
@RequestMapping("/system/config")
public class SysConfigController {

    private final SysConfigInfoService sysConfigInfoService;

    public SysConfigController(SysConfigInfoService sysConfigInfoService) {
        this.sysConfigInfoService = sysConfigInfoService;
    }

    /**
     * 根据 ID 查询配置（编辑回显）
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:config:query')")
    @SxwlLog(title = "参数配置", description = "查询配置详情[id=#{#id}]")
    public SysConfigDTO getConfigById(@PathVariable("id") Long id) {
        return sysConfigInfoService.getConfigById(id);
    }

    /**
     * 根据键名查询配置
     */
    @GetMapping("/key/{configKey}")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:config:query')")
    public SysConfigDTO getConfigByKey(@PathVariable("configKey") String configKey) {
        return sysConfigInfoService.getConfigByKey(configKey);
    }

    /**
     * 分页查询配置列表
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:config:list')")
    @SxwlLog(title = "参数配置", description = "查询配置列表")
    public PageInfo<SysConfigDTO> getConfigPageByParams(@Valid SysConfigPageParams params) {
        return sysConfigInfoService.getConfigPageByParams(params);
    }

    /**
     * 新增配置
     */
    @PostMapping
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:config:add')")
    @SxwlLog(title = "参数配置", description = "新增配置[#{#dto.configKey}]")
    public void createConfig(@Valid @RequestBody SysConfigDTO dto) {
        sysConfigInfoService.createConfig(dto);
    }

    /**
     * 修改配置
     */
    @PutMapping
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:config:edit')")
    @SxwlLog(title = "参数配置", description = "修改配置[#{#dto.configKey}]")
    public void updateConfig(@Valid @RequestBody SysConfigDTO dto) {
        sysConfigInfoService.updateConfig(dto);
    }

    /**
     * 删除配置
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:config:delete')")
    @SxwlLog(title = "参数配置", description = "删除配置[id=#{#id}]")
    public void deleteConfigById(@PathVariable("id") Long id) {
        sysConfigInfoService.deleteConfigById(id);
    }
}

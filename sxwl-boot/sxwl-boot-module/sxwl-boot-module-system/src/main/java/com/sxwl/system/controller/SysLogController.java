package com.sxwl.system.controller;

import com.github.pagehelper.PageInfo;
import com.sxwl.system.model.dto.SysLogDTO;
import com.sxwl.system.model.params.SysLogPageParams;
import com.sxwl.system.service.SysLogService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统日志 Controller（只读，无增删改）
 *
 * @author shitianyang
 * @since 0.1.0
 */
@RestController
@RequestMapping("/system/log")
public class SysLogController {

    private final SysLogService sysLogService;

    public SysLogController(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }

    /**
     * 分页查询日志列表
     *
     * @param params 分页查询参数（logType 必传，支持 title/userName/status/时间范围筛选）
     * @return 分页日志列表
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:log:list')")
    public PageInfo<SysLogDTO> getLogPageByParams(@Valid SysLogPageParams params) {
        return sysLogService.getLogPageByParams(params);
    }
}

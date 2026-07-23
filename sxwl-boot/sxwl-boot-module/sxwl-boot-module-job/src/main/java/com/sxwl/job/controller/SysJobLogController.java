package com.sxwl.job.controller;

import com.github.pagehelper.PageInfo;
import com.sxwl.common.annotation.SxwlLog;
import com.sxwl.common.annotation.SxwlRepeatSubmit;
import com.sxwl.job.model.dto.SysJobLogDTO;
import com.sxwl.job.model.params.SysJobLogPageParams;
import com.sxwl.job.service.SysJobLogService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 定时任务日志 Controller
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@RestController
@RequestMapping("/monitor/job/log")
public class SysJobLogController {

    private final SysJobLogService sysJobLogService;

    public SysJobLogController(SysJobLogService sysJobLogService) {
        this.sysJobLogService = sysJobLogService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('monitor:job:query')")
    public SysJobLogDTO getLogById(@PathVariable("id") Long id) {
        return sysJobLogService.getLogById(id);
    }

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('monitor:job:list')")
    @SxwlLog(title = "定时任务", description = "查询任务日志列表")
    public PageInfo<SysJobLogDTO> getLogPageByParams(@Valid SysJobLogPageParams params) {
        return sysJobLogService.getLogPageByParams(params);
    }

    @DeleteMapping("/{id}")
    @SxwlRepeatSubmit
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('monitor:job:delete')")
    @SxwlLog(title = "定时任务", description = "删除任务日志[id=#{#id}]")
    public void deleteLogById(@PathVariable("id") Long id) {
        sysJobLogService.deleteLogById(id);
    }

    @DeleteMapping("/clean")
    @SxwlRepeatSubmit(message = "清理操作过于频繁")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('monitor:job:clean')")
    @SxwlLog(title = "定时任务", description = "清理任务日志[days=#{#days}]")
    public void cleanLogBefore(@RequestParam(defaultValue = "30") int days) {
        sysJobLogService.cleanLogBefore(days);
    }
}

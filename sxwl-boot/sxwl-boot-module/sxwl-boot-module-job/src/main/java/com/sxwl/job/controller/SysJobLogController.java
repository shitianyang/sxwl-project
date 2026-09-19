package com.sxwl.job.controller;

import com.github.pagehelper.PageInfo;
import com.sxwl.common.constant.SxwlPermConstant;
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

    /**
     * 查询任务日志详情
     *
     * @param id 日志 ID
     * @return 日志 DTO
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('" + SxwlPermConstant.Monitor.JOB_QUERY + "')")
    public SysJobLogDTO getLogById(@PathVariable("id") Long id) {
        return sysJobLogService.getLogById(id);
    }

    /**
     * 分页查询任务日志列表
     *
     * @param params 分页查询参数
     * @return 日志分页列表
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('" + SxwlPermConstant.Monitor.JOB_LIST + "')")
    @SxwlLog(title = "定时任务", description = "查询任务日志列表")
    public PageInfo<SysJobLogDTO> getLogPageByParams(@Valid SysJobLogPageParams params) {
        return sysJobLogService.getLogPageByParams(params);
    }

    /**
     * 删除单条任务日志（软删除）
     *
     * @param id 日志 ID
     */
    @DeleteMapping("/{id}")
    @SxwlRepeatSubmit
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('" + SxwlPermConstant.Monitor.JOB_LOG_DELETE + "')")
    @SxwlLog(title = "定时任务", description = "删除任务日志[id=#{#id}]")
    public void deleteLogById(@PathVariable("id") Long id) {
        sysJobLogService.deleteLogById(id);
    }

    /**
     * 清理 N 天前的任务日志
     *
     * @param days 保留天数（默认 30 天）
     */
    @DeleteMapping("/clean")
    @SxwlRepeatSubmit(message = "清理操作过于频繁")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('" + SxwlPermConstant.Monitor.JOB_LOG_CLEAN + "')")
    @SxwlLog(title = "定时任务", description = "清理任务日志[days=#{#days}]")
    public void cleanLogBefore(@RequestParam(defaultValue = "30") int days) {
        sysJobLogService.cleanLogBefore(days);
    }
}

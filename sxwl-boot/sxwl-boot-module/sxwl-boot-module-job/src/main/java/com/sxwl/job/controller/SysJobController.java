package com.sxwl.job.controller;

import com.github.pagehelper.PageInfo;
import com.sxwl.common.constant.SxwlPermConstant;
import com.sxwl.common.annotation.SxwlLog;
import com.sxwl.common.annotation.SxwlRepeatSubmit;
import com.sxwl.job.model.dto.SysJobDTO;
import com.sxwl.job.model.params.SysJobPageParams;
import com.sxwl.job.service.SysJobInfoService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 定时任务 Controller
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@RestController
@RequestMapping("/monitor/job")
public class SysJobController {

    private final SysJobInfoService sysJobInfoService;

    public SysJobController(SysJobInfoService sysJobInfoService) {
        this.sysJobInfoService = sysJobInfoService;
    }

    /**
     * 查询定时任务详情
     *
     * @param id 任务 ID
     * @return 任务 DTO
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority(" + SxwlPermConstant.Monitor.JOB_QUERY + ")")
    @SxwlLog(title = "定时任务", description = "查询任务详情[id=#{#id}]")
    public SysJobDTO getJobById(@PathVariable("id") Long id) {
        return sysJobInfoService.getJobById(id);
    }

    /**
     * 分页查询定时任务列表
     *
     * @param params 分页查询参数
     * @return 任务分页列表
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority(" + SxwlPermConstant.Monitor.JOB_LIST + ")")
    @SxwlLog(title = "定时任务", description = "查询任务列表")
    public PageInfo<SysJobDTO> getJobPageByParams(@Valid SysJobPageParams params) {
        return sysJobInfoService.getJobPageByParams(params);
    }

    /**
     * 新增定时任务
     *
     * @param dto 任务 DTO（含 jobName、jobGroup、className、methodName、cronExpression 等）
     */
    @PostMapping
    @SxwlRepeatSubmit
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority(" + SxwlPermConstant.Monitor.JOB_ADD + ")")
    @SxwlLog(title = "定时任务", description = "新增任务[#{#dto.jobName}]")
    public void createJob(@Valid @RequestBody SysJobDTO dto) {
        sysJobInfoService.createJob(dto);
    }

    /**
     * 修改定时任务
     *
     * @param dto 任务 DTO（需包含 id）
     */
    @PutMapping
    @SxwlRepeatSubmit
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority(" + SxwlPermConstant.Monitor.JOB_EDIT + ")")
    @SxwlLog(title = "定时任务", description = "修改任务[#{#dto.jobName}]")
    public void updateJob(@Valid @RequestBody SysJobDTO dto) {
        sysJobInfoService.updateJob(dto);
    }

    /**
     * 删除定时任务（软删除 + Quartz 调度移除）
     *
     * @param id 任务 ID
     */
    @DeleteMapping("/{id}")
    @SxwlRepeatSubmit
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority(" + SxwlPermConstant.Monitor.JOB_DELETE + ")")
    @SxwlLog(title = "定时任务", description = "删除任务[id=#{#id}]")
    public void deleteJobById(@PathVariable("id") Long id) {
        sysJobInfoService.deleteJobById(id);
    }

    /**
     * 暂停定时任务（Quartz pauseJob）
     *
     * @param id 任务 ID
     */
    @PutMapping("/pause/{id}")
    @SxwlRepeatSubmit
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority(" + SxwlPermConstant.Monitor.JOB_PAUSE + ")")
    @SxwlLog(title = "定时任务", description = "暂停任务[id=#{#id}]")
    public void pauseJob(@PathVariable("id") Long id) {
        sysJobInfoService.pauseJob(id);
    }

    /**
     * 恢复定时任务（Quartz resumeJob）
     *
     * @param id 任务 ID
     */
    @PutMapping("/resume/{id}")
    @SxwlRepeatSubmit
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority(" + SxwlPermConstant.Monitor.JOB_RESUME + ")")
    @SxwlLog(title = "定时任务", description = "恢复任务[id=#{#id}]")
    public void resumeJob(@PathVariable("id") Long id) {
        sysJobInfoService.resumeJob(id);
    }

    /**
     * 立即执行一次定时任务（Quartz triggerJob）
     *
     * @param id 任务 ID
     */
    @PutMapping("/run/{id}")
    @SxwlRepeatSubmit
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority(" + SxwlPermConstant.Monitor.JOB_RUN + ")")
    @SxwlLog(title = "定时任务", description = "立即执行[id=#{#id}]")
    public void runOnce(@PathVariable("id") Long id) {
        sysJobInfoService.runOnce(id);
    }
}

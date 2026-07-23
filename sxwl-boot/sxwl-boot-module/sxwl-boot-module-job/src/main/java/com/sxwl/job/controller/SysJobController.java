package com.sxwl.job.controller;

import com.github.pagehelper.PageInfo;
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

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('monitor:job:query')")
    @SxwlLog(title = "定时任务", description = "查询任务详情[id=#{#id}]")
    public SysJobDTO getJobById(@PathVariable("id") Long id) {
        return sysJobInfoService.getJobById(id);
    }

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('monitor:job:list')")
    @SxwlLog(title = "定时任务", description = "查询任务列表")
    public PageInfo<SysJobDTO> getJobPageByParams(@Valid SysJobPageParams params) {
        return sysJobInfoService.getJobPageByParams(params);
    }

    @PostMapping
    @SxwlRepeatSubmit
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('monitor:job:add')")
    @SxwlLog(title = "定时任务", description = "新增任务[#{#dto.jobName}]")
    public void createJob(@Valid @RequestBody SysJobDTO dto) {
        sysJobInfoService.createJob(dto);
    }

    @PutMapping
    @SxwlRepeatSubmit
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('monitor:job:edit')")
    @SxwlLog(title = "定时任务", description = "修改任务[#{#dto.jobName}]")
    public void updateJob(@Valid @RequestBody SysJobDTO dto) {
        sysJobInfoService.updateJob(dto);
    }

    @DeleteMapping("/{id}")
    @SxwlRepeatSubmit
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('monitor:job:delete')")
    @SxwlLog(title = "定时任务", description = "删除任务[id=#{#id}]")
    public void deleteJobById(@PathVariable("id") Long id) {
        sysJobInfoService.deleteJobById(id);
    }

    @PutMapping("/pause/{id}")
    @SxwlRepeatSubmit
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('monitor:job:pause')")
    @SxwlLog(title = "定时任务", description = "暂停任务[id=#{#id}]")
    public void pauseJob(@PathVariable("id") Long id) {
        sysJobInfoService.pauseJob(id);
    }

    @PutMapping("/resume/{id}")
    @SxwlRepeatSubmit
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('monitor:job:resume')")
    @SxwlLog(title = "定时任务", description = "恢复任务[id=#{#id}]")
    public void resumeJob(@PathVariable("id") Long id) {
        sysJobInfoService.resumeJob(id);
    }

    @PutMapping("/run/{id}")
    @SxwlRepeatSubmit
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('monitor:job:run')")
    @SxwlLog(title = "定时任务", description = "立即执行[id=#{#id}]")
    public void runOnce(@PathVariable("id") Long id) {
        sysJobInfoService.runOnce(id);
    }
}

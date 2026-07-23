package com.sxwl.backup.controller;

import com.github.pagehelper.PageInfo;
import com.sxwl.backup.dto.SysBackupDTO;
import com.sxwl.backup.service.SysBackupService;
import com.sxwl.common.annotation.SxwlLog;
import com.sxwl.common.annotation.SxwlRepeatSubmit;
import com.sxwl.security.utils.SxwlSecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 数据备份 Controller
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@RestController
@RequestMapping("/sys/backup")
public class SysBackupController {

    private final SysBackupService sysBackupService;

    public SysBackupController(SysBackupService sysBackupService) {
        this.sysBackupService = sysBackupService;
    }

    @PostMapping("/backup")
    @SxwlRepeatSubmit(interval = 60, message = "备份操作执行中，请稍候")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('monitor:backup:backup')")
    @SxwlLog(title = "数据备份", description = "执行数据库备份")
    public void backup() {
        Long userId = SxwlSecurityUtils.getCurrentUser()
                .map(u -> u.getUserId())
                .orElse(null);
        sysBackupService.backup(userId);
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('monitor:backup:list')")
    @SxwlLog(title = "数据备份", description = "查询备份列表")
    public PageInfo<SysBackupDTO> list(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "20") int size) {
        return sysBackupService.list(page, size);
    }

    @PostMapping("/restore/{fileId}")
    @SxwlRepeatSubmit(interval = 60, message = "恢复操作执行中，请稍候")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('monitor:backup:restore')")
    @SxwlLog(title = "数据备份", description = "恢复备份[fileId=#{#fileId}]")
    public void restore(@PathVariable("fileId") Long fileId) {
        sysBackupService.restore(fileId);
    }

    @DeleteMapping("/{id}")
    @SxwlRepeatSubmit
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('monitor:backup:delete')")
    @SxwlLog(title = "数据备份", description = "删除备份记录[id=#{#id}]")
    public void delete(@PathVariable("id") Long id) {
        sysBackupService.delete(id);
    }
}

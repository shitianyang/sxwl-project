package com.sxwl.notice.controller;

import com.github.pagehelper.PageInfo;
import com.sxwl.common.annotation.SxwlLog;
import com.sxwl.common.annotation.SxwlRepeatSubmit;
import com.sxwl.notice.model.dto.SysNoticeDTO;
import com.sxwl.notice.model.dto.SysNoticeUnreadItem;
import com.sxwl.notice.model.params.SysNoticePageParams;
import com.sxwl.notice.service.SysNoticeInfoService;
import com.sxwl.security.utils.SxwlSecurityUtils;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知公告 Controller
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@RestController
@RequestMapping("/system/notice")
public class SysNoticeController {

    private static final Logger log = LoggerFactory.getLogger(SysNoticeController.class);

    private final SysNoticeInfoService sysNoticeInfoService;

    public SysNoticeController(SysNoticeInfoService sysNoticeInfoService) {
        this.sysNoticeInfoService = sysNoticeInfoService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:notice:query')")
    @SxwlLog(title = "通知公告", description = "查询公告详情[id=#{#id}]")
    public SysNoticeDTO getNoticeById(@PathVariable("id") Long id) {
        return sysNoticeInfoService.getNoticeById(id);
    }

    @GetMapping("/page")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:notice:list')")
    @SxwlLog(title = "通知公告", description = "查询公告列表")
    public PageInfo<SysNoticeDTO> getNoticePageByParams(@Valid SysNoticePageParams params) {
        return sysNoticeInfoService.getNoticePageByParams(params);
    }

    @PostMapping
    @SxwlRepeatSubmit
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:notice:add')")
    @SxwlLog(title = "通知公告", description = "新增公告[#{#dto.title}]")
    public void createNotice(@Valid @RequestBody SysNoticeDTO dto) {
        sysNoticeInfoService.createNotice(dto);
    }

    @PutMapping
    @SxwlRepeatSubmit
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:notice:edit')")
    @SxwlLog(title = "通知公告", description = "修改公告[#{#dto.id}]")
    public void updateNotice(@Valid @RequestBody SysNoticeDTO dto) {
        sysNoticeInfoService.updateNotice(dto);
    }

    @DeleteMapping("/{id}")
    @SxwlRepeatSubmit
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:notice:delete')")
    @SxwlLog(title = "通知公告", description = "删除公告[id=#{#id}]")
    public void deleteNoticeById(@PathVariable("id") Long id) {
        sysNoticeInfoService.deleteNoticeById(id);
    }

    @PutMapping("/publish/{id}")
    @SxwlRepeatSubmit
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:notice:publish')")
    @SxwlLog(title = "通知公告", description = "发布公告[id=#{#id}]")
    public void publishNotice(@PathVariable("id") Long id) {
        sysNoticeInfoService.publishNotice(id);
    }

    @PutMapping("/revoke/{id}")
    @SxwlRepeatSubmit
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('system:notice:revoke')")
    @SxwlLog(title = "通知公告", description = "撤回公告[id=#{#id}]")
    public void revokeNotice(@PathVariable("id") Long id) {
        sysNoticeInfoService.revokeNotice(id);
    }

    // ========== 未读/已读接口 ==========

    @GetMapping("/unread/count")
    public Long getUnreadCount() {
        Long userId = SxwlSecurityUtils.getCurrentUserId();
        if (userId == null) return 0L;
        return sysNoticeInfoService.getUnreadCount(userId);
    }

    @GetMapping("/unread/list")
    public List<SysNoticeUnreadItem> getUnreadList() {
        Long userId = SxwlSecurityUtils.getCurrentUserId();
        if (userId == null) return List.of();
        return sysNoticeInfoService.getRecentUnreadList(userId);
    }

    @PostMapping("/read/{noticeId}")
    @SxwlRepeatSubmit
    public void markAsRead(@PathVariable("noticeId") Long noticeId) {
        Long userId = SxwlSecurityUtils.getCurrentUserId();
        if (userId != null) {
            sysNoticeInfoService.markAsRead(noticeId, userId);
        }
    }

    @PostMapping("/read/all")
    @SxwlRepeatSubmit
    public void markAllAsRead() {
        Long userId = SxwlSecurityUtils.getCurrentUserId();
        if (userId != null) {
            sysNoticeInfoService.markAllAsRead(userId);
        }
    }
}

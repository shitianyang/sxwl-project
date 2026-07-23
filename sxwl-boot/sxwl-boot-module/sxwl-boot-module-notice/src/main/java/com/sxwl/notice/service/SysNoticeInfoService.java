package com.sxwl.notice.service;

import com.github.pagehelper.PageInfo;
import com.sxwl.notice.model.dto.SysNoticeDTO;
import com.sxwl.notice.model.dto.SysNoticeUnreadItem;
import com.sxwl.notice.model.params.SysNoticePageParams;

import java.util.List;

/**
 * 通知公告 Service 接口
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public interface SysNoticeInfoService {

    SysNoticeDTO getNoticeById(Long id);

    PageInfo<SysNoticeDTO> getNoticePageByParams(SysNoticePageParams params);

    int createNotice(SysNoticeDTO dto);

    int updateNotice(SysNoticeDTO dto);

    int deleteNoticeById(Long id);

    /**
     * 发布公告
     */
    void publishNotice(Long id);

    /**
     * 撤回公告
     */
    void revokeNotice(Long id);

    // ========== 用户未读/已读接口 ==========

    /**
     * 查询当前用户未读公告数
     */
    Long getUnreadCount(Long userId);

    /**
     * 查询当前用户的最近公告列表（含已读状态）
     */
    List<SysNoticeUnreadItem> getRecentUnreadList(Long userId);

    /**
     * 标记单条公告为已读
     */
    void markAsRead(Long noticeId, Long userId);

    /**
     * 标记全部为已读
     */
    void markAllAsRead(Long userId);
}

package com.sxwl.notice.mapper;

import com.sxwl.notice.model.dto.SysNoticeUnreadItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知公告已读状态 Mapper
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@Mapper
public interface SysNoticeReadMapper {

    /**
     * 查询用户未读公告数量
     */
    Long countUnread(@Param("userId") Long userId);

    /**
     * 查询用户最近 N 条已发布的公告（含已读状态）
     */
    List<SysNoticeUnreadItem> listRecentNotices(@Param("userId") Long userId, @Param("limit") int limit);

    /**
     * 标记单条公告为已读
     */
    int insertRead(@Param("id") Long id, @Param("noticeId") Long noticeId, @Param("userId") Long userId);

    /**
     * 标记所有已发布公告为已读（跳过已存在的）
     */
    int insertReadAll(@Param("userId") Long userId);

    /**
     * 检查某条公告是否已读
     */
    Long existsRead(@Param("noticeId") Long noticeId, @Param("userId") Long userId);
}

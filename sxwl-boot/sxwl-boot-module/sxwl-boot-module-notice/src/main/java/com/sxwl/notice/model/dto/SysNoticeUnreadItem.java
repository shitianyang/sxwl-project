package com.sxwl.notice.model.dto;

import java.time.LocalDateTime;

/**
 * 通知公告未读列表项 DTO
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public class SysNoticeUnreadItem {

    private Long id;
    private String title;
    private String noticeType;
    private String level;
    private LocalDateTime publishTime;
    private String createTime;
    private Integer readFlag; // 0=未读 1=已读

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getNoticeType() { return noticeType; }
    public void setNoticeType(String noticeType) { this.noticeType = noticeType; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public LocalDateTime getPublishTime() { return publishTime; }
    public void setPublishTime(LocalDateTime publishTime) { this.publishTime = publishTime; }

    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }

    public Integer getReadFlag() { return readFlag; }
    public void setReadFlag(Integer readFlag) { this.readFlag = readFlag; }
}

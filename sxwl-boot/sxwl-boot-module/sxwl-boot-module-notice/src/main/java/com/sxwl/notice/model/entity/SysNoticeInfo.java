package com.sxwl.notice.model.entity;

import com.sxwl.common.entity.SxwlBasicField;

import java.time.LocalDateTime;

/**
 * 通知公告实体
 *
 * <p>对应数据库 {@code sys_notice_info} 表。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public class SysNoticeInfo extends SxwlBasicField {

    /** 公告标题 */
    private String title;

    /** 公告内容（富文本 HTML） */
    private String content;

    /** 公告类型：notice=通知 announcement=公告 */
    private String noticeType;

    /** 级别：info=普通 important=重要 urgent=紧急 */
    private String level;

    /** 状态：0=草稿 1=已发布 2=已撤回 */
    private Integer status;

    /** 发布时间 */
    private LocalDateTime publishTime;

    /** 过期时间 */
    private LocalDateTime expireTime;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(LocalDateTime publishTime) {
        this.publishTime = publishTime;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }
}

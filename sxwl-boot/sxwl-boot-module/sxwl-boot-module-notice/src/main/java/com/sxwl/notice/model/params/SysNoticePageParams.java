package com.sxwl.notice.model.params;

import com.sxwl.common.entity.SxwlPageField;

/**
 * 通知公告-分页查询参数
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public class SysNoticePageParams extends SxwlPageField {

    /** 公告标题（模糊匹配） */
    private String title;

    /** 公告类型：notice=通知 announcement=公告 */
    private String noticeType;

    /** 级别：info=普通 important=重要 urgent=紧急 */
    private String level;

    /** 状态：0=草稿 1=已发布 2=已撤回 */
    private Integer status;

    public SysNoticePageParams() {
        setCurrent(1);
        setPageSize(10);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
}

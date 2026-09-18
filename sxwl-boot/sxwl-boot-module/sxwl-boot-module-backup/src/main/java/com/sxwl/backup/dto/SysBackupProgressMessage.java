package com.sxwl.backup.dto;

/**
 * 备份进度推送消息（WebSocket/SSE 用）
 *
 * @author shitianyang
 * @since 0.1.1
 */
public class SysBackupProgressMessage {

    /** 备份记录 ID */
    private Long fileId;

    /** 进度百分比（0-100，负数表示错误状态） */
    private int progress;

    /** 进度消息描述 */
    private String message;

    public SysBackupProgressMessage() {
    }

    public SysBackupProgressMessage(Long fileId, int progress, String message) {
        this.fileId = fileId;
        this.progress = progress;
        this.message = message;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

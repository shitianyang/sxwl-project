package com.sxwl.backup.dto;

/**
 * 数据备份进度 DTO（WebSocket 推送用）
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public class SysBackupProgressDTO {

    /** 备份记录 ID */
    private Long fileId;

    /** 进度百分比（0-100） */
    private int progress;

    /** 进度消息描述 */
    private String message;

    public SysBackupProgressDTO() {
    }

    public SysBackupProgressDTO(Long fileId, int progress, String message) {
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

package com.sxwl.backup.dto;

/**
 * 数据备份记录 DTO
 *
 * <p>对应 sys_file_info 表中 business_type = 'db_backup' 的记录。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public class SysBackupDTO {

    /** 备份记录 ID（对应 sys_file_info.id） */
    private Long id;

    /** 备份文件名 */
    private String fileName;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 文件大小（格式化显示） */
    private String fileSizeDisplay;

    /** 文件访问 URL */
    private String fileUrl;

    /** 备份状态 */
    private Integer status;

    /** 备份时间 */
    private String createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileSizeDisplay() {
        return fileSizeDisplay;
    }

    public void setFileSizeDisplay(String fileSizeDisplay) {
        this.fileSizeDisplay = fileSizeDisplay;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}

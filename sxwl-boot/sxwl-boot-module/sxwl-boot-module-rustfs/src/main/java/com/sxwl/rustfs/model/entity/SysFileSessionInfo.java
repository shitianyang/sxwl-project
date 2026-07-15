package com.sxwl.rustfs.model.entity;

import com.sxwl.common.entity.SxwlBasicField;

/**
 * 系统文件上传会话实体
 *
 * <p>对应数据库 {@code sys_file_session_info} 表，记录一次分片上传会话的生命周期。</p>
 *
 * @author shitianyang
 * @since 0.1.0
 */
public class SysFileSessionInfo extends SxwlBasicField {

    /** 文件 MD5（秒传/去重/续传标识） */
    private String fileMd5;

    /** 原始文件名 */
    private String originalName;

    /** 总文件大小（字节） */
    private Long fileSize;

    /** MIME 类型 */
    private String contentType;

    /** 总分片数 */
    private Integer totalChunks;

    /** 每个分片大小（字节） */
    private Integer chunkSize;

    /** 状态：0=上传中 1=已完成 2=已取消 */
    private Integer status;

    public String getFileMd5() { return fileMd5; }
    public void setFileMd5(String fileMd5) { this.fileMd5 = fileMd5; }

    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public Integer getTotalChunks() { return totalChunks; }
    public void setTotalChunks(Integer totalChunks) { this.totalChunks = totalChunks; }

    public Integer getChunkSize() { return chunkSize; }
    public void setChunkSize(Integer chunkSize) { this.chunkSize = chunkSize; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}

package com.sxwl.rustfs.model.dto;

/**
 * 分片上传初始化 DTO
 *
 * @author shitianyang
 * @since 0.1.0
 */
public class UploadInitDTO {

    /** 文件 MD5 */
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
}

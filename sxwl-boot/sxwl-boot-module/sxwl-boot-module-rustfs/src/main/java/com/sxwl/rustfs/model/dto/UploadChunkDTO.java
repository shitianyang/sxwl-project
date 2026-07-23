package com.sxwl.rustfs.model.dto;

/**
 * 分片上传响应 DTO
 *
 * @author shitianyang
 * @since 0.1.0
 */
public class UploadChunkDTO {

    /** 上传会话 ID */
    private Long uploadId;

    /** 已上传的分片序号 */
    private Integer chunkIndex;

    public UploadChunkDTO() {}

    public UploadChunkDTO(Long uploadId, Integer chunkIndex) {
        this.uploadId = uploadId;
        this.chunkIndex = chunkIndex;
    }

    public Long getUploadId() { return uploadId; }
    public void setUploadId(Long uploadId) { this.uploadId = uploadId; }

    public Integer getChunkIndex() { return chunkIndex; }
    public void setChunkIndex(Integer chunkIndex) { this.chunkIndex = chunkIndex; }
}

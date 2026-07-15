package com.sxwl.rustfs.model.dto;

import java.util.List;

/**
 * 续传查询响应 DTO
 *
 * @author shitianyang
 * @since 0.1.0
 */
public class ChunkCheckDTO {

    /** 上传会话 ID */
    private Long uploadId;

    /** 已上传的分片序号列表 */
    private List<Integer> uploadedChunks;

    public ChunkCheckDTO() {}

    public ChunkCheckDTO(Long uploadId, List<Integer> uploadedChunks) {
        this.uploadId = uploadId;
        this.uploadedChunks = uploadedChunks;
    }

    public Long getUploadId() { return uploadId; }
    public void setUploadId(Long uploadId) { this.uploadId = uploadId; }

    public List<Integer> getUploadedChunks() { return uploadedChunks; }
    public void setUploadedChunks(List<Integer> uploadedChunks) { this.uploadedChunks = uploadedChunks; }
}

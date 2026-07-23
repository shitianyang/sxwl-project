package com.sxwl.rustfs.model.entity;

import com.sxwl.common.entity.SxwlBasicField;

/**
 * 系统文件分片明细实体
 *
 * <p>对应数据库 {@code sys_file_chunk_info} 表，记录分片上传中每个分片的状态。</p>
 *
 * @author shitianyang
 * @since 0.1.0
 */
public class SysFileChunkInfo extends SxwlBasicField {

    /** 上传会话 ID（关联 sys_file_session_info.id） */
    private Long uploadId;

    /** 分片序号，从 0 开始 */
    private Integer chunkIndex;

    /** 分片 MD5 */
    private String chunkMd5;

    /** S3 临时对象键 */
    private String objectKey;

    /** 本分片实际大小（字节） */
    private Long chunkSize;

    /** 状态：0=待上传 1=已上传 */
    private Integer status;

    public Long getUploadId() { return uploadId; }
    public void setUploadId(Long uploadId) { this.uploadId = uploadId; }

    public Integer getChunkIndex() { return chunkIndex; }
    public void setChunkIndex(Integer chunkIndex) { this.chunkIndex = chunkIndex; }

    public String getChunkMd5() { return chunkMd5; }
    public void setChunkMd5(String chunkMd5) { this.chunkMd5 = chunkMd5; }

    public String getObjectKey() { return objectKey; }
    public void setObjectKey(String objectKey) { this.objectKey = objectKey; }

    public Long getChunkSize() { return chunkSize; }
    public void setChunkSize(Long chunkSize) { this.chunkSize = chunkSize; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}

package com.sxwl.rustfs.model.dto;

/**
 * 合并完成请求 DTO
 *
 * @author shitianyang
 * @since 0.1.0
 */
public class UploadCompleteDTO {

    /** 上传会话 ID */
    private Long uploadId;

    /** 文件 MD5 */
    private String fileMd5;

    public Long getUploadId() { return uploadId; }
    public void setUploadId(Long uploadId) { this.uploadId = uploadId; }

    public String getFileMd5() { return fileMd5; }
    public void setFileMd5(String fileMd5) { this.fileMd5 = fileMd5; }
}

package com.sxwl.rustfs.model.dto;

/**
 * 系统文件 DTO（响应）
 *
 * @author shitianyang
 * @since 0.1.0
 */
public class SysFileDTO {

    /** 文件 ID */
    private Long id;

    /** 原始文件名 */
    private String fileName;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 文件 MIME 类型 */
    private String fileType;

    /** 文件后缀 */
    private String fileSuffix;

    /** 文件 MD5 */
    private String md5;

    /** 业务类型 */
    private String businessType;

    /** 预签名访问 URL */
    private String presignedUrl;

    /** 创建时间 */
    private String createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public String getFileSuffix() { return fileSuffix; }
    public void setFileSuffix(String fileSuffix) { this.fileSuffix = fileSuffix; }

    public String getMd5() { return md5; }
    public void setMd5(String md5) { this.md5 = md5; }

    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }

    public String getPresignedUrl() { return presignedUrl; }
    public void setPresignedUrl(String presignedUrl) { this.presignedUrl = presignedUrl; }

    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
}

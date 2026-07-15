package com.sxwl.rustfs.model.entity;

import com.sxwl.common.entity.SxwlBasicField;

/**
 * 系统文件实体
 *
 * <p>对应数据库 {@code sys_file_info} 表。</p>
 *
 * @author shitianyang
 * @since 0.1.0
 */
public class SysFileInfo extends SxwlBasicField {

    /** 原始文件名，如：头像.png */
    private String fileName;

    /** RustFS 对象键，如 2026/07/02/uuid.png */
    private String objectKey;

    /** 访问 URL（冗余，便于前端直接使用） */
    private String fileUrl;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 文件 MIME 类型，如 image/png */
    private String fileType;

    /** 文件后缀，如 png */
    private String fileSuffix;

    /** RustFS bucket 名 */
    private String bucketName;

    /** 文件 MD5（秒传/去重用） */
    private String md5;

    /** 业务类型，如 avatar、attachment */
    private String businessType;

    /** 状态：0=临时 1=正常 2=已删除 */
    private Integer status;

    /** 描述说明 */
    private String description;

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getObjectKey() { return objectKey; }
    public void setObjectKey(String objectKey) { this.objectKey = objectKey; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public String getFileSuffix() { return fileSuffix; }
    public void setFileSuffix(String fileSuffix) { this.fileSuffix = fileSuffix; }

    public String getBucketName() { return bucketName; }
    public void setBucketName(String bucketName) { this.bucketName = bucketName; }

    public String getMd5() { return md5; }
    public void setMd5(String md5) { this.md5 = md5; }

    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

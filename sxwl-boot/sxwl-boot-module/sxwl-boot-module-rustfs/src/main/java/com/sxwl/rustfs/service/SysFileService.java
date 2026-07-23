package com.sxwl.rustfs.service;

import com.github.pagehelper.PageInfo;
import com.sxwl.rustfs.model.dto.*;
import com.sxwl.rustfs.model.params.SysFilePageParams;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * 系统文件 Service 接口
 *
 * @author shitianyang
 * @since 0.1.0
 */
public interface SysFileService {

    /**
     * 初始化分片上传
     *
     * @param dto 初始化参数（fileMd5, originalName, fileSize, contentType, totalChunks, chunkSize）
     * @return 上传会话 ID
     */
    Long initUpload(UploadInitDTO dto);

    /**
     * 上传单个分片
     *
     * @param uploadId   会话 ID
     * @param chunkIndex 分片序号
     * @param chunkMd5   分片 MD5
     * @param file       分片二进制
     * @return 上传结果
     */
    UploadChunkDTO uploadChunk(Long uploadId, Integer chunkIndex, String chunkMd5, MultipartFile file);

    /**
     * 查询已上传的分片列表（断点续传）
     *
     * @param fileMd5 文件 MD5
     * @return 续传信息（uploadId + 已上传分片列表）
     */
    ChunkCheckDTO getUploadedChunks(String fileMd5);

    /**
     * 合并分片完成上传
     *
     * @param dto 合并请求（uploadId, fileMd5）
     * @return 文件信息
     */
    SysFileDTO completeUpload(UploadCompleteDTO dto);

    /**
     * 简单上传（小文件不分片）
     *
     * @param file 文件
     * @return 文件信息
     */
    SysFileDTO simpleUpload(MultipartFile file);

    /**
     * 下载文件
     *
     * @param id 文件 ID
     * @return 文件流响应
     */
    ResponseEntity<Resource> downloadFile(Long id);

    /**
     * 获取预签名文件 URL
     *
     * @param id 文件 ID
     * @return 预签名 URL
     */
    String getPresignedUrl(Long id);

    /**
     * 秒传检查
     *
     * @param md5 文件 MD5
     * @return 已存在的文件信息，不存在返回 null
     */
    SysFileDTO checkMd5(String md5);

    /**
     * 软删除文件
     *
     * @param id 文件 ID
     */
    void deleteFile(Long id);

    /**
     * 分页查询文件列表
     *
     * @param params 分页查询参数
     * @return 分页文件列表
     */
    PageInfo<SysFileDTO> getFilePageByParams(SysFilePageParams params);
}

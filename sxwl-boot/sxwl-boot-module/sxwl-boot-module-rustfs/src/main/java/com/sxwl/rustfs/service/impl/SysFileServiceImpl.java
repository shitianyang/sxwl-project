package com.sxwl.rustfs.service.impl;

import com.github.pagehelper.PageInfo;
import com.sxwl.common.utils.SxwlSnowFlakeUtils;
import com.sxwl.rustfs.client.SxwlRustfsTemplate;
import com.sxwl.rustfs.config.SxwlRustfsProperties;
import com.sxwl.rustfs.mapper.SysFileChunkInfoMapper;
import com.sxwl.rustfs.mapper.SysFileInfoMapper;
import com.sxwl.rustfs.mapper.SysFileSessionInfoMapper;
import com.sxwl.rustfs.model.dto.*;
import com.sxwl.rustfs.model.entity.SysFileChunkInfo;
import com.sxwl.rustfs.model.entity.SysFileInfo;
import com.sxwl.rustfs.model.entity.SysFileSessionInfo;
import com.sxwl.rustfs.model.params.SysFilePageParams;
import com.sxwl.rustfs.service.SysFileService;
import com.sxwl.security.utils.SxwlSecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 系统文件 Service 实现
 *
 * @author shitianyang
 * @since 0.1.0
 */
@Service
public class SysFileServiceImpl implements SysFileService {

    private static final Logger log = LoggerFactory.getLogger(SysFileServiceImpl.class);

    /** 文件状态：正常 */
    private static final int FILE_STATUS_NORMAL = 1;

    /** 会话状态：上传中 */
    private static final int SESSION_STATUS_UPLOADING = 0;

    /** 会话状态：已完成 */
    private static final int SESSION_STATUS_COMPLETED = 1;

    /** 分片状态：已上传 */
    private static final int CHUNK_STATUS_UPLOADED = 1;

    private final SysFileInfoMapper sysFileInfoMapper;
    private final SysFileSessionInfoMapper sysFileSessionInfoMapper;
    private final SysFileChunkInfoMapper sysFileChunkInfoMapper;
    private final SxwlRustfsTemplate rustfsTemplate;
    private final SxwlRustfsProperties properties;

    public SysFileServiceImpl(SysFileInfoMapper sysFileInfoMapper,
                              SysFileSessionInfoMapper sysFileSessionInfoMapper,
                              SysFileChunkInfoMapper sysFileChunkInfoMapper,
                              SxwlRustfsTemplate rustfsTemplate,
                              SxwlRustfsProperties properties) {
        this.sysFileInfoMapper = sysFileInfoMapper;
        this.sysFileSessionInfoMapper = sysFileSessionInfoMapper;
        this.sysFileChunkInfoMapper = sysFileChunkInfoMapper;
        this.rustfsTemplate = rustfsTemplate;
        this.properties = properties;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long initUpload(UploadInitDTO dto) {
        // 检查是否有同 md5 未完成的会话
        SysFileSessionInfo existing = sysFileSessionInfoMapper.getByMd5(dto.getFileMd5());
        if (existing != null) {
            return existing.getId();
        }

        // 创建上传会话
        SysFileSessionInfo session = new SysFileSessionInfo();
        session.setFileMd5(dto.getFileMd5());
        session.setOriginalName(dto.getOriginalName());
        session.setFileSize(dto.getFileSize());
        session.setContentType(dto.getContentType());
        session.setTotalChunks(dto.getTotalChunks());
        session.setChunkSize(dto.getChunkSize());
        session.setStatus(SESSION_STATUS_UPLOADING);
        sysFileSessionInfoMapper.insertUpload(session);

        // 批量生成分片记录
        Long sessionId = session.getId();
        List<SysFileChunkInfo> chunks = IntStream.range(0, dto.getTotalChunks())
                .mapToObj(i -> {
                    SysFileChunkInfo chunk = new SysFileChunkInfo();
                    chunk.setId(SxwlSnowFlakeUtils.nextId());
                    chunk.setUploadId(sessionId);
                    chunk.setChunkIndex(i);
                    chunk.setChunkSize((long) dto.getChunkSize());
                    chunk.setStatus(0);
                    chunk.setCreateTime(LocalDateTime.now());
                    chunk.setDeleteFlag(0);
                    return chunk;
                })
                .collect(Collectors.toList());
        sysFileChunkInfoMapper.batchInsert(chunks);

        log.info("Init upload session: id={}, fileMd5={}, totalChunks={}", sessionId, dto.getFileMd5(), dto.getTotalChunks());
        return sessionId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UploadChunkDTO uploadChunk(Long uploadId, Integer chunkIndex, String chunkMd5, MultipartFile file) {
        // 校验会话存在且未完成
        SysFileSessionInfo session = sysFileSessionInfoMapper.getById(uploadId);
        if (session == null || session.getStatus() != SESSION_STATUS_UPLOADING) {
            throw new IllegalArgumentException("上传会话不存在或已结束: uploadId=" + uploadId);
        }
        if (chunkIndex < 0 || chunkIndex >= session.getTotalChunks()) {
            throw new IllegalArgumentException("分片序号超出范围: " + chunkIndex);
        }

        // 上传分片到 S3 临时目录
        String objectKey = properties.getTempPathPrefix() + uploadId + "/" + chunkIndex;
        try (InputStream inputStream = file.getInputStream()) {
            rustfsTemplate.upload(
                    properties.getDefaultBucket(),
                    objectKey,
                    inputStream,
                    file.getSize(),
                    file.getContentType());
        } catch (Exception e) {
            throw new RuntimeException("分片上传到 S3 失败: uploadId=" + uploadId + ", chunkIndex=" + chunkIndex, e);
        }

        // 更新分片状态
        sysFileChunkInfoMapper.updateChunkStatus(uploadId, chunkIndex, objectKey);

        return new UploadChunkDTO(uploadId, chunkIndex);
    }

    @Override
    public ChunkCheckDTO getUploadedChunks(String fileMd5) {
        SysFileSessionInfo session = sysFileSessionInfoMapper.getByMd5(fileMd5);
        if (session == null) {
            return null;
        }
        List<Integer> uploaded = sysFileChunkInfoMapper.getUploadedChunks(session.getId());
        return new ChunkCheckDTO(session.getId(), uploaded);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysFileDTO completeUpload(UploadCompleteDTO dto) {
        Long uploadId = dto.getUploadId();
        String fileMd5 = dto.getFileMd5();

        // 校验会话
        SysFileSessionInfo session = sysFileSessionInfoMapper.getById(uploadId);
        if (session == null) {
            throw new IllegalArgumentException("上传会话不存在: uploadId=" + uploadId);
        }
        if (session.getStatus() != SESSION_STATUS_UPLOADING) {
            throw new IllegalArgumentException("上传会话已结束: uploadId=" + uploadId);
        }

        // 校验所有分片已上传
        int uploadedCount = sysFileChunkInfoMapper.countUploadedChunks(uploadId);
        if (uploadedCount != session.getTotalChunks()) {
            throw new IllegalArgumentException("分片未全部上传: " + uploadedCount + "/" + session.getTotalChunks());
        }

        // 获取所有分片信息
        List<SysFileChunkInfo> chunks = sysFileChunkInfoMapper.getChunksByUploadId(uploadId);
        List<String> sourceKeys = chunks.stream()
                .map(SysFileChunkInfo::getObjectKey)
                .collect(Collectors.toList());

        // 生成最终对象键
        String originalName = session.getOriginalName();
        String suffix = "";
        int dotIndex = originalName.lastIndexOf(".");
        if (dotIndex > 0) {
            suffix = originalName.substring(dotIndex);
        }
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String finalObjectKey = datePrefix + "/" + uuid + suffix;

        // 合并分片
        String bucket = properties.getDefaultBucket();
        try {
            rustfsTemplate.composeObject(bucket, sourceKeys, finalObjectKey);
        } catch (Exception e) {
            throw new RuntimeException("分片合并失败: uploadId=" + uploadId, e);
        }

        // 写入 sys_file_info
        SysFileInfo fileInfo = new SysFileInfo();
        fileInfo.setFileName(originalName);
        fileInfo.setObjectKey(finalObjectKey);
        fileInfo.setFileSize(session.getFileSize());
        fileInfo.setFileType(session.getContentType() != null ? session.getContentType() : "application/octet-stream");
        fileInfo.setFileSuffix(suffix.isEmpty() ? null : suffix.substring(1));
        fileInfo.setBucketName(bucket);
        fileInfo.setMd5(fileMd5);
        fileInfo.setStatus(FILE_STATUS_NORMAL);
        sysFileInfoMapper.insertFile(fileInfo);

        // 更新会话状态为已完成
        sysFileSessionInfoMapper.updateStatus(uploadId, SESSION_STATUS_COMPLETED);

        // 清理临时分片
        try {
            rustfsTemplate.deleteByPrefix(bucket, properties.getTempPathPrefix() + uploadId + "/");
        } catch (Exception e) {
            log.warn("清理临时分片失败: uploadId={}", uploadId, e);
        }

        log.info("Complete upload: uploadId={}, fileId={}, objectKey={}", uploadId, fileInfo.getId(), finalObjectKey);
        return buildSysFileDTO(fileInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysFileDTO simpleUpload(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isEmpty()) {
            originalName = "unknown";
        }

        String suffix = "";
        int dotIndex = originalName.lastIndexOf(".");
        if (dotIndex > 0) {
            suffix = originalName.substring(dotIndex);
        }
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String objectKey = datePrefix + "/" + uuid + suffix;

        String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";

        // 上传到 S3
        String bucket = properties.getDefaultBucket();
        try (InputStream inputStream = file.getInputStream()) {
            rustfsTemplate.upload(bucket, objectKey, inputStream, file.getSize(), contentType);
        } catch (Exception e) {
            throw new RuntimeException("简单上传到 S3 失败: " + originalName, e);
        }

        // 写入 sys_file_info
        SysFileInfo fileInfo = new SysFileInfo();
        fileInfo.setFileName(originalName);
        fileInfo.setObjectKey(objectKey);
        fileInfo.setFileSize(file.getSize());
        fileInfo.setFileType(contentType);
        fileInfo.setFileSuffix(suffix.isEmpty() ? null : suffix.substring(1));
        fileInfo.setBucketName(bucket);
        fileInfo.setStatus(FILE_STATUS_NORMAL);
        sysFileInfoMapper.insertFile(fileInfo);

        return buildSysFileDTO(fileInfo);
    }

    @Override
    public ResponseEntity<Resource> downloadFile(Long id) {
        SysFileInfo fileInfo = sysFileInfoMapper.getVisibleFileById(id);
        if (fileInfo == null) {
            throw new IllegalArgumentException("文件不存在: id=" + id);
        }

        InputStream inputStream = rustfsTemplate.download(
                fileInfo.getBucketName() != null ? fileInfo.getBucketName() : properties.getDefaultBucket(),
                fileInfo.getObjectKey());

        Resource resource = new InputStreamResource(inputStream);

        String encodedFileName = URLEncoder.encode(fileInfo.getFileName(), StandardCharsets.UTF_8)
                .replace("+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileInfo.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename*=UTF-8''" + encodedFileName)
                .body(resource);
    }

    @Override
    public String getPresignedUrl(Long id) {
        SysFileInfo fileInfo = sysFileInfoMapper.getVisibleFileById(id);
        if (fileInfo == null) {
            throw new IllegalArgumentException("文件不存在: id=" + id);
        }

        return rustfsTemplate.generatePresignedUrl(
                fileInfo.getBucketName() != null ? fileInfo.getBucketName() : properties.getDefaultBucket(),
                fileInfo.getObjectKey(),
                Duration.ofSeconds(properties.getPresignedUrlExpire()));
    }

    @Override
    public SysFileDTO checkMd5(String md5) {
        SysFileInfo fileInfo = sysFileInfoMapper.getFileByMd5(md5);
        if (fileInfo == null) {
            return null;
        }
        return buildSysFileDTO(fileInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFile(Long id) {
        SysFileInfo fileInfo = sysFileInfoMapper.getVisibleFileById(id);
        if (fileInfo == null) {
            throw new IllegalArgumentException("文件不存在: id=" + id);
        }
        sysFileInfoMapper.deleteFileById(id);
        log.info("Soft delete file: id={}, objectKey={}", id, fileInfo.getObjectKey());
    }

    @Override
    public PageInfo<SysFileDTO> getFilePageByParams(SysFilePageParams params) {
        List<SysFileDTO> rows = sysFileInfoMapper.selectFilePageByParams(params);
        return new PageInfo<>(rows);
    }

    /**
     * 构建 SysFileDTO
     */
    private SysFileDTO buildSysFileDTO(SysFileInfo entity) {
        SysFileDTO dto = new SysFileDTO();
        dto.setId(entity.getId());
        dto.setFileName(entity.getFileName());
        dto.setFileSize(entity.getFileSize());
        dto.setFileType(entity.getFileType());
        dto.setFileSuffix(entity.getFileSuffix());
        dto.setMd5(entity.getMd5());
        dto.setBusinessType(entity.getBusinessType());
        dto.setCreateTime(entity.getCreateTime() != null
                ? entity.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                : null);
        return dto;
    }
}

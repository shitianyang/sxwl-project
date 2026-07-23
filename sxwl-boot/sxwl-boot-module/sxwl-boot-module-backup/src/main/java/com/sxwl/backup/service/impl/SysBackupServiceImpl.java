package com.sxwl.backup.service.impl;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.sxwl.backup.dto.SysBackupDTO;
import com.sxwl.backup.service.SysBackupService;
import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.rustfs.client.SxwlRustfsTemplate;
import com.sxwl.rustfs.config.SxwlRustfsProperties;
import com.sxwl.rustfs.mapper.SysFileInfoMapper;
import com.sxwl.rustfs.model.dto.SysFileDTO;
import com.sxwl.rustfs.model.entity.SysFileInfo;
import com.sxwl.rustfs.model.params.SysFilePageParams;
import com.sxwl.websocket.manager.SxwlWebSocketSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 数据备份 Service 实现
 *
 * <p>调用 pg_dump 命令行工具备份数据库，备份文件上传至 RustFS S3 存储，
 * 备份记录写入 sys_file_info（business_type = 'db_backup'）。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@Service
public class SysBackupServiceImpl implements SysBackupService {

    private static final Logger log = LoggerFactory.getLogger(SysBackupServiceImpl.class);

    private static final String BACKUP_BUSINESS_TYPE = "db_backup";

    private final SysFileInfoMapper sysFileInfoMapper;
    private final SxwlRustfsTemplate rustfsTemplate;
    private final SxwlRustfsProperties rustfsProperties;
    private final SxwlWebSocketSessionManager wsSessionManager;

    @Value("${spring.datasource.url:}")
    private String datasourceUrl;

    @Value("${spring.datasource.username:}")
    private String datasourceUsername;

    @Value("${spring.datasource.password:}")
    private String datasourcePassword;

    @Value("${sxwl.backup.pg-dump-path:pg_dump}")
    private String pgDumpPath;

    @Value("${sxwl.backup.psql-path:psql}")
    private String psqlPath;

    public SysBackupServiceImpl(SysFileInfoMapper sysFileInfoMapper,
                                SxwlRustfsTemplate rustfsTemplate,
                                SxwlRustfsProperties rustfsProperties,
                                SxwlWebSocketSessionManager wsSessionManager) {
        this.sysFileInfoMapper = sysFileInfoMapper;
        this.rustfsTemplate = rustfsTemplate;
        this.rustfsProperties = rustfsProperties;
        this.wsSessionManager = wsSessionManager;
    }

    @Override
    @Async
    public void backup(Long userId) {
        log.info("开始执行数据备份...");
        sendProgress(userId, 5, "开始备份数据库...");
        try {
            String dbName = parseDbNameFromUrl(datasourceUrl);
            if (dbName == null) {
                throw new SxwlBusinessException(10001, "无法解析数据库名称，请检查 datasource.url 配置");
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String backupFileName = "backup_" + dbName + "_" + timestamp + ".sql.gz";

            log.info("备份文件名: {}, 数据库: {}", backupFileName, dbName);

            // 1. 执行 pg_dump，捕获 stdout
            ProcessBuilder dumpPb = new ProcessBuilder(
                    pgDumpPath,
                    "-h", parseHostFromUrl(datasourceUrl),
                    "-p", parsePortFromUrl(datasourceUrl),
                    "-U", datasourceUsername,
                    "-d", dbName,
                    "--no-owner",
                    "--no-acl",
                    "--no-password"
            );
            dumpPb.environment().put("PGPASSWORD", datasourcePassword);
            dumpPb.redirectErrorStream(false);

            Process dumpProcess = dumpPb.start();

            // 2. 读取 stdout 并 gzip 压缩
            ByteArrayOutputStream gzippedOs = new ByteArrayOutputStream(64 * 1024 * 1024);
            try (GZIPOutputStream gzipOs = new GZIPOutputStream(gzippedOs);
                 InputStream pgStdout = dumpProcess.getInputStream()) {
                pgStdout.transferTo(gzipOs);
                gzipOs.finish();
            }

            int exitCode = dumpProcess.waitFor();
            if (exitCode != 0) {
                // 读取 stderr 获取错误详情
                String errorMsg = new String(dumpProcess.getErrorStream().readAllBytes());
                log.error("pg_dump 执行失败, exitCode={}, error={}", exitCode, errorMsg);
                throw new SxwlBusinessException(10001, "数据库备份执行失败: " + errorMsg);
            }

            byte[] gzippedData = gzippedOs.toByteArray();
            log.info("pg_dump 完成, 压缩后大小: {} bytes", gzippedData.length);
            sendProgress(userId, 50, "数据库导出完成，压缩上传中...");

            // 3. 上传到 RustFS S3
            String bucket = rustfsProperties.getDefaultBucket();
            String objectKey = "backup/" + timestamp + "/" + UUID.randomUUID().toString().replace("-", "") + ".sql.gz";

            rustfsTemplate.upload(bucket, objectKey,
                    new ByteArrayInputStream(gzippedData),
                    gzippedData.length,
                    "application/gzip");

            log.info("备份文件已上传至 S3: bucket={}, key={}", bucket, objectKey);

            // 4. 记录到 sys_file_info
            SysFileInfo fileInfo = new SysFileInfo();
            fileInfo.setFileName(backupFileName);
            fileInfo.setObjectKey(objectKey);
            fileInfo.setFileUrl(null); // 由 presigned URL 按需生成
            fileInfo.setFileSize((long) gzippedData.length);
            fileInfo.setFileType("application/gzip");
            fileInfo.setFileSuffix("sql.gz");
            fileInfo.setBucketName(bucket);
            fileInfo.setBusinessType(BACKUP_BUSINESS_TYPE);
            fileInfo.setStatus(1); // 正常
            fileInfo.setDescription("数据库备份 " + timestamp);
            sysFileInfoMapper.insertFile(fileInfo);

            log.info("数据备份完成: fileName={}, fileId={}", backupFileName, fileInfo.getId());
            sendProgress(userId, 100, "备份完成");

        } catch (SxwlBusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("数据备份异常", e);
            throw new SxwlBusinessException(10001, "数据库备份异常: " + e.getMessage());
        }
    }

    @Override
    public PageInfo<SysBackupDTO> list(int page, int size) {
        SysFilePageParams params = new SysFilePageParams();
        params.setBusinessType(BACKUP_BUSINESS_TYPE);
        params.setCurrent(page);
        params.setPageSize(size);

        PageMethod.startPage(page, size);
        List<SysFileDTO> fileList = sysFileInfoMapper.selectFilePageByParams(params);
        List<SysBackupDTO> backupList = fileList.stream()
                .map(this::toBackupDTO)
                .toList();
        return new PageInfo<>(backupList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restore(Long fileId) {
        log.info("开始恢复备份: fileId={}", fileId);

        SysFileInfo fileInfo = sysFileInfoMapper.getFileById(fileId);
        if (fileInfo == null) {
            throw new SxwlBusinessException(10004, "备份记录不存在");
        }
        if (!BACKUP_BUSINESS_TYPE.equals(fileInfo.getBusinessType())) {
            throw new SxwlBusinessException(10001, "该记录不是数据库备份文件");
        }

        String bucket = fileInfo.getBucketName() != null ? fileInfo.getBucketName() : rustfsProperties.getDefaultBucket();
        String objectKey = fileInfo.getObjectKey();
        if (objectKey == null || objectKey.isEmpty()) {
            throw new SxwlBusinessException(10001, "备份文件对象键为空，无法恢复");
        }

        // 高风险操作当前仅打印 warn 日志，暂不自动执行
        log.warn("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        log.warn("!! 备份恢复请求: fileId={}, fileName={}", fileId, fileInfo.getFileName());
        log.warn("!! 此操作将使用 S3 对象恢复数据库: bucket={}, key={}", bucket, objectKey);
        log.warn("!! 恢复尚未自动执行，需人工确认后手动操作。");
        log.warn("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        throw new SxwlBusinessException(10001,
                "备份恢复功能尚未开放自动执行。如需手动恢复，请执行: pg_dump 备份文件路径=" + objectKey);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        log.info("删除备份文件: id={}", id);

        SysFileInfo fileInfo = sysFileInfoMapper.getFileById(id);
        if (fileInfo == null) {
            throw new SxwlBusinessException(10004, "备份记录不存在");
        }

        // 1. 删除 S3 对象
        String bucket = fileInfo.getBucketName() != null ? fileInfo.getBucketName() : rustfsProperties.getDefaultBucket();
        String objectKey = fileInfo.getObjectKey();
        if (objectKey != null && !objectKey.isEmpty()) {
            try {
                rustfsTemplate.delete(bucket, objectKey);
                log.info("S3 对象已删除: bucket={}, key={}", bucket, objectKey);
            } catch (Exception e) {
                log.warn("删除 S3 对象失败（已忽略）: bucket={}, key={}", bucket, objectKey, e);
            }
        }

        // 2. 软删除数据库记录
        int affected = sysFileInfoMapper.deleteFileById(id);
        if (affected == 0) {
            throw new SxwlBusinessException(10004, "备份记录不存在或已被删除");
        }

        log.info("备份文件删除成功: id={}", id);
    }

    // ==================== 内部工具方法 ====================

    /**
     * 通过 WebSocket 推送备份进度
     */
    private void sendProgress(Long userId, int progress, String message) {
        if (userId == null) return;
        try {
            String payload = "{\"type\":\"backup:progress\",\"data\":{\"progress\":" + progress + ",\"message\":\"" + message + "\"}}";
            wsSessionManager.sendToUser(userId, payload);
        } catch (Exception e) {
            log.warn("WebSocket 进度推送失败: userId={}, progress={}", userId, progress, e);
        }
    }

    /**
     * 将 SysFileDTO 转换为 SysBackupDTO
     */
    private SysBackupDTO toBackupDTO(SysFileDTO file) {
        SysBackupDTO dto = new SysBackupDTO();
        dto.setId(file.getId());
        dto.setFileName(file.getFileName());
        dto.setFileSize(file.getFileSize());
        dto.setFileSizeDisplay(formatFileSize(file.getFileSize()));
        dto.setFileUrl(file.getPresignedUrl());
        dto.setStatus(1);
        dto.setCreateTime(file.getCreateTime());
        return dto;
    }

    /**
     * 格式化文件大小（字节 → 可读格式）
     */
    private String formatFileSize(Long bytes) {
        if (bytes == null) return "0 B";
        final String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = bytes;
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        return String.format("%.2f %s", size, units[unitIndex]);
    }

    private String parseDbNameFromUrl(String url) {
        if (url == null || url.isEmpty()) return null;
        int lastSlash = url.lastIndexOf('/');
        if (lastSlash < 0) return null;
        String dbPart = url.substring(lastSlash + 1);
        int questionMark = dbPart.indexOf('?');
        return questionMark > 0 ? dbPart.substring(0, questionMark) : dbPart;
    }

    private String parseHostFromUrl(String url) {
        if (url == null || url.isEmpty()) return "localhost";
        int start = url.indexOf("://");
        if (start < 0) return "localhost";
        int colon = url.indexOf(':', start + 3);
        if (colon < 0) return "localhost";
        return url.substring(start + 3, colon);
    }

    private String parsePortFromUrl(String url) {
        if (url == null || url.isEmpty()) return "5432";
        int start = url.indexOf("://");
        if (start < 0) return "5432";
        int colon = url.indexOf(':', start + 3);
        if (colon < 0) return "5432";
        int slash = url.indexOf('/', colon + 1);
        if (slash < 0) return "5432";
        return url.substring(colon + 1, slash);
    }
}


package com.sxwl.backup.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.sxwl.redis.helper.SxwlRedisHelper;
import com.sxwl.security.model.SxwlLoginUser;
import com.sxwl.security.utils.SxwlSecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.time.Duration;
import java.util.UUID;
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
    private static final String AUTO_BACKUP_RUNNING_KEY = "sys:backup:auto_running";  // Redis 标记自动备份是否运行中
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final SysFileInfoMapper sysFileInfoMapper;
    private final SxwlRustfsTemplate rustfsTemplate;
    private final SxwlRustfsProperties rustfsProperties;
    private final SxwlWebSocketSessionManager wsSessionManager;
    private final SxwlRedisHelper redisHelper;

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
                                SxwlWebSocketSessionManager wsSessionManager,
                                SxwlRedisHelper redisHelper) {
        this.sysFileInfoMapper = sysFileInfoMapper;
        this.rustfsTemplate = rustfsTemplate;
        this.rustfsProperties = rustfsProperties;
        this.wsSessionManager = wsSessionManager;
        this.redisHelper = redisHelper;
    }

    @PostConstruct
    public void init() {
        // 验证 pg_dump 和 psql 路径是否有效
        try {
            ProcessBuilder pb1 = new ProcessBuilder(pgDumpPath, "--version");
            Process p1 = pb1.start();
            int exitCode1 = p1.waitFor();
            if (exitCode1 != 0) {
                log.warn("pg_dump 路径可能无效: {}, 请在 application.yaml 中配置正确的路径 (sxwl.backup.pg-dump-path)", pgDumpPath);
            } else {
                log.info("pg_dump 路径验证通过: {}", pgDumpPath);
            }
        } catch (Exception e) {
            log.warn("pg_dump 验证失败: {}. 请确保 {} 在系统 PATH 中，或通过 sxwl.backup.pg-dump-path 配置完整路径", e.getMessage(), pgDumpPath);
        }

        try {
            ProcessBuilder pb2 = new ProcessBuilder(psqlPath, "--version");
            Process p2 = pb2.start();
            int exitCode2 = p2.waitFor();
            if (exitCode2 != 0) {
                log.warn("psql 路径可能无效: {}, 请在 application.yaml 中配置正确的路径 (sxwl.backup.psql-path)", psqlPath);
            } else {
                log.info("psql 路径验证通过: {}", psqlPath);
            }
        } catch (Exception e) {
            log.warn("psql 验证失败: {}. 请确保 {} 在系统 PATH 中，或通过 sxwl.backup.psql-path 配置完整路径", e.getMessage(), psqlPath);
        }
    }

    @Override
    @Async("backupExecutor")
    public void backup(Long userId, Long orgId) {
        log.info("开始执行数据备份...");
        sendProgress(userId, 5, "开始备份数据库...");
        Path backupPath = null;
        Path errorPath = null;
        try {
            String dbName = parseDbNameFromUrl(datasourceUrl);
            if (dbName == null) {
                throw new SxwlBusinessException(10001, "无法解析数据库名称，请检查 datasource.url 配置");
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String backupFileName = "backup_" + dbName + "_" + timestamp + ".sql.gz";

            log.info("备份文件名: {}, 数据库: {}", backupFileName, dbName);

            backupPath = Files.createTempFile("sxwl-backup-", ".sql.gz");
            errorPath = Files.createTempFile("sxwl-backup-", ".err");

            // 1. 执行 pg_dump。stderr 重定向到文件，避免管道缓冲区阻塞子进程。
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
            dumpPb.redirectError(errorPath.toFile());

            Process dumpProcess = dumpPb.start();

            // 2. 读取 stdout 并直接写入临时 gzip 文件，避免整个备份常驻堆内存。
            try (OutputStream fileOutput = Files.newOutputStream(backupPath);
                 GZIPOutputStream gzipOs = new GZIPOutputStream(fileOutput);
                 InputStream pgStdout = dumpProcess.getInputStream()) {
                pgStdout.transferTo(gzipOs);
            }

            int exitCode = dumpProcess.waitFor();
            if (exitCode != 0) {
                String errorMsg = readError(errorPath);
                log.error("pg_dump 执行失败, exitCode={}, error={}", exitCode, errorMsg);
                throw new SxwlBusinessException(10001, "数据库备份执行失败: " + errorMsg);
            }

            long backupSize = Files.size(backupPath);
            log.info("pg_dump 完成, 压缩后大小: {} bytes", backupSize);
            sendProgress(userId, 50, "数据库导出完成，压缩上传中...");

            // 3. 上传到 RustFS S3
            String bucket = rustfsProperties.getDefaultBucket();
            String objectKey = "backup/" + timestamp + "/" + UUID.randomUUID().toString().replace("-", "") + ".sql.gz";

            try (InputStream backupInput = new BufferedInputStream(Files.newInputStream(backupPath))) {
                rustfsTemplate.upload(bucket, objectKey, backupInput, "application/gzip");
            }

            log.info("备份文件已上传至 S3: bucket={}, key={}", bucket, objectKey);

            // 4. 记录到 sys_file_info
            // 注意：本方法运行在 @Async 独立线程，SecurityContext 不可达，
            // SxwlAutoFillInterceptor 无法自动填充 create_by/create_org，
            // 因此必须显式设置，否则会因 NOT NULL 约束导致插入失败（备份失效 + S3 孤儿对象）。
            SysFileInfo fileInfo = new SysFileInfo();
            fileInfo.setFileName(backupFileName);
            fileInfo.setObjectKey(objectKey);
            fileInfo.setFileUrl(null); // 由 presigned URL 按需生成
            fileInfo.setFileSize(backupSize);
            fileInfo.setFileType("application/gzip");
            fileInfo.setFileSuffix("sql.gz");
            fileInfo.setBucketName(bucket);
            fileInfo.setBusinessType(BACKUP_BUSINESS_TYPE);
            fileInfo.setStatus(1); // 正常
            fileInfo.setDescription("数据库备份 " + timestamp);
            fileInfo.setCreateBy(userId);
            fileInfo.setCreateOrg(orgId);
            sysFileInfoMapper.insertFile(fileInfo);

            log.info("数据备份完成: fileName={}, fileId={}", backupFileName, fileInfo.getId());
            sendProgress(userId, 100, "备份完成");

        } catch (SxwlBusinessException e) {
            sendProgress(userId, -1, "备份失败: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("数据备份异常", e);
            sendProgress(userId, -1, "备份失败: " + e.getMessage());
            throw new SxwlBusinessException(10001, "数据库备份异常: " + e.getMessage());
        } finally {
            deleteTempFile(backupPath);
            deleteTempFile(errorPath);
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
        // 注意：stream().toList() 得到的是普通 List，PageHelper 的分页信息（total/pages）会丢失。
        // 从运行时实际的 Page 对象回填分页元数据，避免前端分页总数/页数错误。
        PageInfo<SysBackupDTO> pageInfo = new PageInfo<>(backupList);
        if (fileList instanceof com.github.pagehelper.Page<?> p) {
            pageInfo.setTotal(p.getTotal());
            pageInfo.setPages(p.getPages());
        }
        return pageInfo;
    }

    @Override
    public void restore(Long fileId) {
        log.info("开始恢复备份: fileId={}", fileId);

        SysFileInfo fileInfo = sysFileInfoMapper.getVisibleFileById(fileId);
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

        SysFileInfo fileInfo = sysFileInfoMapper.getVisibleFileById(id);
        if (fileInfo == null) {
            throw new SxwlBusinessException(10004, "备份记录不存在");
        }

        // 先逻辑删除 DB 记录（事务内，确保可见性先消失），再清理 S3 对象。
        // 若 S3 删除失败，仅遗留无引用的孤儿存储（无破窗链接），优于 S3 已删而 DB 仍可见（L26）。
        int affected = sysFileInfoMapper.deleteFileById(id);
        if (affected == 0) {
            throw new SxwlBusinessException(10004, "备份记录不存在或已被删除");
        }

        // 2. 删除 S3 对象（失败仅告警，DB 已不可见，对象可后续清理）
        String bucket = fileInfo.getBucketName() != null ? fileInfo.getBucketName() : rustfsProperties.getDefaultBucket();
        String objectKey = fileInfo.getObjectKey();
        if (objectKey != null && !objectKey.isEmpty()) {
            try {
                rustfsTemplate.delete(bucket, objectKey);
                log.info("S3 对象已删除: bucket={}, key={}", bucket, objectKey);
            } catch (Exception e) {
                log.error("删除 S3 对象失败（DB 记录已删除）: bucket={}, key={}", bucket, objectKey, e);
            }
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
            Map<String, Object> data = Map.of(
                    "progress", progress,
                    "message", message
            );
            Map<String, Object> payload = Map.of(
                    "type", "backup:progress",
                    "data", data
            );
            String json = objectMapper.writeValueAsString(payload);
            wsSessionManager.sendToUser(userId, json);
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

    private String readError(Path errorPath) throws java.io.IOException {
        try (InputStream input = Files.newInputStream(errorPath)) {
            return new String(input.readNBytes(8192), StandardCharsets.UTF_8);
        }
    }

    private void deleteTempFile(Path path) {
        if (path == null) return;
        try {
            Files.deleteIfExists(path);
        } catch (Exception e) {
            log.warn("清理备份临时文件失败: {}", path, e);
        }
    }

    private String parseDbNameFromUrl(String url) {
        try {
            if (url == null || url.isEmpty()) return null;
            int lastSlash = url.lastIndexOf('/');
            if (lastSlash < 0) return null;
            String dbPart = url.substring(lastSlash + 1);
            int questionMark = dbPart.indexOf('?');
            return questionMark > 0 ? dbPart.substring(0, questionMark) : dbPart;
        } catch (Exception e) {
            log.error("解析数据库名称失败: url={}, error={}", url, e.getMessage());
            return null;
        }
    }

    private String parseHostFromUrl(String url) {
        try {
            if (url == null || url.isEmpty()) return "localhost";
            // jdbc:postgresql://host:port/db
            int start = url.indexOf("://");
            if (start < 0) return "localhost";
            int colon = url.indexOf(':', start + 3);
            if (colon < 0) return "localhost";
            return url.substring(start + 3, colon);
        } catch (Exception e) {
            log.error("解析数据库主机失败: url={}, error={}", url, e.getMessage());
            return "localhost";
        }
    }

    private String parsePortFromUrl(String url) {
        try {
            if (url == null || url.isEmpty()) return "5432";
            // jdbc:postgresql://host:port/db
            int start = url.indexOf("://");
            if (start < 0) return "5432";
            int colon = url.indexOf(':', start + 3);
            if (colon < 0) return "5432";
            int slash = url.indexOf('/', colon + 1);
            if (slash < 0) return "5432";
            return url.substring(colon + 1, slash);
        } catch (Exception e) {
            log.error("解析数据库端口失败: url={}, error={}", url, e.getMessage());
            return "5432";
        }
    }

    // ==================== 定时任务管理实现 ====================

    @Override
    public void autoBackup() {
        // 检查是否已有自动备份在运行（防止并发执行）
        if (isAutoBackupRunning()) {
            log.warn("自动备份已在运行中，跳过本次调度");
            return;
        }

        // 设置运行状态
        setAutoBackupRunning(true);
        log.info("开始自动备份任务...");

        try {
            // 尝试从 SecurityContext 获取当前用户（如果是 Quartz 触发，可能为 null）
            SxwlLoginUser loginUser = SxwlSecurityUtils.getCurrentUser().orElse(null);
            Long userId = loginUser != null ? loginUser.getUserId() : 0L;  // 0 表示系统自动
            Long orgId = loginUser != null ? loginUser.getOrgId() : null;

            // 调用异步备份方法
            backup(userId, orgId);

            log.info("自动备份任务完成");
        } finally {
            // 清除运行状态
            setAutoBackupRunning(false);
        }
    }

    @Override
    public boolean isAutoBackupRunning() {
        try {
            return Boolean.TRUE.equals(redisHelper.exists(AUTO_BACKUP_RUNNING_KEY));
        } catch (Exception e) {
            log.error("检查自动备份运行状态失败", e);
            return false;  // 默认返回 false，不影响备份执行
        }
    }

    @Override
    public void setAutoBackupRunning(boolean running) {
        try {
            if (running) {
                redisHelper.set(AUTO_BACKUP_RUNNING_KEY, "1", Duration.ofHours(1));  // TTL 1 小时
            } else {
                redisHelper.delete(AUTO_BACKUP_RUNNING_KEY);
            }
        } catch (Exception e) {
            log.error("设置自动备份运行状态失败: running={}", running, e);
        }
    }
}

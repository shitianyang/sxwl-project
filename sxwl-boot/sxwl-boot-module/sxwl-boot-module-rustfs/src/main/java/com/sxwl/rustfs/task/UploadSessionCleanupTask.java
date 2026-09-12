package com.sxwl.rustfs.task;

import com.sxwl.rustfs.service.SysFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 文件上传会话清理定时任务
 *
 * <p>负责定期清理超过指定时间（默认 24 小时）的未完成上传会话，释放存储空间。</p>
 *
 * @author shitianyang
 * @since 0.1.0
 */
@Component
public class UploadSessionCleanupTask {

    private static final Logger log = LoggerFactory.getLogger(UploadSessionCleanupTask.class);

    private final SysFileService sysFileService;

    public UploadSessionCleanupTask(SysFileService sysFileService) {
        this.sysFileService = sysFileService;
    }

    /**
     * 每天凌晨 2 点执行清理任务
     *
     * <p>清理超过 24 小时的未完成上传会话</p>
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void executeCleanup() {
        log.info("开始执行上传会话清理任务");
        try {
            int cleanedCount = sysFileService.cleanupExpiredUploadSessions(24);
            log.info("上传会话清理任务完成: 清理 {} 个过期会话", cleanedCount);
        } catch (Exception e) {
            log.error("上传会话清理任务执行失败", e);
        }
    }
}

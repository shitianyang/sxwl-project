package com.sxwl.backup.service;

import com.sxwl.backup.dto.SysBackupDTO;
import com.github.pagehelper.PageInfo;

/**
 * 数据备份 Service 接口（扩展定时任务管理）
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public interface SysBackupService {

    /**
     * 执行备份（异步）
     *
     * @param userId 发起备份的用户 ID，用于 WebSocket 进度推送
     * @param orgId  发起备份的用户所属组织 ID，因异步线程无 SecurityContext，
     *               需由调用方同步传入，用于填充 sys_file_info 的 create_org（NOT NULL 约束）
     */
    void backup(Long userId, Long orgId);

    /**
     * 备份文件列表（分页）
     */
    PageInfo<SysBackupDTO> list(int page, int size);

    /**
     * 恢复备份（高风险）
     */
    void restore(Long fileId);

    /**
     * 删除备份文件
     */
    void delete(Long id);

    // ==================== 定时任务管理方法 ====================

    /**
     * 自动备份入口（供 Quartz Job 调用）
     * <p>该方法无参数，内部通过 SecurityContext 或默认值获取 userId/orgId。</p>
     */
    void autoBackup();

    /**
     * 检查是否已存在正在进行的定时备份任务
     *
     * @return true 表示已有定时备份在运行
     */
    boolean isAutoBackupRunning();

    /**
     * 设置定时备份运行状态
     *
     * @param running true 表示正在运行
     */
    void setAutoBackupRunning(boolean running);
}

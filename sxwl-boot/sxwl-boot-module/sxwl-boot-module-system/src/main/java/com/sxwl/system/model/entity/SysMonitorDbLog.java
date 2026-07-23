package com.sxwl.system.model.entity;

import java.time.LocalDateTime;

/**
 * 系统监控-数据库指标日志
 *
 * <p>对应数据库 {@code sys_monitor_db_log} 表。纯时序记录，无审计字段。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public class SysMonitorDbLog {

    /** 雪花ID */
    private Long id;

    /** 活跃连接数 */
    private Integer activeConnections;

    /** 记录时间 */
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getActiveConnections() { return activeConnections; }
    public void setActiveConnections(Integer activeConnections) { this.activeConnections = activeConnections; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}

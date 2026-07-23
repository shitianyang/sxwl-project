package com.sxwl.system.model.entity;

import java.time.LocalDateTime;

/**
 * 系统监控-Redis 指标日志
 *
 * <p>对应数据库 {@code sys_monitor_redis_log} 表。纯时序记录，无审计字段。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public class SysMonitorRedisLog {

    /** 雪花ID */
    private Long id;

    /** 已连接客户端数 */
    private Integer connectedClients;

    /** Redis 内存使用（字节） */
    private Long usedMemory;

    /** 缓存命中率（0~100） */
    private Double hitRate;

    /** Key 总数 */
    private Integer totalKeys;

    /** 记录时间 */
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getConnectedClients() { return connectedClients; }
    public void setConnectedClients(Integer connectedClients) { this.connectedClients = connectedClients; }

    public Long getUsedMemory() { return usedMemory; }
    public void setUsedMemory(Long usedMemory) { this.usedMemory = usedMemory; }

    public Double getHitRate() { return hitRate; }
    public void setHitRate(Double hitRate) { this.hitRate = hitRate; }

    public Integer getTotalKeys() { return totalKeys; }
    public void setTotalKeys(Integer totalKeys) { this.totalKeys = totalKeys; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}

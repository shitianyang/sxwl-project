package com.sxwl.system.model.entity;

import java.time.LocalDateTime;

/**
 * 系统监控-服务器指标日志
 *
 * <p>对应数据库 {@code sys_monitor_server_log} 表。纯时序记录，无审计字段。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public class SysMonitorServerLog {

    /** 雪花ID */
    private Long id;

    /** CPU 负载百分比（0~100） */
    private Double cpuLoad;

    /** 已用内存（字节） */
    private Long memUsed;

    /** 总内存（字节） */
    private Long memTotal;

    /** 已用磁盘（字节） */
    private Long diskUsed;

    /** 总磁盘（字节） */
    private Long diskTotal;

    /** 记录时间 */
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getCpuLoad() { return cpuLoad; }
    public void setCpuLoad(Double cpuLoad) { this.cpuLoad = cpuLoad; }

    public Long getMemUsed() { return memUsed; }
    public void setMemUsed(Long memUsed) { this.memUsed = memUsed; }

    public Long getMemTotal() { return memTotal; }
    public void setMemTotal(Long memTotal) { this.memTotal = memTotal; }

    public Long getDiskUsed() { return diskUsed; }
    public void setDiskUsed(Long diskUsed) { this.diskUsed = diskUsed; }

    public Long getDiskTotal() { return diskTotal; }
    public void setDiskTotal(Long diskTotal) { this.diskTotal = diskTotal; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}

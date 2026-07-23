package com.sxwl.system.model.entity;

import java.time.LocalDateTime;

/**
 * 系统监控-JVM 指标日志
 *
 * <p>对应数据库 {@code sys_monitor_jvm_log} 表。纯时序记录，无审计字段。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public class SysMonitorJvmLog {

    /** 雪花ID */
    private Long id;

    /** 堆内存已用（字节） */
    private Long heapUsed;

    /** 堆内存最大值（字节） */
    private Long heapMax;

    /** 堆内存提交值（字节） */
    private Long heapCommitted;

    /** 当前线程数 */
    private Integer threadCount;

    /** 峰值线程数 */
    private Integer peakThreadCount;

    /** 已加载类数 */
    private Integer classLoadedCount;

    /** 记录时间 */
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getHeapUsed() { return heapUsed; }
    public void setHeapUsed(Long heapUsed) { this.heapUsed = heapUsed; }

    public Long getHeapMax() { return heapMax; }
    public void setHeapMax(Long heapMax) { this.heapMax = heapMax; }

    public Long getHeapCommitted() { return heapCommitted; }
    public void setHeapCommitted(Long heapCommitted) { this.heapCommitted = heapCommitted; }

    public Integer getThreadCount() { return threadCount; }
    public void setThreadCount(Integer threadCount) { this.threadCount = threadCount; }

    public Integer getPeakThreadCount() { return peakThreadCount; }
    public void setPeakThreadCount(Integer peakThreadCount) { this.peakThreadCount = peakThreadCount; }

    public Integer getClassLoadedCount() { return classLoadedCount; }
    public void setClassLoadedCount(Integer classLoadedCount) { this.classLoadedCount = classLoadedCount; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}

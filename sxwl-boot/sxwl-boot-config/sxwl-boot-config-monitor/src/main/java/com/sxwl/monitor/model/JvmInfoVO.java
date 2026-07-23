package com.sxwl.monitor.model;

import java.util.List;

/**
 * JVM 信息 VO
 *
 * <p>包含 Java 虚拟机堆内存、线程、类加载、GC 等信息。数据来源：ManagementFactory。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public class JvmInfoVO {

    /** 堆内存最大值（字节） */
    private long heapMax;

    /** 已用堆内存（字节） */
    private long heapUsed;

    /** 已提交堆内存（字节） */
    private long heapCommitted;

    /** 非堆内存已用（字节） */
    private long nonHeapUsed;

    /** 当前活跃线程数 */
    private int threadCount;

    /** 峰值线程数 */
    private int peakThreadCount;

    /** 已加载类总数 */
    private long classLoadedCount;

    /** GC 详情列表 */
    private List<GcInfoVO> gcInfos;

    public long getHeapMax() {
        return heapMax;
    }

    public void setHeapMax(long heapMax) {
        this.heapMax = heapMax;
    }

    public long getHeapUsed() {
        return heapUsed;
    }

    public void setHeapUsed(long heapUsed) {
        this.heapUsed = heapUsed;
    }

    public long getHeapCommitted() {
        return heapCommitted;
    }

    public void setHeapCommitted(long heapCommitted) {
        this.heapCommitted = heapCommitted;
    }

    public long getNonHeapUsed() {
        return nonHeapUsed;
    }

    public void setNonHeapUsed(long nonHeapUsed) {
        this.nonHeapUsed = nonHeapUsed;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public int getPeakThreadCount() {
        return peakThreadCount;
    }

    public void setPeakThreadCount(int peakThreadCount) {
        this.peakThreadCount = peakThreadCount;
    }

    public long getClassLoadedCount() {
        return classLoadedCount;
    }

    public void setClassLoadedCount(long classLoadedCount) {
        this.classLoadedCount = classLoadedCount;
    }

    public List<GcInfoVO> getGcInfos() {
        return gcInfos;
    }

    public void setGcInfos(List<GcInfoVO> gcInfos) {
        this.gcInfos = gcInfos;
    }
}

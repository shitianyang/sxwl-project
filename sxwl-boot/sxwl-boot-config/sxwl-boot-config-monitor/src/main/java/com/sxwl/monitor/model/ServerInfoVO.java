package com.sxwl.monitor.model;

/**
 * 服务器信息 VO
 *
 * <p>包含操作系统级别的 CPU、内存、磁盘使用情况。数据来源：OSHI 库。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public class ServerInfoVO {

    /** CPU 核心数 */
    private int cpuCores;

    /** CPU 使用率（百分比，0~100） */
    private double cpuLoad;

    /** 内存总量（字节） */
    private long memTotal;

    /** 已用内存（字节） */
    private long memUsed;

    /** 磁盘总量（字节） */
    private long diskTotal;

    /** 已用磁盘（字节） */
    private long diskUsed;

    public int getCpuCores() {
        return cpuCores;
    }

    public void setCpuCores(int cpuCores) {
        this.cpuCores = cpuCores;
    }

    public double getCpuLoad() {
        return cpuLoad;
    }

    public void setCpuLoad(double cpuLoad) {
        this.cpuLoad = cpuLoad;
    }

    public long getMemTotal() {
        return memTotal;
    }

    public void setMemTotal(long memTotal) {
        this.memTotal = memTotal;
    }

    public long getMemUsed() {
        return memUsed;
    }

    public void setMemUsed(long memUsed) {
        this.memUsed = memUsed;
    }

    public long getDiskTotal() {
        return diskTotal;
    }

    public void setDiskTotal(long diskTotal) {
        this.diskTotal = diskTotal;
    }

    public long getDiskUsed() {
        return diskUsed;
    }

    public void setDiskUsed(long diskUsed) {
        this.diskUsed = diskUsed;
    }
}

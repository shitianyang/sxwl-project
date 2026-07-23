package com.sxwl.monitor.model;

/**
 * GC 信息 VO
 *
 * <p>单个垃圾回收器的统计数据。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public class GcInfoVO {

    /** GC 名称，如 "G1 Young Generation" */
    private String name;

    /** GC 总次数 */
    private long count;

    /** GC 总耗时（毫秒） */
    private long totalTimeMs;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getTotalTimeMs() {
        return totalTimeMs;
    }

    public void setTotalTimeMs(long totalTimeMs) {
        this.totalTimeMs = totalTimeMs;
    }
}

package com.sxwl.system.model.dto;

/**
 * Redis 信息 DTO
 *
 * <p>通过 Redis INFO 命令获取的统计数据。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public class SysRedisInfoDTO {

    /** 已连接客户端数 */
    private Long connectedClients;

    /** Redis 内存使用量（字节） */
    private Long usedMemory;

    /** 缓存命中率（百分比） */
    private Double hitRate;

    /** Key 总数 */
    private Long totalKeys;

    public Long getConnectedClients() {
        return connectedClients;
    }

    public void setConnectedClients(Long connectedClients) {
        this.connectedClients = connectedClients;
    }

    public Long getUsedMemory() {
        return usedMemory;
    }

    public void setUsedMemory(Long usedMemory) {
        this.usedMemory = usedMemory;
    }

    public Double getHitRate() {
        return hitRate;
    }

    public void setHitRate(Double hitRate) {
        this.hitRate = hitRate;
    }

    public Long getTotalKeys() {
        return totalKeys;
    }

    public void setTotalKeys(Long totalKeys) {
        this.totalKeys = totalKeys;
    }
}

package com.sxwl.system.model.dto;

import com.sxwl.monitor.model.JvmInfoVO;
import com.sxwl.monitor.model.ServerInfoVO;

/**
 * 系统监控 SSE 推送数据 DTO
 *
 * <p>聚合服务器、JVM、Redis、数据库的实时监控数据，通过 SSE 推送给前端。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public class SysMonitorDataDTO {

    /** 时间戳（ISO-8601） */
    private String timestamp;

    /** 服务器信息 */
    private ServerInfoVO server;

    /** JVM 信息 */
    private JvmInfoVO jvm;

    /** Redis 信息 */
    private SysRedisInfoDTO redis;

    /** 数据库信息 */
    private SysDbInfoDTO db;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public ServerInfoVO getServer() {
        return server;
    }

    public void setServer(ServerInfoVO server) {
        this.server = server;
    }

    public JvmInfoVO getJvm() {
        return jvm;
    }

    public void setJvm(JvmInfoVO jvm) {
        this.jvm = jvm;
    }

    public SysRedisInfoDTO getRedis() {
        return redis;
    }

    public void setRedis(SysRedisInfoDTO redis) {
        this.redis = redis;
    }

    public SysDbInfoDTO getDb() {
        return db;
    }

    public void setDb(SysDbInfoDTO db) {
        this.db = db;
    }
}

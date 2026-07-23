package com.sxwl.system.service;

import com.sxwl.common.utils.SxwlSnowFlakeUtils;
import com.sxwl.system.mapper.SysMonitorDbLogMapper;
import com.sxwl.system.mapper.SysMonitorJvmLogMapper;
import com.sxwl.system.mapper.SysMonitorRedisLogMapper;
import com.sxwl.system.mapper.SysMonitorServerLogMapper;
import com.sxwl.system.model.dto.SysMonitorDataDTO;
import com.sxwl.system.model.entity.SysMonitorDbLog;
import com.sxwl.system.model.entity.SysMonitorJvmLog;
import com.sxwl.system.model.entity.SysMonitorRedisLog;
import com.sxwl.system.model.entity.SysMonitorServerLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 监控数据持久化服务
 *
 * <p>将采集的监控数据异步写入分表（server / jvm / redis / db）。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@Service
public class SysMonitorPersistenceService {

    private static final Logger log = LoggerFactory.getLogger(SysMonitorPersistenceService.class);

    private final SysMonitorServerLogMapper serverLogMapper;
    private final SysMonitorJvmLogMapper jvmLogMapper;
    private final SysMonitorRedisLogMapper redisLogMapper;
    private final SysMonitorDbLogMapper dbLogMapper;

    public SysMonitorPersistenceService(SysMonitorServerLogMapper serverLogMapper,
                                        SysMonitorJvmLogMapper jvmLogMapper,
                                        SysMonitorRedisLogMapper redisLogMapper,
                                        SysMonitorDbLogMapper dbLogMapper) {
        this.serverLogMapper = serverLogMapper;
        this.jvmLogMapper = jvmLogMapper;
        this.redisLogMapper = redisLogMapper;
        this.dbLogMapper = dbLogMapper;
    }

    /**
     * 异步存储监控快照
     */
    @Async("monitorAsyncExecutor")
    public void saveAsync(SysMonitorDataDTO data) {
        LocalDateTime now = LocalDateTime.now();

        // 服务器指标
        if (data.getServer() != null) {
            SysMonitorServerLog serverLog = new SysMonitorServerLog();
            serverLog.setId(SxwlSnowFlakeUtils.nextId());
            serverLog.setCpuLoad(data.getServer().getCpuLoad());
            serverLog.setMemUsed(data.getServer().getMemUsed());
            serverLog.setMemTotal(data.getServer().getMemTotal());
            serverLog.setDiskUsed(data.getServer().getDiskUsed());
            serverLog.setDiskTotal(data.getServer().getDiskTotal());
            serverLog.setCreateTime(now);
            try {
                serverLogMapper.insert(serverLog);
            } catch (Exception e) {
                log.warn("写入服务器监控日志失败: {}", e.getMessage());
            }
        }

        // JVM 指标
        if (data.getJvm() != null) {
            SysMonitorJvmLog jvmLog = new SysMonitorJvmLog();
            jvmLog.setId(SxwlSnowFlakeUtils.nextId());
            jvmLog.setHeapUsed(data.getJvm().getHeapUsed());
            jvmLog.setHeapMax(data.getJvm().getHeapMax());
            jvmLog.setHeapCommitted(data.getJvm().getHeapCommitted());
            jvmLog.setThreadCount(data.getJvm().getThreadCount());
            jvmLog.setPeakThreadCount(data.getJvm().getPeakThreadCount());
            jvmLog.setClassLoadedCount((int) data.getJvm().getClassLoadedCount());
            jvmLog.setCreateTime(now);
            try {
                jvmLogMapper.insert(jvmLog);
            } catch (Exception e) {
                log.warn("写入 JVM 监控日志失败: {}", e.getMessage());
            }
        }

        // Redis 指标
        if (data.getRedis() != null) {
            SysMonitorRedisLog redisLog = new SysMonitorRedisLog();
            redisLog.setId(SxwlSnowFlakeUtils.nextId());
            redisLog.setConnectedClients(data.getRedis().getConnectedClients().intValue());
            redisLog.setUsedMemory(data.getRedis().getUsedMemory());
            redisLog.setHitRate(data.getRedis().getHitRate());
            redisLog.setTotalKeys(data.getRedis().getTotalKeys().intValue());
            redisLog.setCreateTime(now);
            try {
                redisLogMapper.insert(redisLog);
            } catch (Exception e) {
                log.warn("写入 Redis 监控日志失败: {}", e.getMessage());
            }
        }

        // 数据库指标
        if (data.getDb() != null) {
            SysMonitorDbLog dbLog = new SysMonitorDbLog();
            dbLog.setId(SxwlSnowFlakeUtils.nextId());
            dbLog.setActiveConnections(data.getDb().getActiveConnections());
            dbLog.setCreateTime(now);
            try {
                dbLogMapper.insert(dbLog);
            } catch (Exception e) {
                log.warn("写入数据库监控日志失败: {}", e.getMessage());
            }
        }
    }
}

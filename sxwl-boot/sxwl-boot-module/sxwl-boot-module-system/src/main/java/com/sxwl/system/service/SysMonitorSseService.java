package com.sxwl.system.service;

import com.sxwl.monitor.model.JvmInfoVO;
import com.sxwl.monitor.model.ServerInfoVO;
import com.sxwl.monitor.service.JvmInfoService;
import com.sxwl.monitor.service.ServerInfoService;
import com.sxwl.sse.manager.SxwlSseEmitterManager;
import com.sxwl.system.model.dto.SysDbInfoDTO;
import com.sxwl.system.model.dto.SysMonitorDataDTO;
import com.sxwl.system.model.dto.SysRedisInfoDTO;
import com.sxwl.system.service.SysMonitorPersistenceService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 系统监控 SSE 推送服务
 *
 * <p>管理监控 SSE 连接，每 5 秒采集一次监控数据并推送给所有连接的客户端。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@Service
public class SysMonitorSseService {

    private static final Logger log = LoggerFactory.getLogger(SysMonitorSseService.class);

    /** 推送间隔：5 秒 */
    private static final long PUSH_INTERVAL_MS = 5000;

    /** 定时推送线程池 */
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "monitor-sse-push");
        t.setDaemon(true);
        return t;
    });

    private final SxwlSseEmitterManager sseEmitterManager;
    private final ServerInfoService serverInfoService;
    private final JvmInfoService jvmInfoService;
    private final StringRedisTemplate stringRedisTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final SysMonitorPersistenceService persistenceService;

    public SysMonitorSseService(SxwlSseEmitterManager sseEmitterManager,
                                ServerInfoService serverInfoService,
                                JvmInfoService jvmInfoService,
                                StringRedisTemplate stringRedisTemplate,
                                JdbcTemplate jdbcTemplate,
                                SysMonitorPersistenceService persistenceService) {
        this.sseEmitterManager = sseEmitterManager;
        this.serverInfoService = serverInfoService;
        this.persistenceService = persistenceService;
        this.jvmInfoService = jvmInfoService;
        this.stringRedisTemplate = stringRedisTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void init() {
        scheduler.scheduleAtFixedRate(this::broadcastMonitorData,
                PUSH_INTERVAL_MS, PUSH_INTERVAL_MS, TimeUnit.MILLISECONDS);
        log.info("监控 SSE 推送服务已启动，间隔={}ms", PUSH_INTERVAL_MS);
    }

    @PreDestroy
    public void destroy() {
        scheduler.shutdown();
        log.info("监控 SSE 推送服务已停止");
    }

    /**
     * 为用户建立监控 SSE 连接
     */
    public SseEmitter createEmitter(Long userId) {
        SseEmitter emitter = sseEmitterManager.connect(userId);
        // 建立后立即推送一次
        pushToUser(userId);
        return emitter;
    }

    /**
     * 采集监控数据
     */
    private SysMonitorDataDTO collectData() {
        SysMonitorDataDTO data = new SysMonitorDataDTO();
        data.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        try {
            data.setServer(serverInfoService.getServerInfo());
        } catch (Exception e) {
            log.warn("采集服务器信息失败: {}", e.getMessage());
        }

        try {
            data.setJvm(jvmInfoService.getJvmInfo());
        } catch (Exception e) {
            log.warn("采集 JVM 信息失败: {}", e.getMessage());
        }

        try {
            data.setRedis(collectRedisInfo());
        } catch (Exception e) {
            log.warn("采集 Redis 信息失败: {}", e.getMessage());
        }

        try {
            data.setDb(collectDbInfo());
        } catch (Exception e) {
            log.warn("采集数据库信息失败: {}", e.getMessage());
        }

        return data;
    }

    private SysRedisInfoDTO collectRedisInfo() {
        SysRedisInfoDTO dto = new SysRedisInfoDTO();
        try (RedisConnection connection = stringRedisTemplate.getRequiredConnectionFactory().getConnection()) {
            Properties info = connection.serverCommands().info();
            if (info != null) {
                dto.setConnectedClients(getLong(info, "connected_clients"));
                dto.setUsedMemory(getLong(info, "used_memory"));
                dto.setTotalKeys(getLong(info, "db0", "keys"));
                long hits = getLong(info, "keyspace_hits");
                long misses = getLong(info, "keyspace_misses");
                long total = hits + misses;
                dto.setHitRate(total > 0 ? (double) hits / total * 100 : 100.0);
            }
        } catch (Exception e) {
            log.warn("采集 Redis 信息失败: {}", e.getMessage());
        }
        return dto;
    }

    private SysDbInfoDTO collectDbInfo() {
        SysDbInfoDTO dto = new SysDbInfoDTO();
        Integer count = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM pg_stat_activity WHERE state = 'active'", Integer.class);
        dto.setActiveConnections(count != null ? count : 0);
        return dto;
    }

    /**
     * 推送给所有在线用户
     */
    private void broadcastMonitorData() {
        if (sseEmitterManager.getOnlineCount() == 0) {
            return;
        }

        SysMonitorDataDTO data = collectData();

        // 异步持久化
        persistenceService.saveAsync(data);

        sseEmitterManager.sendToAll("monitor-data", data);
    }

    /**
     * 推送给指定用户
     */
    private void pushToUser(Long userId) {
        SysMonitorDataDTO data = collectData();
        sseEmitterManager.sendToUser(userId, "monitor-data", data);
    }

    private Long getLong(Properties props, String key) {
        String val = props.getProperty(key);
        return val != null ? Long.parseLong(val) : 0L;
    }

    private Long getLong(Properties props, String section, String key) {
        String val = props.getProperty(key);
        return val != null ? Long.parseLong(val) : 0L;
    }
}

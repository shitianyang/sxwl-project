package com.sxwl.system.controller;

import com.sxwl.common.annotation.SxwlLog;
import com.sxwl.monitor.model.ServerInfoVO;
import com.sxwl.monitor.model.JvmInfoVO;
import com.sxwl.monitor.service.ServerInfoService;
import com.sxwl.monitor.service.JvmInfoService;
import com.sxwl.system.model.dto.SysDbInfoDTO;
import com.sxwl.system.model.dto.SysRedisInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;

/**
 * 系统监控 Controller
 *
 * <p>提供服务器、JVM、Redis、数据库的监控数据。数据采集层在 config-monitor 模块中。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@RestController
@RequestMapping("/sys/monitor")
public class SysMonitorController {

    private static final Logger log = LoggerFactory.getLogger(SysMonitorController.class);

    private final ServerInfoService serverInfoService;
    private final JvmInfoService jvmInfoService;
    private final StringRedisTemplate stringRedisTemplate;
    private final JdbcTemplate jdbcTemplate;

    public SysMonitorController(ServerInfoService serverInfoService,
                                JvmInfoService jvmInfoService,
                                StringRedisTemplate stringRedisTemplate,
                                JdbcTemplate jdbcTemplate) {
        this.serverInfoService = serverInfoService;
        this.jvmInfoService = jvmInfoService;
        this.stringRedisTemplate = stringRedisTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 获取服务器信息
     */
    @GetMapping("/server")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('monitor:server:list')")
    @SxwlLog(title = "系统监控", description = "查询服务器信息")
    public ServerInfoVO server() {
        return serverInfoService.getServerInfo();
    }

    /**
     * 获取 JVM 信息
     */
    @GetMapping("/jvm")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('monitor:server:list')")
    public JvmInfoVO jvm() {
        return jvmInfoService.getJvmInfo();
    }

    /**
     * 获取 Redis 信息
     */
    @GetMapping("/redis")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('monitor:server:list')")
    public SysRedisInfoDTO redis() {
        SysRedisInfoDTO dto = new SysRedisInfoDTO();
        try (RedisConnection connection = stringRedisTemplate.getRequiredConnectionFactory().getConnection()) {
            Properties info = connection.serverCommands().info();
            if (info != null) {
                dto.setConnectedClients(getLong(info, "connected_clients"));
                dto.setUsedMemory(getLong(info, "used_memory"));
                dto.setTotalKeys(getLong(info, "db0", "keys"));
                // 命中率 = keyspace_hits / (keyspace_hits + keyspace_misses) * 100
                long hits = getLong(info, "keyspace_hits");
                long misses = getLong(info, "keyspace_misses");
                long total = hits + misses;
                if (total > 0) {
                    dto.setHitRate((double) hits / total * 100);
                } else {
                    dto.setHitRate(100.0);
                }
            }
        } catch (Exception e) {
            log.warn("获取 Redis 信息失败: {}", e.getMessage());
        }
        return dto;
    }

    /**
     * 获取数据库连接信息
     */
    @GetMapping("/db")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority('monitor:server:list')")
    public SysDbInfoDTO db() {
        SysDbInfoDTO dto = new SysDbInfoDTO();
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT count(*) FROM pg_stat_activity WHERE state = 'active'", Integer.class);
            dto.setActiveConnections(count != null ? count : 0);
        } catch (Exception e) {
            log.warn("获取数据库连接信息失败: {}", e.getMessage());
        }
        return dto;
    }

    // ==================== 私有方法 ====================

    private Long getLong(Properties props, String key) {
        String val = props.getProperty(key);
        return val != null ? Long.parseLong(val) : 0L;
    }

    private Long getLong(Properties props, String section, String key) {
        // Redis INFO 按 section 组织，需要解析 section 内的属性
        String val = props.getProperty(key);
        return val != null ? Long.parseLong(val) : 0L;
    }
}

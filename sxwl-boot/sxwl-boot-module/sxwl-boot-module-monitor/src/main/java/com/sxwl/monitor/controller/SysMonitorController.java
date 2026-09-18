package com.sxwl.monitor.controller;

import com.sxwl.common.constant.SxwlPermConstant;
import com.sxwl.common.annotation.SxwlLog;
import com.sxwl.monitor.model.VO.ServerInfoVO;
import com.sxwl.monitor.model.VO.JvmInfoVO;
import com.sxwl.monitor.model.dto.SysDbInfoDTO;
import com.sxwl.monitor.model.dto.SysRedisInfoDTO;
import com.sxwl.monitor.service.ServerInfoService;
import com.sxwl.monitor.service.JvmInfoService;
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
 * <p>提供服务器、JVM、Redis、数据库的监控数据。
 * 数据采集层在 config-monitor 模块中，业务逻辑在本模块中。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@RestController
@RequestMapping("/sys/monitor")
public class SysMonitorController {

    private static final Logger log = LoggerFactory.getLogger(SysMonitorController.class);

    // ✅ 数据库监控查询常量（参数化查询，防止 SQL 注入）
    private static final String QUERY_ACTIVE_CONNECTIONS = 
            "SELECT COUNT(*) FROM pg_stat_activity WHERE state = ?";

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
     *
     * @return 服务器硬件信息（CPU、内存、磁盘）
     */
    @GetMapping("/server")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority(" + SxwlPermConstant.Monitor.SERVER_VIEW + ")")
    @SxwlLog(title = "系统监控", description = "查询服务器信息")
    public ServerInfoVO server() {
        return serverInfoService.getServerInfo();
    }

    /**
     * 获取 JVM 信息
     *
     * @return JVM 运行时信息（堆内存、非堆内存、线程数、类加载数、GC 统计）
     */
    @GetMapping("/jvm")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority(" + SxwlPermConstant.Monitor.SERVER_VIEW + ")")
    @SxwlLog(title = "系统监控", description = "查询 JVM 信息")
    public JvmInfoVO jvm() {
        return jvmInfoService.getJvmInfo();
    }

    /**
     * 获取 Redis 信息
     *
     * @return Redis 运行时信息（连接客户端数、内存使用、总 Key 数、命中率）
     */
    @GetMapping("/redis")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority(" + SxwlPermConstant.Monitor.SERVER_VIEW + ")")
    @SxwlLog(title = "系统监控", description = "查询 Redis 信息")
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
     *
     * @return 数据库连接池信息（活跃连接数）
     */
    @GetMapping("/db")
    @PreAuthorize("hasAuthority('*:*:*') or hasAuthority(" + SxwlPermConstant.Monitor.SERVER_VIEW + ")")
    @SxwlLog(title = "系统监控", description = "查询数据库连接信息")
    public SysDbInfoDTO db() {
        SysDbInfoDTO dto = new SysDbInfoDTO();
        try {
            // ✅ 使用参数化查询，防止 SQL 注入
            Integer count = jdbcTemplate.queryForObject(QUERY_ACTIVE_CONNECTIONS, Integer.class, "active");
            dto.setActiveConnections(count != null ? count : 0);
        } catch (Exception e) {
            log.warn("获取数据库连接信息失败: {}", e.getMessage());
        }
        return dto;
    }

    // ==================== 私有方法 ====================

    /**
     * 从 Properties 中读取 Long 值
     *
     * @param props 属性集合
     * @param key   键名
     * @return Long 值，读取失败返回 0L
     */
    private Long getLong(Properties props, String key) {
        String val = props.getProperty(key);
        return val != null ? Long.parseLong(val) : 0L;
    }

    /**
     * 从 Properties 中读取 Long 值（支持 section 格式）
     *
     * @param props   属性集合
     * @param section section 名称
     * @param key     键名
     * @return Long 值，读取失败返回 0L
     */
    private Long getLong(Properties props, String section, String key) {
        try {
            // ✅ 先尝试带 section 前缀的键名（如 db0:keys）
            String prefixedKey = section + ":" + key;
            String val = props.getProperty(prefixedKey);
            
            if (val == null) {
                // 降级为全局查找（处理 keyspace_hits 等全局配置）
                val = props.getProperty(key);
            }
            
            return val != null ? Long.parseLong(val) : 0L;
        } catch (NumberFormatException e) {
            log.warn("解析 Redis 属性失败: {}={}, error={}", section + ":" + key, 
                     props.getProperty(section + ":" + key), e.getMessage());
            return 0L;
        }
    }
}

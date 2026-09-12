/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.service.impl;

import com.sxwl.monitor.model.JvmInfoVO;
import com.sxwl.monitor.model.ServerInfoVO;
import com.sxwl.monitor.service.JvmInfoService;
import com.sxwl.monitor.service.ServerInfoService;
import com.sxwl.sse.manager.SxwlSseEmitterManager;
import com.sxwl.system.model.dto.SysDbInfoDTO;
import com.sxwl.system.model.dto.SysMonitorDataDTO;
import com.sxwl.system.model.dto.SysRedisInfoDTO;
import com.sxwl.system.service.SysMonitorPersistenceService;
import com.sxwl.system.service.SysMonitorSseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SysMonitorSseService 测试")
class SysMonitorSseServiceTest {
    private SysMonitorSseService service;

    @Mock private SxwlSseEmitterManager sseEmitterManager;
    @Mock private ServerInfoService serverInfoService;
    @Mock private JvmInfoService jvmInfoService;
    @Mock private StringRedisTemplate stringRedisTemplate;
    @Mock private JdbcTemplate jdbcTemplate;
    @Mock private SysMonitorPersistenceService persistenceService;
    @Mock private RedisConnectionFactory redisConnectionFactory;
    @Mock private RedisConnection redisConnection;
    @Mock private RedisServerCommands redisServerCommands;
    @Captor private ArgumentCaptor<SysMonitorDataDTO> dataCaptor;

    @BeforeEach
    void setUp() {
        service = new SysMonitorSseService(sseEmitterManager, serverInfoService, jvmInfoService,
                stringRedisTemplate, jdbcTemplate, persistenceService);
    }

    @Test
    void testCreateEmitter() {
        // 准备打桩
        ServerInfoVO serverInfo = new ServerInfoVO();
        JvmInfoVO jvmInfo = new JvmInfoVO();
        when(serverInfoService.getServerInfo()).thenReturn(serverInfo);
        when(jvmInfoService.getJvmInfo()).thenReturn(jvmInfo);
        when(stringRedisTemplate.getRequiredConnectionFactory()).thenReturn(redisConnectionFactory);
        when(redisConnectionFactory.getConnection()).thenReturn(redisConnection);
        when(redisConnection.serverCommands()).thenReturn(redisServerCommands);
        Properties redisProps = new Properties();
        redisProps.setProperty("connected_clients", "10");
        redisProps.setProperty("used_memory", "1048576");
        redisProps.setProperty("keyspace_hits", "100");
        redisProps.setProperty("keyspace_misses", "10");
        redisProps.setProperty("keys", "500");
        when(redisServerCommands.info()).thenReturn(redisProps);
        when(jdbcTemplate.queryForObject("SELECT count(*) FROM pg_stat_activity WHERE state = 'active'", Integer.class))
                .thenReturn(5);

        SseEmitter emitter = new SseEmitter();
        when(sseEmitterManager.connect(1L)).thenReturn(emitter);

        // 执行
        SseEmitter result = service.createEmitter(1L);

        // 验证
        assertNotNull(result);
        verify(sseEmitterManager).connect(1L);
        verify(sseEmitterManager).sendToUser(eq(1L), eq("monitor-data"), dataCaptor.capture());

        SysMonitorDataDTO data = dataCaptor.getValue();
        assertNotNull(data.getTimestamp());
        assertNotNull(data.getServer());
        assertNotNull(data.getJvm());
        assertNotNull(data.getRedis());
        assertEquals(10, data.getRedis().getConnectedClients().intValue());
        assertEquals(1048576, data.getRedis().getUsedMemory().longValue());
        assertEquals(500, data.getRedis().getTotalKeys().longValue());
        assertEquals(100.0 * 100 / 110, data.getRedis().getHitRate(), 0.001);
        assertNotNull(data.getDb());
        assertEquals(5, data.getDb().getActiveConnections());
    }

    @Test
    void testCreateEmitter_whenServerInfoFails() {
        when(serverInfoService.getServerInfo()).thenThrow(new RuntimeException("timeout"));
        JvmInfoVO jvmInfo = new JvmInfoVO();
        when(jvmInfoService.getJvmInfo()).thenReturn(jvmInfo);
        when(stringRedisTemplate.getRequiredConnectionFactory()).thenReturn(redisConnectionFactory);
        when(redisConnectionFactory.getConnection()).thenReturn(redisConnection);
        when(redisConnection.serverCommands()).thenReturn(redisServerCommands);
        Properties redisProps = new Properties();
        redisProps.setProperty("connected_clients", "5");
        redisProps.setProperty("used_memory", "512000");
        redisProps.setProperty("keyspace_hits", "50");
        redisProps.setProperty("keyspace_misses", "0");
        redisProps.setProperty("keys", "100");
        when(redisServerCommands.info()).thenReturn(redisProps);
        when(jdbcTemplate.queryForObject("SELECT count(*) FROM pg_stat_activity WHERE state = 'active'", Integer.class))
                .thenReturn(3);

        SseEmitter emitter = new SseEmitter();
        when(sseEmitterManager.connect(1L)).thenReturn(emitter);

        SseEmitter result = service.createEmitter(1L);

        assertNotNull(result);
        verify(sseEmitterManager).sendToUser(eq(1L), eq("monitor-data"), dataCaptor.capture());
        SysMonitorDataDTO data = dataCaptor.getValue();
        // server 采集失败时为 null
        assertEquals(5, data.getRedis().getConnectedClients().intValue());
        assertEquals(100.0, data.getRedis().getHitRate(), 0.001);
        assertEquals(3, data.getDb().getActiveConnections());
    }

    @Test
    void testCreateEmitter_whenRedisFails() {
        ServerInfoVO serverInfo = new ServerInfoVO();
        JvmInfoVO jvmInfo = new JvmInfoVO();
        when(serverInfoService.getServerInfo()).thenReturn(serverInfo);
        when(jvmInfoService.getJvmInfo()).thenReturn(jvmInfo);
        when(stringRedisTemplate.getRequiredConnectionFactory()).thenReturn(redisConnectionFactory);
        when(redisConnectionFactory.getConnection()).thenThrow(new RuntimeException("Redis connection refused"));
        when(jdbcTemplate.queryForObject("SELECT count(*) FROM pg_stat_activity WHERE state = 'active'", Integer.class))
                .thenReturn(2);

        SseEmitter emitter = new SseEmitter();
        when(sseEmitterManager.connect(1L)).thenReturn(emitter);

        SseEmitter result = service.createEmitter(1L);

        assertNotNull(result);
        verify(sseEmitterManager).sendToUser(eq(1L), eq("monitor-data"), dataCaptor.capture());
        SysMonitorDataDTO data = dataCaptor.getValue();
        assertNotNull(data.getServer());
        assertNotNull(data.getJvm());
        // Redis 采集失败时 DTO 属性为 null
        assertEquals(2, data.getDb().getActiveConnections());
    }

    @Test
    void testCreateEmitter_whenDbFails() {
        ServerInfoVO serverInfo = new ServerInfoVO();
        JvmInfoVO jvmInfo = new JvmInfoVO();
        when(serverInfoService.getServerInfo()).thenReturn(serverInfo);
        when(jvmInfoService.getJvmInfo()).thenReturn(jvmInfo);
        when(stringRedisTemplate.getRequiredConnectionFactory()).thenReturn(redisConnectionFactory);
        when(redisConnectionFactory.getConnection()).thenReturn(redisConnection);
        when(redisConnection.serverCommands()).thenReturn(redisServerCommands);
        Properties redisProps = new Properties();
        redisProps.setProperty("connected_clients", "3");
        redisProps.setProperty("used_memory", "256000");
        redisProps.setProperty("keyspace_hits", "30");
        redisProps.setProperty("keyspace_misses", "10");
        redisProps.setProperty("keys", "50");
        when(redisServerCommands.info()).thenReturn(redisProps);
        when(jdbcTemplate.queryForObject("SELECT count(*) FROM pg_stat_activity WHERE state = 'active'", Integer.class))
                .thenThrow(new RuntimeException("DB connection failed"));

        SseEmitter emitter = new SseEmitter();
        when(sseEmitterManager.connect(1L)).thenReturn(emitter);

        SseEmitter result = service.createEmitter(1L);

        assertNotNull(result);
        verify(sseEmitterManager).sendToUser(eq(1L), eq("monitor-data"), dataCaptor.capture());
        SysMonitorDataDTO data = dataCaptor.getValue();
        assertNotNull(data.getRedis());
        assertEquals(3, data.getRedis().getConnectedClients().intValue());
        // DB 失败时 data.setDb() 不会被调用，getDb() 返回 null
        assertNull(data.getDb());
    }

    @Test
    void testDestroy() {
        // destroy 方法将 scheduler shutdown，多次调用不应抛异常
        service.destroy();
        service.destroy();
    }
}

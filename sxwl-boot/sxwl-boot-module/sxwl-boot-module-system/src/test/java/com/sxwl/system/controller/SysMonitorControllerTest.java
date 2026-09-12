/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.controller;

import com.sxwl.system.model.dto.SysDbInfoDTO;
import com.sxwl.system.model.dto.SysRedisInfoDTO;
import com.sxwl.monitor.model.ServerInfoVO;
import com.sxwl.monitor.model.JvmInfoVO;
import com.sxwl.monitor.service.ServerInfoService;
import com.sxwl.monitor.service.JvmInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SysMonitorController 测试")
class SysMonitorControllerTest {
    private SysMonitorController controller;
    @Mock private ServerInfoService serverInfoService;
    @Mock private JvmInfoService jvmInfoService;
    @Mock private StringRedisTemplate stringRedisTemplate;
    @Mock private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() { controller = new SysMonitorController(serverInfoService, jvmInfoService, stringRedisTemplate, jdbcTemplate); }

    @Test void testServer() {
        ServerInfoVO vo = new ServerInfoVO();
        when(serverInfoService.getServerInfo()).thenReturn(vo);
        assertSame(vo, controller.server());
    }
    @Test void testJvm() {
        JvmInfoVO vo = new JvmInfoVO();
        when(jvmInfoService.getJvmInfo()).thenReturn(vo);
        assertSame(vo, controller.jvm());
    }
    @Test void testRedis_whenInfoFails_returnsEmptyDto() {
        when(stringRedisTemplate.getRequiredConnectionFactory()).thenThrow(new RuntimeException("no connection"));
        SysRedisInfoDTO dto = controller.redis();
        assertNotNull(dto);
        assertNull(dto.getConnectedClients());
    }
    @Test void testDb_whenQueryFails_returnsZero() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class))).thenThrow(new RuntimeException("db error"));
        SysDbInfoDTO dto = controller.db();
        assertNotNull(dto);
        assertEquals(0, dto.getActiveConnections());
    }
}

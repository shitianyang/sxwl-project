/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.service.impl;

import com.sxwl.common.utils.SxwlSnowFlakeUtils;
import com.sxwl.system.mapper.SysMonitorDbLogMapper;
import com.sxwl.system.mapper.SysMonitorJvmLogMapper;
import com.sxwl.system.mapper.SysMonitorRedisLogMapper;
import com.sxwl.system.mapper.SysMonitorServerLogMapper;
import com.sxwl.system.model.dto.SysDbInfoDTO;
import com.sxwl.system.model.dto.SysMonitorDataDTO;
import com.sxwl.system.model.dto.SysRedisInfoDTO;
import com.sxwl.monitor.model.ServerInfoVO;
import com.sxwl.monitor.model.JvmInfoVO;
import com.sxwl.system.service.SysMonitorPersistenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SysMonitorPersistenceService 测试")
class SysMonitorPersistenceServiceTest {
    private SysMonitorPersistenceService service;
    @Mock private SysMonitorServerLogMapper serverLogMapper;
    @Mock private SysMonitorJvmLogMapper jvmLogMapper;
    @Mock private SysMonitorRedisLogMapper redisLogMapper;
    @Mock private SysMonitorDbLogMapper dbLogMapper;

    @BeforeEach
    void setUp() { service = new SysMonitorPersistenceService(serverLogMapper, jvmLogMapper, redisLogMapper, dbLogMapper); }

    @Test
    void testSaveAsync_withAllData() {
        try (MockedStatic<SxwlSnowFlakeUtils> sf = mockStatic(SxwlSnowFlakeUtils.class)) {
            sf.when(SxwlSnowFlakeUtils::nextId).thenReturn(1L);

            SysMonitorDataDTO data = new SysMonitorDataDTO();
            ServerInfoVO server = new ServerInfoVO(); server.setCpuLoad(45.5); server.setMemUsed(8192L); server.setMemTotal(16384L); server.setDiskUsed(102400L); server.setDiskTotal(512000L);
            JvmInfoVO jvm = new JvmInfoVO(); jvm.setHeapUsed(512L); jvm.setHeapMax(1024L); jvm.setHeapCommitted(768L); jvm.setThreadCount(10); jvm.setPeakThreadCount(20); jvm.setClassLoadedCount(5000);
            SysRedisInfoDTO redis = new SysRedisInfoDTO(); redis.setConnectedClients(5L); redis.setUsedMemory(256L); redis.setHitRate(99.5); redis.setTotalKeys(1000L);
            SysDbInfoDTO db = new SysDbInfoDTO(); db.setActiveConnections(10);
            data.setServer(server); data.setJvm(jvm); data.setRedis(redis); data.setDb(db);

            service.saveAsync(data);

            verify(serverLogMapper).insert(any());
            verify(jvmLogMapper).insert(any());
            verify(redisLogMapper).insert(any());
            verify(dbLogMapper).insert(any());
        }
    }

    @Test
    void testSaveAsync_withNullData_shouldNotInsert() {
        SysMonitorDataDTO data = new SysMonitorDataDTO();
        service.saveAsync(data);

        verifyNoInteractions(serverLogMapper, jvmLogMapper, redisLogMapper, dbLogMapper);
    }
}

/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SysMonitorRedisLog 实体测试")
class SysMonitorRedisLogTest {
    @Test
    void testGettersAndSetters() {
        SysMonitorRedisLog entity = new SysMonitorRedisLog();
        LocalDateTime now = LocalDateTime.now();
        entity.setId(1L); entity.setConnectedClients(5); entity.setUsedMemory(256L);
        entity.setHitRate(99.5); entity.setTotalKeys(1000); entity.setCreateTime(now);
        assertEquals(1L, entity.getId()); assertEquals(5, entity.getConnectedClients());
        assertEquals(256L, entity.getUsedMemory()); assertEquals(99.5, entity.getHitRate());
        assertEquals(1000, entity.getTotalKeys()); assertEquals(now, entity.getCreateTime());
    }
    @Test void testNoArgsConstructor() { SysMonitorRedisLog e = new SysMonitorRedisLog(); assertNull(e.getId()); assertNull(e.getConnectedClients()); }
}

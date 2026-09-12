/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SysMonitorServerLog 实体测试")
class SysMonitorServerLogTest {
    @Test
    void testGettersAndSetters() {
        SysMonitorServerLog entity = new SysMonitorServerLog();
        LocalDateTime now = LocalDateTime.now();
        entity.setId(1L); entity.setCpuLoad(45.5); entity.setMemUsed(8192L); entity.setMemTotal(16384L);
        entity.setDiskUsed(102400L); entity.setDiskTotal(512000L); entity.setCreateTime(now);
        assertEquals(1L, entity.getId()); assertEquals(45.5, entity.getCpuLoad());
        assertEquals(8192L, entity.getMemUsed()); assertEquals(16384L, entity.getMemTotal());
        assertEquals(102400L, entity.getDiskUsed()); assertEquals(512000L, entity.getDiskTotal());
        assertEquals(now, entity.getCreateTime());
    }
    @Test void testNoArgsConstructor() { SysMonitorServerLog e = new SysMonitorServerLog(); assertNull(e.getId()); assertNull(e.getCpuLoad()); }
}

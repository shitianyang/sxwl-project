/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SysMonitorJvmLog 实体测试")
class SysMonitorJvmLogTest {
    @Test
    void testGettersAndSetters() {
        SysMonitorJvmLog entity = new SysMonitorJvmLog();
        LocalDateTime now = LocalDateTime.now();
        entity.setId(1L); entity.setHeapUsed(512L); entity.setHeapMax(1024L); entity.setHeapCommitted(768L);
        entity.setThreadCount(10); entity.setPeakThreadCount(20); entity.setClassLoadedCount(5000);
        entity.setCreateTime(now);
        assertEquals(1L, entity.getId()); assertEquals(512L, entity.getHeapUsed());
        assertEquals(1024L, entity.getHeapMax()); assertEquals(768L, entity.getHeapCommitted());
        assertEquals(10, entity.getThreadCount()); assertEquals(20, entity.getPeakThreadCount());
        assertEquals(5000, entity.getClassLoadedCount()); assertEquals(now, entity.getCreateTime());
    }
    @Test void testNoArgsConstructor() { SysMonitorJvmLog e = new SysMonitorJvmLog(); assertNull(e.getId()); assertNull(e.getHeapUsed()); }
}

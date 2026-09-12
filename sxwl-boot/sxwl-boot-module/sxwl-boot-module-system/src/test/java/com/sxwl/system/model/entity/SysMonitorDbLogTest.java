/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SysMonitorDbLog 实体测试")
class SysMonitorDbLogTest {
    @Test
    void testGettersAndSetters() {
        SysMonitorDbLog entity = new SysMonitorDbLog();
        LocalDateTime now = LocalDateTime.now();
        entity.setId(1L); entity.setActiveConnections(10); entity.setCreateTime(now);
        assertEquals(1L, entity.getId()); assertEquals(10, entity.getActiveConnections());
        assertEquals(now, entity.getCreateTime());
    }
    @Test void testNoArgsConstructor() { SysMonitorDbLog e = new SysMonitorDbLog(); assertNull(e.getId()); assertNull(e.getActiveConnections()); }
}

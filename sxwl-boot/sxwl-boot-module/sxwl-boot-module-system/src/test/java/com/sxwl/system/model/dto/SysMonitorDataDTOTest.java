/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SysMonitorDataDTO 测试")
class SysMonitorDataDTOTest {
    @Test
    void testGettersAndSetters() {
        SysMonitorDataDTO dto = new SysMonitorDataDTO();
        dto.setTimestamp("2026-01-01T00:00:00");
        dto.setRedis(new SysRedisInfoDTO()); dto.setDb(new SysDbInfoDTO());
        assertEquals("2026-01-01T00:00:00", dto.getTimestamp());
        assertNotNull(dto.getRedis()); assertNotNull(dto.getDb());
        assertNull(dto.getServer()); assertNull(dto.getJvm());
    }
}

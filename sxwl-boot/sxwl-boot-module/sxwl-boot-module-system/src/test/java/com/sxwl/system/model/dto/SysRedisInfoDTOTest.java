/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SysRedisInfoDTO 测试")
class SysRedisInfoDTOTest {
    @Test
    void testGettersAndSetters() {
        SysRedisInfoDTO dto = new SysRedisInfoDTO();
        dto.setConnectedClients(5L); dto.setUsedMemory(256L); dto.setHitRate(99.5); dto.setTotalKeys(1000L);
        assertEquals(5L, dto.getConnectedClients()); assertEquals(256L, dto.getUsedMemory());
        assertEquals(99.5, dto.getHitRate()); assertEquals(1000L, dto.getTotalKeys());
    }
}

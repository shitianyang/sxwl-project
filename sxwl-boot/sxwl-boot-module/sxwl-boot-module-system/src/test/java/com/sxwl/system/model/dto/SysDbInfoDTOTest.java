/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SysDbInfoDTO 测试")
class SysDbInfoDTOTest {
    @Test
    void testGettersAndSetters() {
        SysDbInfoDTO dto = new SysDbInfoDTO();
        dto.setActiveConnections(10);
        assertEquals(10, dto.getActiveConnections());
    }
}

/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SysCacheKeyDetailDTO 测试")
class SysCacheKeyDetailDTOTest {
    @Test
    void testGettersAndSetters() {
        SysCacheKeyDetailDTO dto = new SysCacheKeyDetailDTO();
        dto.setKey("dict:sys_status"); dto.setType("string"); dto.setValue("value"); dto.setTtl(3600L);
        assertEquals("dict:sys_status", dto.getKey()); assertEquals("string", dto.getType());
        assertEquals("value", dto.getValue()); assertEquals(3600L, dto.getTtl());
    }
}

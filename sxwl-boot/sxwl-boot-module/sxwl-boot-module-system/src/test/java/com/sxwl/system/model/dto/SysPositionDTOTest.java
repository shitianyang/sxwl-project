/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SysPositionDTO 测试")
class SysPositionDTOTest {
    @Test
    void testGettersAndSetters() {
        SysPositionDTO dto = new SysPositionDTO();
        dto.setId(1L); dto.setPositionCode("P001"); dto.setPositionName("Java开发");
        dto.setSort(1); dto.setStatus(1); dto.setDescription("描述"); dto.setCreateTime("2026-01-01");
        assertEquals(1L, dto.getId()); assertEquals("P001", dto.getPositionCode());
        assertEquals("Java开发", dto.getPositionName()); assertEquals(1, dto.getSort());
        assertEquals(1, dto.getStatus()); assertEquals("描述", dto.getDescription());
        assertEquals("2026-01-01", dto.getCreateTime());
    }
}

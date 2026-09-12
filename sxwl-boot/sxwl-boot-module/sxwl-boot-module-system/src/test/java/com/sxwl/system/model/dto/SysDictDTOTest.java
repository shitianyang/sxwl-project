/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SysDictDTO 测试")
class SysDictDTOTest {
    @Test
    void testGettersAndSetters() {
        SysDictDTO dto = new SysDictDTO();
        dto.setId(1L); dto.setDictCode("sys_status"); dto.setDictName("系统状态");
        dto.setDescription("描述"); dto.setStatus(1); dto.setCreateTime("2026-01-01");
        assertEquals(1L, dto.getId()); assertEquals("sys_status", dto.getDictCode());
        assertEquals("系统状态", dto.getDictName()); assertEquals("描述", dto.getDescription());
        assertEquals(1, dto.getStatus()); assertEquals("2026-01-01", dto.getCreateTime());
    }
}

/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SysDictDetailDTO 测试")
class SysDictDetailDTOTest {
    @Test
    void testGettersAndSetters() {
        SysDictDetailDTO dto = new SysDictDetailDTO();
        dto.setId(1L); dto.setDictId(10L); dto.setDetailValue("1"); dto.setDetailLabel("启用");
        dto.setDescription("描述"); dto.setSort(1); dto.setStatus(1); dto.setIsDefault(0);
        assertEquals(1L, dto.getId()); assertEquals(10L, dto.getDictId());
        assertEquals("1", dto.getDetailValue()); assertEquals("启用", dto.getDetailLabel());
        assertEquals("描述", dto.getDescription()); assertEquals(1, dto.getSort());
        assertEquals(1, dto.getStatus()); assertEquals(0, dto.getIsDefault());
    }
}

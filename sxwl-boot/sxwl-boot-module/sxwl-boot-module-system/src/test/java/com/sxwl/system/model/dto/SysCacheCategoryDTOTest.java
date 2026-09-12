/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SysCacheCategoryDTO 测试")
class SysCacheCategoryDTOTest {
    @Test
    void testGettersAndSetters() {
        SysCacheCategoryDTO dto = new SysCacheCategoryDTO();
        dto.setName("字典缓存"); dto.setKeyPrefix("dict:*");
        assertEquals("字典缓存", dto.getName()); assertEquals("dict:*", dto.getKeyPrefix());
    }
    @Test
    void testConstructor() {
        SysCacheCategoryDTO dto = new SysCacheCategoryDTO("字典缓存", "dict:*");
        assertEquals("字典缓存", dto.getName()); assertEquals("dict:*", dto.getKeyPrefix());
    }
}

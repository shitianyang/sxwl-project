/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SysPosition 实体测试")
class SysPositionTest {
    @Test
    void testGettersAndSetters() {
        SysPosition entity = new SysPosition();
        LocalDateTime now = LocalDateTime.now();
        entity.setId(1L); entity.setPositionCode("P001"); entity.setPositionName("Java开发"); entity.setSort(1);
        entity.setStatus(1); entity.setDescription("描述");
        entity.setCreateBy(100L); entity.setCreateOrg(200L); entity.setCreateTime(now);
        entity.setUpdateBy(101L); entity.setUpdateTime(now); entity.setDeleteFlag(0);
        assertEquals(1L, entity.getId()); assertEquals("P001", entity.getPositionCode());
        assertEquals("Java开发", entity.getPositionName()); assertEquals(1, entity.getSort());
        assertEquals(1, entity.getStatus()); assertEquals("描述", entity.getDescription());
    }
    @Test void testNoArgsConstructor() { SysPosition e = new SysPosition(); assertNull(e.getId()); assertNull(e.getPositionCode()); }
}

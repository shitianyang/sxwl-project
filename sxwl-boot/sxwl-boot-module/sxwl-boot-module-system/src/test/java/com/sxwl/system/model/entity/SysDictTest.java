/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SysDict 实体测试")
class SysDictTest {
    @Test
    void testGettersAndSetters() {
        SysDict entity = new SysDict();
        LocalDateTime now = LocalDateTime.now();
        entity.setId(1L); entity.setDictCode("sys_status"); entity.setDictName("系统状态");
        entity.setDescription("描述"); entity.setStatus(1);
        entity.setCreateBy(100L); entity.setCreateOrg(200L); entity.setCreateTime(now);
        entity.setUpdateBy(101L); entity.setUpdateTime(now); entity.setDeleteFlag(0);
        assertEquals(1L, entity.getId()); assertEquals("sys_status", entity.getDictCode());
        assertEquals("系统状态", entity.getDictName()); assertEquals("描述", entity.getDescription());
        assertEquals(1, entity.getStatus());
    }
    @Test void testNoArgsConstructor() { SysDict e = new SysDict(); assertNull(e.getId()); assertNull(e.getDictCode()); }
}

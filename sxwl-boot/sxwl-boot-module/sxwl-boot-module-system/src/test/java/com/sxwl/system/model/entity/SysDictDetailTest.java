/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SysDictDetail 实体测试")
class SysDictDetailTest {
    @Test
    void testGettersAndSetters() {
        SysDictDetail entity = new SysDictDetail();
        LocalDateTime now = LocalDateTime.now();
        entity.setId(1L); entity.setDictId(10L); entity.setDetailValue("1"); entity.setDetailLabel("启用");
        entity.setDescription("描述"); entity.setSort(1); entity.setStatus(1); entity.setIsDefault(0);
        entity.setCreateBy(100L); entity.setCreateOrg(200L); entity.setCreateTime(now);
        entity.setUpdateBy(101L); entity.setUpdateTime(now); entity.setDeleteFlag(0);
        assertEquals(1L, entity.getId()); assertEquals(10L, entity.getDictId());
        assertEquals("1", entity.getDetailValue()); assertEquals("启用", entity.getDetailLabel());
        assertEquals("描述", entity.getDescription()); assertEquals(1, entity.getSort());
        assertEquals(1, entity.getStatus()); assertEquals(0, entity.getIsDefault());
        assertEquals(100L, entity.getCreateBy()); assertEquals(200L, entity.getCreateOrg());
        assertEquals(now, entity.getCreateTime()); assertEquals(101L, entity.getUpdateBy());
        assertEquals(now, entity.getUpdateTime()); assertEquals(0, entity.getDeleteFlag());
    }
    @Test void testNoArgsConstructor() { SysDictDetail e = new SysDictDetail(); assertNull(e.getId()); assertNull(e.getDictId()); }
}

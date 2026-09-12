/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SysRole 实体测试")
class SysRoleTest {
    @Test
    void testGettersAndSetters() {
        SysRole entity = new SysRole();
        LocalDateTime now = LocalDateTime.now();
        entity.setId(1L); entity.setRoleCode("admin"); entity.setRoleName("管理员"); entity.setDataScope(1);
        entity.setSort(1); entity.setStatus(1); entity.setDescription("描述");
        entity.setCreateBy(100L); entity.setCreateOrg(200L); entity.setCreateTime(now);
        entity.setUpdateBy(101L); entity.setUpdateTime(now); entity.setDeleteFlag(0);
        assertEquals(1L, entity.getId()); assertEquals("admin", entity.getRoleCode());
        assertEquals("管理员", entity.getRoleName()); assertEquals(1, entity.getDataScope());
        assertEquals(1, entity.getSort()); assertEquals(1, entity.getStatus()); assertEquals("描述", entity.getDescription());
        assertEquals(100L, entity.getCreateBy()); assertEquals(200L, entity.getCreateOrg());
        assertEquals(now, entity.getCreateTime()); assertEquals(now, entity.getUpdateTime()); assertEquals(0, entity.getDeleteFlag());
    }
    @Test void testNoArgsConstructor() { SysRole e = new SysRole(); assertNull(e.getId()); assertNull(e.getRoleCode()); }
}

/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SysUser 实体测试")
class SysUserTest {
    @Test
    void testGettersAndSetters() {
        SysUser entity = new SysUser();
        LocalDateTime now = LocalDateTime.now();
        entity.setId(1L); entity.setUsername("admin"); entity.setPassword("pwd"); entity.setRealName("管理员");
        entity.setPhone("13800138000"); entity.setEmail("admin@test.com"); entity.setStatus(1);
        entity.setCreateBy(100L); entity.setCreateOrg(200L); entity.setCreateTime(now);
        entity.setUpdateBy(101L); entity.setUpdateTime(now); entity.setDeleteFlag(0);
        assertEquals(1L, entity.getId()); assertEquals("admin", entity.getUsername());
        assertEquals("pwd", entity.getPassword()); assertEquals("管理员", entity.getRealName());
        assertEquals("13800138000", entity.getPhone()); assertEquals("admin@test.com", entity.getEmail());
        assertEquals(1, entity.getStatus()); assertEquals(100L, entity.getCreateBy());
        assertEquals(200L, entity.getCreateOrg()); assertEquals(now, entity.getCreateTime());
        assertEquals(101L, entity.getUpdateBy()); assertEquals(now, entity.getUpdateTime());
        assertEquals(0, entity.getDeleteFlag());
    }
    @Test void testNoArgsConstructor() { SysUser e = new SysUser(); assertNull(e.getId()); assertNull(e.getUsername()); }
}

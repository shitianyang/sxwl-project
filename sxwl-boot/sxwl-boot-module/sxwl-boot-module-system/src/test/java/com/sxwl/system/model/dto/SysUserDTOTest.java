/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SysUserDTO 测试")
class SysUserDTOTest {
    @Test
    void testGettersAndSetters() {
        SysUserDTO dto = new SysUserDTO();
        dto.setId(1L); dto.setUsername("admin"); dto.setPassword("pwd"); dto.setRealName("管理员");
        dto.setPhone("13800138000"); dto.setEmail("admin@test.com"); dto.setStatus(1); dto.setCreateTime("2026-01-01");
        assertEquals(1L, dto.getId()); assertEquals("admin", dto.getUsername());
        assertEquals("pwd", dto.getPassword()); assertEquals("管理员", dto.getRealName());
        assertEquals("13800138000", dto.getPhone()); assertEquals("admin@test.com", dto.getEmail());
        assertEquals(1, dto.getStatus()); assertEquals("2026-01-01", dto.getCreateTime());
    }
}

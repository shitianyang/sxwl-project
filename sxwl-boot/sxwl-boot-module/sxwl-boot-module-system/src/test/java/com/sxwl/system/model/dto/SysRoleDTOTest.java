/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SysRoleDTO 测试")
class SysRoleDTOTest {
    @Test
    void testGettersAndSetters() {
        SysRoleDTO dto = new SysRoleDTO();
        dto.setId(1L); dto.setRoleCode("admin"); dto.setRoleName("管理员");
        dto.setDataScope(1); dto.setSort(1); dto.setStatus(1); dto.setDescription("描述"); dto.setCreateTime("2026-01-01");
        assertEquals(1L, dto.getId()); assertEquals("admin", dto.getRoleCode());
        assertEquals("管理员", dto.getRoleName()); assertEquals(1, dto.getDataScope());
        assertEquals(1, dto.getSort()); assertEquals(1, dto.getStatus());
        assertEquals("描述", dto.getDescription()); assertEquals("2026-01-01", dto.getCreateTime());
    }
}

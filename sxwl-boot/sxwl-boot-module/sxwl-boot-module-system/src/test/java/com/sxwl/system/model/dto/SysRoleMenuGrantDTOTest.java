/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SysRoleMenuGrantDTO 测试")
class SysRoleMenuGrantDTOTest {
    @Test
    void testGettersAndSetters() {
        SysRoleMenuGrantDTO dto = new SysRoleMenuGrantDTO();
        dto.setMenuIds(List.of(1L, 2L));
        assertEquals(List.of(1L, 2L), dto.getMenuIds());
    }
}

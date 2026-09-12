/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SysRoleDataScopeDTO 测试")
class SysRoleDataScopeDTOTest {
    @Test
    void testGettersAndSetters() {
        SysRoleDataScopeDTO dto = new SysRoleDataScopeDTO();
        dto.setOrgIds(List.of(1L, 2L));
        assertEquals(List.of(1L, 2L), dto.getOrgIds());
    }
}

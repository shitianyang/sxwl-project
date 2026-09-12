/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SysDashboardVO 测试")
class SysDashboardVOTest {
    @Test
    void testGettersAndSetters() {
        SysDashboardVO vo = new SysDashboardVO();
        vo.setUserCount(100); vo.setRoleCount(5); vo.setMenuCount(50); vo.setTodayLogCount(10);
        assertEquals(100, vo.getUserCount()); assertEquals(5, vo.getRoleCount());
        assertEquals(50, vo.getMenuCount()); assertEquals(10, vo.getTodayLogCount());
    }
}

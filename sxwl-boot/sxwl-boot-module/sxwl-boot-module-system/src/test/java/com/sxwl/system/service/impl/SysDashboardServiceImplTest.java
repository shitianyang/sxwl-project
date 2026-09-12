/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.service.impl;

import com.sxwl.system.mapper.SysDashboardMapper;
import com.sxwl.system.model.dto.SysDashboardVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SysDashboardServiceImpl 测试")
class SysDashboardServiceImplTest {
    private SysDashboardServiceImpl service;
    @Mock private SysDashboardMapper dashboardMapper;

    @BeforeEach
    void setUp() { service = new SysDashboardServiceImpl(dashboardMapper); }

    @Test
    void testGetStatistics() {
        when(dashboardMapper.countUsers()).thenReturn(100L);
        when(dashboardMapper.countRoles()).thenReturn(5L);
        when(dashboardMapper.countMenus()).thenReturn(50L);
        when(dashboardMapper.countTodayLogs()).thenReturn(10L);

        SysDashboardVO vo = service.getStatistics();
        assertEquals(100, vo.getUserCount());
        assertEquals(5, vo.getRoleCount());
        assertEquals(50, vo.getMenuCount());
        assertEquals(10, vo.getTodayLogCount());
    }
}

/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.controller;

import com.sxwl.common.entity.SxwlResult;
import com.sxwl.system.model.dto.SysDashboardVO;
import com.sxwl.system.service.SysDashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SysDashboardController 测试")
class SysDashboardControllerTest {
    private SysDashboardController controller;
    @Mock private SysDashboardService dashboardService;

    @BeforeEach
    void setUp() { controller = new SysDashboardController(dashboardService); }

    @Test void testGetStatistics() {
        SysDashboardVO vo = new SysDashboardVO(); vo.setUserCount(100);
        when(dashboardService.getStatistics()).thenReturn(vo);
        SxwlResult<SysDashboardVO> result = controller.getStatistics();
        assertEquals(200, result.getCode()); assertSame(vo, result.getData());
    }
}

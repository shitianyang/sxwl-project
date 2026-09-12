/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.controller;

import com.github.pagehelper.PageInfo;
import com.sxwl.system.model.dto.SysLogDTO;
import com.sxwl.system.model.params.SysLogPageParams;
import com.sxwl.system.service.SysLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SysLogController 测试")
class SysLogControllerTest {
    private SysLogController controller;
    @Mock private SysLogService sysLogService;

    @BeforeEach
    void setUp() { controller = new SysLogController(sysLogService); }

    @Test void testGetLogPageByParams() {
        SysLogPageParams p = new SysLogPageParams(); PageInfo<SysLogDTO> page = new PageInfo<>();
        when(sysLogService.getLogPageByParams(p)).thenReturn(page);
        assertSame(page, controller.getLogPageByParams(p)); verify(sysLogService).getLogPageByParams(p);
    }
}

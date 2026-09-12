/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.controller;

import com.github.pagehelper.PageInfo;
import com.sxwl.system.model.dto.SysOnlineUserDTO;
import com.sxwl.system.service.SysOnlineUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SysOnlineUserController 测试")
class SysOnlineUserControllerTest {
    private SysOnlineUserController controller;
    @Mock private SysOnlineUserService sysOnlineUserService;

    @BeforeEach
    void setUp() { controller = new SysOnlineUserController(sysOnlineUserService); }

    @Test void testList() {
        PageInfo<SysOnlineUserDTO> page = new PageInfo<>();
        when(sysOnlineUserService.list(1, 10)).thenReturn(page);
        assertSame(page, controller.list(1, 10)); verify(sysOnlineUserService).list(1, 10);
    }
    @Test void testCount() {
        when(sysOnlineUserService.count()).thenReturn(5L);
        assertSame(5L, controller.count()); verify(sysOnlineUserService).count();
    }
    @Test void testForceLogout() {
        controller.forceLogout(1L); verify(sysOnlineUserService).forceLogout(1L);
    }
}

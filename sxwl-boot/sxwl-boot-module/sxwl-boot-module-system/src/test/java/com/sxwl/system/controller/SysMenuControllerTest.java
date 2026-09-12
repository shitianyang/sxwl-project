/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.controller;

import com.sxwl.system.model.dto.SysMenuDTO;
import com.sxwl.system.service.SysMenuService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SysMenuController 测试")
class SysMenuControllerTest {
    private SysMenuController controller;
    @Mock private SysMenuService sysMenuService;

    @BeforeEach
    void setUp() { controller = new SysMenuController(sysMenuService); }

    @Test void testGetMenuById() {
        SysMenuDTO dto = new SysMenuDTO(); when(sysMenuService.getMenuById(1L)).thenReturn(dto);
        assertSame(dto, controller.getMenuById(1L)); verify(sysMenuService).getMenuById(1L);
    }
    @Test void testGetMenuTree() {
        List<SysMenuDTO> list = List.of(new SysMenuDTO());
        when(sysMenuService.getMenuTree()).thenReturn(list); assertSame(list, controller.getMenuTree());
    }
    @Test void testGetAllMenuList() {
        List<SysMenuDTO> list = List.of(new SysMenuDTO());
        when(sysMenuService.getAllMenuList()).thenReturn(list); assertSame(list, controller.getAllMenuList());
    }
    @Test void testCreateMenu() { SysMenuDTO dto = new SysMenuDTO(); controller.createMenu(dto); verify(sysMenuService).createMenu(dto); }
    @Test void testUpdateMenu() { SysMenuDTO dto = new SysMenuDTO(); controller.updateMenu(dto); verify(sysMenuService).updateMenu(dto); }
    @Test void testDeleteMenuById() { controller.deleteMenuById(1L); verify(sysMenuService).deleteMenuById(1L); }
}

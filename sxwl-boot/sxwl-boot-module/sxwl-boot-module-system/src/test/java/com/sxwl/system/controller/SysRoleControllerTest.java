/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.controller;

import com.github.pagehelper.PageInfo;
import com.sxwl.system.model.dto.SysRoleDTO;
import com.sxwl.system.model.dto.SysRoleDataScopeDTO;
import com.sxwl.system.model.dto.SysRoleMenuGrantDTO;
import com.sxwl.system.model.params.SysRolePageParams;
import com.sxwl.system.service.SysRoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SysRoleController 测试")
class SysRoleControllerTest {
    private SysRoleController controller;
    @Mock private SysRoleService sysRoleService;

    @BeforeEach
    void setUp() { controller = new SysRoleController(sysRoleService); }

    @Test void testGetRoleById() {
        SysRoleDTO dto = new SysRoleDTO(); when(sysRoleService.getRoleById(1L)).thenReturn(dto);
        assertSame(dto, controller.getRoleById(1L)); verify(sysRoleService).getRoleById(1L);
    }
    @Test void testGetRolePageByParams() {
        SysRolePageParams p = new SysRolePageParams(); PageInfo<SysRoleDTO> page = new PageInfo<>();
        when(sysRoleService.getRolePageByParams(p)).thenReturn(page);
        assertSame(page, controller.getRolePageByParams(p)); verify(sysRoleService).getRolePageByParams(p);
    }
    @Test void testCreateRole() { SysRoleDTO dto = new SysRoleDTO(); controller.createRole(dto); verify(sysRoleService).createRole(dto); }
    @Test void testUpdateRole() { SysRoleDTO dto = new SysRoleDTO(); controller.updateRole(dto); verify(sysRoleService).updateRole(dto); }
    @Test void testDeleteRoleById() { controller.deleteRoleById(1L); verify(sysRoleService).deleteRoleById(1L); }
    @Test void testSaveRoleMenus() {
        SysRoleMenuGrantDTO grant = new SysRoleMenuGrantDTO(); grant.setMenuIds(List.of(1L));
        controller.saveRoleMenus(1L, grant); verify(sysRoleService).saveRoleMenus(1L, List.of(1L));
    }
    @Test void testGetMenuIdListByRoleId() {
        when(sysRoleService.getMenuIdListByRoleId(1L)).thenReturn(List.of(1L));
        assertEquals(List.of(1L), controller.getMenuIdListByRoleId(1L)); verify(sysRoleService).getMenuIdListByRoleId(1L);
    }
    @Test void testSaveRoleDataScope() {
        SysRoleDataScopeDTO scope = new SysRoleDataScopeDTO(); scope.setOrgIds(List.of(1L));
        controller.saveRoleDataScope(1L, scope); verify(sysRoleService).saveRoleDataScope(1L, List.of(1L));
    }
    @Test void testGetDataScopeOrgIdListByRoleId() {
        when(sysRoleService.getDataScopeOrgIdListByRoleId(1L)).thenReturn(List.of(1L));
        assertEquals(List.of(1L), controller.getDataScopeOrgIdListByRoleId(1L)); verify(sysRoleService).getDataScopeOrgIdListByRoleId(1L);
    }
}

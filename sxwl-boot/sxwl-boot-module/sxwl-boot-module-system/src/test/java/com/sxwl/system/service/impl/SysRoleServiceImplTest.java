/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.service.impl;

import com.github.pagehelper.PageInfo;
import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.common.utils.SxwlDiffUtils;
import com.sxwl.common.utils.SxwlSnowFlakeUtils;
import com.sxwl.system.mapper.SysRoleMapper;
import com.sxwl.system.model.dto.SysRoleDTO;
import com.sxwl.system.model.params.SysRolePageParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SysRoleServiceImpl 测试")
class SysRoleServiceImplTest {
    private SysRoleServiceImpl service;
    @Mock private SysRoleMapper sysRoleMapper;

    @BeforeEach
    void setUp() { service = new SysRoleServiceImpl(sysRoleMapper); }

    @Test
    void testGetRoleById_found() {
        SysRoleDTO dto = new SysRoleDTO();
        dto.setId(1L);
        when(sysRoleMapper.getRoleById(1L)).thenReturn(dto);
        assertEquals(1L, service.getRoleById(1L).getId());
    }

    @Test
    void testGetRoleById_notFound() {
        when(sysRoleMapper.getRoleById(1L)).thenReturn(null);
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.getRoleById(1L));
        assertEquals(10004, ex.getCode());
    }

    @Test
    void testGetRolePageByParams() {
        SysRolePageParams params = new SysRolePageParams();
        when(sysRoleMapper.getRolePageByParams(params)).thenReturn(List.of(new SysRoleDTO()));
        PageInfo<SysRoleDTO> result = service.getRolePageByParams(params);
        assertEquals(1, result.getList().size());
    }

    @Test
    void testCreateRole_codeDuplicate() {
        SysRoleDTO dto = new SysRoleDTO();
        dto.setRoleCode("admin");
        when(sysRoleMapper.checkRoleCodeUnique("admin", null)).thenReturn(1);
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.createRole(dto));
        assertEquals(10002, ex.getCode());
    }

    @Test
    void testCreateRole_insertFails() {
        SysRoleDTO dto = new SysRoleDTO();
        dto.setRoleCode("admin");
        when(sysRoleMapper.checkRoleCodeUnique("admin", null)).thenReturn(0);
        when(sysRoleMapper.insertRole(any())).thenReturn(0);
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.createRole(dto));
        assertEquals(10001, ex.getCode());
    }

    @Test
    void testCreateRole_success() {
        SysRoleDTO dto = new SysRoleDTO();
        dto.setRoleCode("admin");
        when(sysRoleMapper.checkRoleCodeUnique("admin", null)).thenReturn(0);
        when(sysRoleMapper.insertRole(any())).thenReturn(1);
        assertEquals(1, service.createRole(dto));
    }

    @Test
    void testUpdateRole_codeDuplicate() {
        SysRoleDTO dto = new SysRoleDTO();
        dto.setId(1L);
        dto.setRoleCode("admin");
        when(sysRoleMapper.checkRoleCodeUnique("admin", 1L)).thenReturn(1);
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.updateRole(dto));
        assertEquals(10002, ex.getCode());
    }

    @Test
    void testUpdateRole_success() {
        SysRoleDTO dto = new SysRoleDTO();
        dto.setId(1L);
        dto.setRoleCode("admin");
        when(sysRoleMapper.checkRoleCodeUnique("admin", 1L)).thenReturn(0);

        SysRoleDTO oldDto = new SysRoleDTO();
        oldDto.setRoleCode("old");
        when(sysRoleMapper.getRoleById(1L)).thenReturn(oldDto);

        try (MockedStatic<SxwlDiffUtils> diffUtils = mockStatic(SxwlDiffUtils.class)) {
            diffUtils.when(() -> SxwlDiffUtils.diff(any(), any())).thenReturn("diff");
            diffUtils.when(() -> SxwlDiffUtils.setContextDiff("diff")).then(invocation -> null);
            when(sysRoleMapper.updateRole(any())).thenReturn(1);

            assertEquals(1, service.updateRole(dto));
        }
    }

    @Test
    void testUpdateRole_notFound() {
        SysRoleDTO dto = new SysRoleDTO();
        dto.setId(1L);
        dto.setRoleCode("admin");
        when(sysRoleMapper.checkRoleCodeUnique("admin", 1L)).thenReturn(0);

        SysRoleDTO oldDto = new SysRoleDTO();
        when(sysRoleMapper.getRoleById(1L)).thenReturn(oldDto);

        try (MockedStatic<SxwlDiffUtils> diffUtils = mockStatic(SxwlDiffUtils.class)) {
            diffUtils.when(() -> SxwlDiffUtils.diff(any(), any())).thenReturn(null);
            when(sysRoleMapper.updateRole(any())).thenReturn(0);

            SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.updateRole(dto));
            assertEquals(10004, ex.getCode());
        }
    }

    @Test
    void testDeleteRoleById_success() {
        when(sysRoleMapper.deleteRoleById(1L)).thenReturn(1);
        assertEquals(1, service.deleteRoleById(1L));
    }

    @Test
    void testDeleteRoleById_notFound() {
        when(sysRoleMapper.deleteRoleById(1L)).thenReturn(0);
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.deleteRoleById(1L));
        assertEquals(10004, ex.getCode());
    }

    @Test
    void testSaveRoleMenus_empty() {
        service.saveRoleMenus(1L, List.of());
        verify(sysRoleMapper).deleteRoleMenusByRoleId(1L);
        verify(sysRoleMapper, never()).batchInsertRoleMenus(any(), any(), any(), any(), any(), any());
    }

    @Test
    void testSaveRoleMenus_withMenuIds() {
        try (MockedStatic<SxwlSnowFlakeUtils> sf = mockStatic(SxwlSnowFlakeUtils.class)) {
            sf.when(SxwlSnowFlakeUtils::nextId).thenReturn(100L, 101L);

            service.saveRoleMenus(1L, List.of(10L, 20L));

            verify(sysRoleMapper).deleteRoleMenusByRoleId(1L);
            verify(sysRoleMapper).batchInsertRoleMenus(eq(1L), eq(List.of(10L, 20L)), eq(List.of(100L, 101L)), eq(0L), eq(0L), any());
        }
    }

    @Test
    void testSaveRoleDataScope_empty() {
        service.saveRoleDataScope(1L, List.of());
        verify(sysRoleMapper).deleteRoleDataScopeByRoleId(1L);
        verify(sysRoleMapper, never()).batchInsertRoleDataScope(any(), any(), any(), any(), any(), any());
    }

    @Test
    void testSaveRoleDataScope_withOrgIds() {
        try (MockedStatic<SxwlSnowFlakeUtils> sf = mockStatic(SxwlSnowFlakeUtils.class)) {
            sf.when(SxwlSnowFlakeUtils::nextId).thenReturn(200L);

            service.saveRoleDataScope(1L, List.of(10L));

            verify(sysRoleMapper).deleteRoleDataScopeByRoleId(1L);
            verify(sysRoleMapper).batchInsertRoleDataScope(eq(1L), eq(List.of(10L)), eq(List.of(200L)), eq(0L), eq(0L), any());
        }
    }

    @Test
    void testGetMenuIdListByRoleId() {
        when(sysRoleMapper.getMenuIdListByRoleId(1L)).thenReturn(List.of(1L, 2L));
        assertEquals(List.of(1L, 2L), service.getMenuIdListByRoleId(1L));
    }

    @Test
    void testGetDataScopeOrgIdListByRoleId() {
        when(sysRoleMapper.getDataScopeOrgIdListByRoleId(1L)).thenReturn(List.of(10L));
        assertEquals(List.of(10L), service.getDataScopeOrgIdListByRoleId(1L));
    }
}

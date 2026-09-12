/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.service.impl;

import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.common.utils.SxwlDiffUtils;
import com.sxwl.common.utils.SxwlTreeUtils;
import com.sxwl.system.mapper.SysMenuMapper;
import com.sxwl.system.model.dto.SysMenuDTO;
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
@DisplayName("SysMenuServiceImpl 测试")
class SysMenuServiceImplTest {
    private SysMenuServiceImpl service;
    @Mock private SysMenuMapper sysMenuMapper;

    @BeforeEach
    void setUp() { service = new SysMenuServiceImpl(sysMenuMapper); }

    @Test
    void testGetMenuById_found() {
        SysMenuDTO dto = new SysMenuDTO();
        dto.setId(1L);
        when(sysMenuMapper.getMenuById(1L)).thenReturn(dto);
        assertEquals(1L, service.getMenuById(1L).getId());
    }

    @Test
    void testGetMenuById_notFound() {
        when(sysMenuMapper.getMenuById(1L)).thenReturn(null);
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.getMenuById(1L));
        assertEquals(10004, ex.getCode());
    }

    @Test
    void testGetMenuTree() {
        List<SysMenuDTO> allMenus = List.of(new SysMenuDTO());
        when(sysMenuMapper.selectAllMenus()).thenReturn(allMenus);
        try (MockedStatic<SxwlTreeUtils> treeUtils = mockStatic(SxwlTreeUtils.class)) {
            treeUtils.when(() -> SxwlTreeUtils.buildTree(allMenus)).thenReturn(allMenus);
            assertEquals(1, service.getMenuTree().size());
        }
    }

    @Test
    void testGetAllMenuList() {
        when(sysMenuMapper.selectAllMenus()).thenReturn(List.of(new SysMenuDTO()));
        assertEquals(1, service.getAllMenuList().size());
    }

    @Test
    void testCreateMenu_withParent_found() {
        SysMenuDTO dto = new SysMenuDTO();
        dto.setMenuName("test");
        dto.setParentId(1L);
        SysMenuDTO parent = new SysMenuDTO();
        parent.setAncestors("0");
        when(sysMenuMapper.getMenuById(1L)).thenReturn(parent);
        when(sysMenuMapper.insertMenu(any())).thenReturn(1);

        assertEquals(1, service.createMenu(dto));
        verify(sysMenuMapper).insertMenu(argThat(m -> "0,1".equals(m.getAncestors())));
    }

    @Test
    void testCreateMenu_withParent_notFound() {
        SysMenuDTO dto = new SysMenuDTO();
        dto.setParentId(1L);
        when(sysMenuMapper.getMenuById(1L)).thenReturn(null);
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.createMenu(dto));
        assertEquals(10004, ex.getCode());
    }

    @Test
    void testCreateMenu_topLevel() {
        SysMenuDTO dto = new SysMenuDTO();
        dto.setMenuName("top");
        when(sysMenuMapper.insertMenu(any())).thenReturn(1);
        assertEquals(1, service.createMenu(dto));
        verify(sysMenuMapper).insertMenu(argThat(m -> "0".equals(m.getAncestors()) && 0L == m.getParentId()));
    }

    @Test
    void testCreateMenu_permsDuplicate() {
        SysMenuDTO dto = new SysMenuDTO();
        dto.setMenuName("test");
        dto.setPerms("system:user");
        when(sysMenuMapper.checkPermsUnique("system:user", null)).thenReturn(1);
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.createMenu(dto));
        assertEquals(10001, ex.getCode());
    }

    @Test
    void testCreateMenu_insertFails() {
        SysMenuDTO dto = new SysMenuDTO();
        dto.setMenuName("test");
        when(sysMenuMapper.insertMenu(any())).thenReturn(0);
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.createMenu(dto));
        assertEquals(10001, ex.getCode());
    }

    @Test
    void testUpdateMenu_parentChanged_newParentFound() {
        SysMenuDTO dto = new SysMenuDTO();
        dto.setId(1L);
        dto.setMenuName("test");
        dto.setParentId(2L);

        SysMenuDTO old = new SysMenuDTO();
        old.setMenuName("old");
        old.setParentId(1L);
        SysMenuDTO parent = new SysMenuDTO();
        parent.setAncestors("0,1");
        when(sysMenuMapper.getMenuById(1L)).thenReturn(old);
        when(sysMenuMapper.getMenuById(2L)).thenReturn(parent);

        try (MockedStatic<SxwlDiffUtils> diffUtils = mockStatic(SxwlDiffUtils.class)) {
            diffUtils.when(() -> SxwlDiffUtils.diff(any(), any())).thenReturn(null);
            when(sysMenuMapper.updateMenu(any())).thenReturn(1);

            assertEquals(1, service.updateMenu(dto));
            verify(sysMenuMapper).updateMenu(argThat(m -> "0,1,2".equals(m.getAncestors())));
        }
    }

    @Test
    void testUpdateMenu_parentChanged_newParentNotFound() {
        SysMenuDTO dto = new SysMenuDTO();
        dto.setId(1L);
        dto.setParentId(2L);

        SysMenuDTO old = new SysMenuDTO();
        old.setParentId(1L);
        when(sysMenuMapper.getMenuById(1L)).thenReturn(old);
        when(sysMenuMapper.getMenuById(2L)).thenReturn(null);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.updateMenu(dto));
        assertEquals(10004, ex.getCode());
    }

    @Test
    void testUpdateMenu_parentUnchanged() {
        SysMenuDTO dto = new SysMenuDTO();
        dto.setId(1L);
        dto.setMenuName("test");
        dto.setParentId(1L);

        SysMenuDTO old = new SysMenuDTO();
        old.setMenuName("old");
        old.setParentId(1L);
        old.setAncestors("0");
        when(sysMenuMapper.getMenuById(1L)).thenReturn(old);

        try (MockedStatic<SxwlDiffUtils> diffUtils = mockStatic(SxwlDiffUtils.class)) {
            diffUtils.when(() -> SxwlDiffUtils.diff(any(), any())).thenReturn(null);
            when(sysMenuMapper.updateMenu(any())).thenReturn(1);

            assertEquals(1, service.updateMenu(dto));
            verify(sysMenuMapper).updateMenu(argThat(m -> "0".equals(m.getAncestors())));
        }
    }

    @Test
    void testUpdateMenu_permsDuplicate() {
        SysMenuDTO dto = new SysMenuDTO();
        dto.setId(1L);
        dto.setPerms("system:user");

        SysMenuDTO old = new SysMenuDTO();
        old.setParentId(0L);
        when(sysMenuMapper.getMenuById(1L)).thenReturn(old);

        try (MockedStatic<SxwlDiffUtils> diffUtils = mockStatic(SxwlDiffUtils.class)) {
            diffUtils.when(() -> SxwlDiffUtils.diff(any(), any())).thenReturn(null);
            when(sysMenuMapper.checkPermsUnique("system:user", 1L)).thenReturn(1);

            SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.updateMenu(dto));
            assertEquals(10001, ex.getCode());
        }
    }

    @Test
    void testUpdateMenu_notFound() {
        SysMenuDTO dto = new SysMenuDTO();
        dto.setId(1L);

        SysMenuDTO old = new SysMenuDTO();
        old.setParentId(0L);
        when(sysMenuMapper.getMenuById(1L)).thenReturn(old);

        try (MockedStatic<SxwlDiffUtils> diffUtils = mockStatic(SxwlDiffUtils.class)) {
            diffUtils.when(() -> SxwlDiffUtils.diff(any(), any())).thenReturn(null);
            when(sysMenuMapper.updateMenu(any())).thenReturn(0);

            SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.updateMenu(dto));
            assertEquals(10004, ex.getCode());
        }
    }

    @Test
    void testDeleteMenuById_hasChildren() {
        when(sysMenuMapper.countChildrenByParentId(1L)).thenReturn(2);
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.deleteMenuById(1L));
        assertEquals(10001, ex.getCode());
    }

    @Test
    void testDeleteMenuById_success() {
        when(sysMenuMapper.countChildrenByParentId(1L)).thenReturn(0);
        when(sysMenuMapper.deleteMenuById(1L)).thenReturn(1);
        assertEquals(1, service.deleteMenuById(1L));
    }

    @Test
    void testDeleteMenuById_notFound() {
        when(sysMenuMapper.countChildrenByParentId(1L)).thenReturn(0);
        when(sysMenuMapper.deleteMenuById(1L)).thenReturn(0);
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.deleteMenuById(1L));
        assertEquals(10004, ex.getCode());
    }

    @Test
    void testGetUserMenuTree() {
        List<SysMenuDTO> allMenus = List.of(new SysMenuDTO());
        when(sysMenuMapper.selectMenusByUserId(1L)).thenReturn(allMenus);
        try (MockedStatic<SxwlTreeUtils> treeUtils = mockStatic(SxwlTreeUtils.class)) {
            treeUtils.when(() -> SxwlTreeUtils.buildTree(allMenus)).thenReturn(allMenus);
            assertEquals(1, service.getUserMenuTree(1L).size());
        }
    }
}

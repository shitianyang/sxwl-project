/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.service.impl;

import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.common.utils.SxwlDiffUtils;
import com.sxwl.common.utils.SxwlTreeUtils;
import com.sxwl.system.mapper.SysOrganizationMapper;
import com.sxwl.system.model.dto.SysOrganizationDTO;
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
@DisplayName("SysOrganizationServiceImpl 测试")
class SysOrganizationServiceImplTest {
    private SysOrganizationServiceImpl service;
    @Mock private SysOrganizationMapper sysOrganizationMapper;

    @BeforeEach
    void setUp() { service = new SysOrganizationServiceImpl(sysOrganizationMapper); }

    @Test
    void testGetOrganizationById_found() {
        SysOrganizationDTO dto = new SysOrganizationDTO();
        dto.setId(1L);
        when(sysOrganizationMapper.getOrganizationById(1L)).thenReturn(dto);
        assertEquals(1L, service.getOrganizationById(1L).getId());
    }

    @Test
    void testGetOrganizationById_notFound() {
        when(sysOrganizationMapper.getOrganizationById(1L)).thenReturn(null);
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.getOrganizationById(1L));
        assertEquals(10004, ex.getCode());
    }

    @Test
    void testGetOrganizationTree() {
        List<SysOrganizationDTO> all = List.of(new SysOrganizationDTO());
        when(sysOrganizationMapper.selectAllOrganizations()).thenReturn(all);
        try (MockedStatic<SxwlTreeUtils> treeUtils = mockStatic(SxwlTreeUtils.class)) {
            treeUtils.when(() -> SxwlTreeUtils.buildTree(all)).thenReturn(all);
            assertEquals(1, service.getOrganizationTree().size());
        }
    }

    @Test
    void testGetAllOrganizationList() {
        when(sysOrganizationMapper.selectAllOrganizations()).thenReturn(List.of(new SysOrganizationDTO()));
        assertEquals(1, service.getAllOrganizationList().size());
    }

    @Test
    void testCreateOrganization_codeDuplicate() {
        SysOrganizationDTO dto = new SysOrganizationDTO();
        dto.setOrgCode("ORG001");
        when(sysOrganizationMapper.checkOrgCodeUnique("ORG001", null)).thenReturn(1);
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.createOrganization(dto));
        assertEquals(10002, ex.getCode());
    }

    @Test
    void testCreateOrganization_withParent_found() {
        SysOrganizationDTO dto = new SysOrganizationDTO();
        dto.setOrgCode("ORG001");
        dto.setParentId(1L);
        SysOrganizationDTO parent = new SysOrganizationDTO();
        parent.setAncestors("0");
        when(sysOrganizationMapper.checkOrgCodeUnique("ORG001", null)).thenReturn(0);
        when(sysOrganizationMapper.getOrganizationById(1L)).thenReturn(parent);
        when(sysOrganizationMapper.insertOrganization(any())).thenReturn(1);

        assertEquals(1, service.createOrganization(dto));
        verify(sysOrganizationMapper).insertOrganization(argThat(m -> "0,1".equals(m.getAncestors())));
    }

    @Test
    void testCreateOrganization_withParent_notFound() {
        SysOrganizationDTO dto = new SysOrganizationDTO();
        dto.setParentId(1L);
        when(sysOrganizationMapper.checkOrgCodeUnique(any(), any())).thenReturn(0);
        when(sysOrganizationMapper.getOrganizationById(1L)).thenReturn(null);
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.createOrganization(dto));
        assertEquals(10004, ex.getCode());
    }

    @Test
    void testCreateOrganization_topLevel() {
        SysOrganizationDTO dto = new SysOrganizationDTO();
        dto.setOrgCode("ORG001");
        when(sysOrganizationMapper.checkOrgCodeUnique("ORG001", null)).thenReturn(0);
        when(sysOrganizationMapper.insertOrganization(any())).thenReturn(1);

        assertEquals(1, service.createOrganization(dto));
        verify(sysOrganizationMapper).insertOrganization(argThat(m -> "0".equals(m.getAncestors()) && 0L == m.getParentId()));
    }

    @Test
    void testCreateOrganization_insertFails() {
        SysOrganizationDTO dto = new SysOrganizationDTO();
        dto.setOrgCode("ORG001");
        when(sysOrganizationMapper.checkOrgCodeUnique("ORG001", null)).thenReturn(0);
        when(sysOrganizationMapper.insertOrganization(any())).thenReturn(0);
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.createOrganization(dto));
        assertEquals(10001, ex.getCode());
    }

    @Test
    void testUpdateOrganization_codeDuplicate() {
        SysOrganizationDTO dto = new SysOrganizationDTO();
        dto.setId(1L);
        dto.setOrgCode("ORG001");
        when(sysOrganizationMapper.checkOrgCodeUnique("ORG001", 1L)).thenReturn(1);
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.updateOrganization(dto));
        assertEquals(10002, ex.getCode());
    }

    @Test
    void testUpdateOrganization_parentChanged_newParentFound() {
        SysOrganizationDTO dto = new SysOrganizationDTO();
        dto.setId(1L);
        dto.setOrgCode("ORG001");
        dto.setParentId(2L);

        SysOrganizationDTO old = new SysOrganizationDTO();
        old.setOrgCode("OLD");
        old.setParentId(1L);
        SysOrganizationDTO parent = new SysOrganizationDTO();
        parent.setAncestors("0");
        when(sysOrganizationMapper.checkOrgCodeUnique("ORG001", 1L)).thenReturn(0);
        when(sysOrganizationMapper.getOrganizationById(1L)).thenReturn(old);
        when(sysOrganizationMapper.getOrganizationById(2L)).thenReturn(parent);

        try (MockedStatic<SxwlDiffUtils> diffUtils = mockStatic(SxwlDiffUtils.class)) {
            diffUtils.when(() -> SxwlDiffUtils.diff(any(), any())).thenReturn(null);
            when(sysOrganizationMapper.updateOrganization(any())).thenReturn(1);

            assertEquals(1, service.updateOrganization(dto));
            verify(sysOrganizationMapper).updateOrganization(argThat(m -> "0,2".equals(m.getAncestors())));
        }
    }

    @Test
    void testUpdateOrganization_parentChanged_newParentNotFound() {
        SysOrganizationDTO dto = new SysOrganizationDTO();
        dto.setId(1L);
        dto.setOrgCode("ORG001");
        dto.setParentId(2L);

        SysOrganizationDTO old = new SysOrganizationDTO();
        old.setParentId(1L);
        when(sysOrganizationMapper.checkOrgCodeUnique("ORG001", 1L)).thenReturn(0);
        when(sysOrganizationMapper.getOrganizationById(1L)).thenReturn(old);
        when(sysOrganizationMapper.getOrganizationById(2L)).thenReturn(null);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.updateOrganization(dto));
        assertEquals(10004, ex.getCode());
    }

    @Test
    void testUpdateOrganization_parentUnchanged() {
        SysOrganizationDTO dto = new SysOrganizationDTO();
        dto.setId(1L);
        dto.setOrgCode("ORG001");
        dto.setParentId(1L);

        SysOrganizationDTO old = new SysOrganizationDTO();
        old.setParentId(1L);
        old.setAncestors("0");
        when(sysOrganizationMapper.checkOrgCodeUnique("ORG001", 1L)).thenReturn(0);
        when(sysOrganizationMapper.getOrganizationById(1L)).thenReturn(old);

        try (MockedStatic<SxwlDiffUtils> diffUtils = mockStatic(SxwlDiffUtils.class)) {
            diffUtils.when(() -> SxwlDiffUtils.diff(any(), any())).thenReturn(null);
            when(sysOrganizationMapper.updateOrganization(any())).thenReturn(1);

            assertEquals(1, service.updateOrganization(dto));
        }
    }

    @Test
    void testUpdateOrganization_notFound() {
        SysOrganizationDTO dto = new SysOrganizationDTO();
        dto.setId(1L);
        dto.setOrgCode("ORG001");
        SysOrganizationDTO old = new SysOrganizationDTO();
        old.setParentId(0L);
        when(sysOrganizationMapper.checkOrgCodeUnique("ORG001", 1L)).thenReturn(0);
        when(sysOrganizationMapper.getOrganizationById(1L)).thenReturn(old);

        try (MockedStatic<SxwlDiffUtils> diffUtils = mockStatic(SxwlDiffUtils.class)) {
            diffUtils.when(() -> SxwlDiffUtils.diff(any(), any())).thenReturn(null);
            when(sysOrganizationMapper.updateOrganization(any())).thenReturn(0);

            SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.updateOrganization(dto));
            assertEquals(10004, ex.getCode());
        }
    }

    @Test
    void testDeleteOrganizationById_hasChildren() {
        when(sysOrganizationMapper.countChildrenByParentId(1L)).thenReturn(2);
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.deleteOrganizationById(1L));
        assertEquals(10001, ex.getCode());
    }

    @Test
    void testDeleteOrganizationById_success() {
        when(sysOrganizationMapper.countChildrenByParentId(1L)).thenReturn(0);
        when(sysOrganizationMapper.deleteOrganizationById(1L)).thenReturn(1);
        assertEquals(1, service.deleteOrganizationById(1L));
    }

    @Test
    void testDeleteOrganizationById_notFound() {
        when(sysOrganizationMapper.countChildrenByParentId(1L)).thenReturn(0);
        when(sysOrganizationMapper.deleteOrganizationById(1L)).thenReturn(0);
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.deleteOrganizationById(1L));
        assertEquals(10004, ex.getCode());
    }
}

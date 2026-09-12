/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.controller;

import com.sxwl.system.model.dto.SysOrganizationDTO;
import com.sxwl.system.service.SysOrganizationService;
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
@DisplayName("SysOrganizationController 测试")
class SysOrganizationControllerTest {
    private SysOrganizationController controller;
    @Mock private SysOrganizationService sysOrganizationService;

    @BeforeEach
    void setUp() { controller = new SysOrganizationController(sysOrganizationService); }

    @Test void testGetOrganizationById() {
        SysOrganizationDTO dto = new SysOrganizationDTO(); when(sysOrganizationService.getOrganizationById(1L)).thenReturn(dto);
        assertSame(dto, controller.getOrganizationById(1L)); verify(sysOrganizationService).getOrganizationById(1L);
    }
    @Test void testGetOrganizationTree() {
        List<SysOrganizationDTO> list = List.of(new SysOrganizationDTO());
        when(sysOrganizationService.getOrganizationTree()).thenReturn(list); assertSame(list, controller.getOrganizationTree());
    }
    @Test void testGetAllOrganizationList() {
        List<SysOrganizationDTO> list = List.of(new SysOrganizationDTO());
        when(sysOrganizationService.getAllOrganizationList()).thenReturn(list); assertSame(list, controller.getAllOrganizationList());
    }
    @Test void testCreateOrganization() { SysOrganizationDTO dto = new SysOrganizationDTO(); controller.createOrganization(dto); verify(sysOrganizationService).createOrganization(dto); }
    @Test void testUpdateOrganization() { SysOrganizationDTO dto = new SysOrganizationDTO(); controller.updateOrganization(dto); verify(sysOrganizationService).updateOrganization(dto); }
    @Test void testDeleteOrganizationById() { controller.deleteOrganizationById(1L); verify(sysOrganizationService).deleteOrganizationById(1L); }
}

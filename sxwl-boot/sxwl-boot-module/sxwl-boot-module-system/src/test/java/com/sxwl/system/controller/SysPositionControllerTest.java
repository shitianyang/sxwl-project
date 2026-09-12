/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.controller;

import com.github.pagehelper.PageInfo;
import com.sxwl.system.model.dto.SysPositionDTO;
import com.sxwl.system.model.params.SysPositionPageParams;
import com.sxwl.system.service.SysPositionService;
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
@DisplayName("SysPositionController 测试")
class SysPositionControllerTest {
    private SysPositionController controller;
    @Mock private SysPositionService sysPositionService;

    @BeforeEach
    void setUp() { controller = new SysPositionController(sysPositionService); }

    @Test void testGetPositionById() {
        SysPositionDTO dto = new SysPositionDTO(); when(sysPositionService.getPositionById(1L)).thenReturn(dto);
        assertSame(dto, controller.getPositionById(1L)); verify(sysPositionService).getPositionById(1L);
    }
    @Test void testGetPositionPageByParams() {
        SysPositionPageParams p = new SysPositionPageParams(); PageInfo<SysPositionDTO> page = new PageInfo<>();
        when(sysPositionService.getPositionPageByParams(p)).thenReturn(page);
        assertSame(page, controller.getPositionPageByParams(p)); verify(sysPositionService).getPositionPageByParams(p);
    }
    @Test void testCreatePosition() { SysPositionDTO dto = new SysPositionDTO(); controller.createPosition(dto); verify(sysPositionService).createPosition(dto); }
    @Test void testUpdatePosition() { SysPositionDTO dto = new SysPositionDTO(); controller.updatePosition(dto); verify(sysPositionService).updatePosition(dto); }
    @Test void testDeletePositionById() { controller.deletePositionById(1L); verify(sysPositionService).deletePositionById(1L); }
    @Test void testBatchDeletePositionByIds() {
        controller.batchDeletePositionByIds(List.of(1L)); verify(sysPositionService).batchDeletePositionByIds(List.of(1L));
    }
}

/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.service.impl;

import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.system.mapper.SysPositionMapper;
import com.sxwl.system.model.dto.SysPositionDTO;
import com.sxwl.system.model.params.SysPositionPageParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SysPositionServiceImpl 测试")
class SysPositionServiceImplTest {
    private SysPositionServiceImpl service;
    @Mock private SysPositionMapper sysPositionMapper;

    @BeforeEach
    void setUp() { service = new SysPositionServiceImpl(sysPositionMapper); }

    @Test void testGetPositionById_found() {
        SysPositionDTO dto = new SysPositionDTO(); when(sysPositionMapper.getPositionById(1L)).thenReturn(dto);
        assertSame(dto, service.getPositionById(1L));
    }
    @Test void testGetPositionById_notFound() {
        when(sysPositionMapper.getPositionById(1L)).thenReturn(null);
        assertThrows(SxwlBusinessException.class, () -> service.getPositionById(1L));
    }
    @Test void testGetPositionPageByParams() {
        SysPositionPageParams p = new SysPositionPageParams();
        when(sysPositionMapper.getPositionPageByParams(p)).thenReturn(List.of(new SysPositionDTO()));
        assertEquals(1, service.getPositionPageByParams(p).getList().size());
    }
    @Test void testCreatePosition_duplicate() {
        SysPositionDTO dto = new SysPositionDTO(); dto.setPositionCode("P001");
        when(sysPositionMapper.checkPositionCodeUnique("P001", null)).thenReturn(1);
        assertThrows(SxwlBusinessException.class, () -> service.createPosition(dto));
    }
    @Test void testCreatePosition_success() {
        SysPositionDTO dto = new SysPositionDTO(); dto.setPositionCode("P001");
        when(sysPositionMapper.checkPositionCodeUnique("P001", null)).thenReturn(0);
        when(sysPositionMapper.insertPosition(any())).thenReturn(1);
        assertEquals(1, service.createPosition(dto));
    }
    @Test void testDeletePositionById_success() {
        when(sysPositionMapper.deletePositionById(1L)).thenReturn(1);
        assertEquals(1, service.deletePositionById(1L));
    }
    @Test void testDeletePositionById_notFound() {
        when(sysPositionMapper.deletePositionById(1L)).thenReturn(0);
        assertThrows(SxwlBusinessException.class, () -> service.deletePositionById(1L));
    }
    @Test void testBatchDeleteByIds_empty() {
        assertThrows(SxwlBusinessException.class, () -> service.batchDeletePositionByIds(List.of()));
    }
    @Test void testBatchDeleteByIds_success() {
        when(sysPositionMapper.batchDeletePositionByIds(List.of(1L))).thenReturn(1);
        assertEquals(1, service.batchDeletePositionByIds(List.of(1L)));
    }
}

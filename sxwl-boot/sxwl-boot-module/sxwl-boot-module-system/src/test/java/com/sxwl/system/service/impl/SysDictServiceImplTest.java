/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.service.impl;

import com.github.pagehelper.PageInfo;
import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.common.utils.SxwlDiffUtils;
import com.sxwl.system.mapper.SysDictDetailMapper;
import com.sxwl.system.mapper.SysDictMapper;
import com.sxwl.system.model.dto.SysDictDTO;
import com.sxwl.system.model.dto.SysDictDetailDTO;
import com.sxwl.system.model.params.SysDictPageParams;
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
@DisplayName("SysDictServiceImpl 测试")
class SysDictServiceImplTest {
    private SysDictServiceImpl service;
    @Mock private SysDictMapper sysDictMapper;
    @Mock private SysDictDetailMapper sysDictDetailMapper;

    @BeforeEach
    void setUp() { service = new SysDictServiceImpl(sysDictMapper, sysDictDetailMapper); }

    // ==================== Dict CRUD ====================

    @Test
    void testGetDictById_found() {
        SysDictDTO dto = new SysDictDTO();
        dto.setId(1L);
        when(sysDictMapper.getDictById(1L)).thenReturn(dto);
        assertEquals(1L, service.getDictById(1L).getId());
    }

    @Test
    void testGetDictById_notFound() {
        when(sysDictMapper.getDictById(1L)).thenReturn(null);
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.getDictById(1L));
        assertEquals(10004, ex.getCode());
    }

    @Test
    void testGetDictPageByParams() {
        SysDictPageParams params = new SysDictPageParams();
        when(sysDictMapper.getDictPageByParams(params)).thenReturn(List.of(new SysDictDTO()));
        PageInfo<SysDictDTO> result = service.getDictPageByParams(params);
        assertEquals(1, result.getList().size());
    }

    @Test
    void testCreateDict_codeDuplicate() {
        SysDictDTO dto = new SysDictDTO();
        dto.setDictCode("DICT001");
        when(sysDictMapper.checkDictCodeUnique("DICT001", null)).thenReturn(1);
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.createDict(dto));
        assertEquals(10002, ex.getCode());
    }

    @Test
    void testCreateDict_insertFails() {
        SysDictDTO dto = new SysDictDTO();
        dto.setDictCode("DICT001");
        when(sysDictMapper.checkDictCodeUnique("DICT001", null)).thenReturn(0);
        when(sysDictMapper.insertDict(any())).thenReturn(0);
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.createDict(dto));
        assertEquals(10001, ex.getCode());
    }

    @Test
    void testCreateDict_success() {
        SysDictDTO dto = new SysDictDTO();
        dto.setDictCode("DICT001");
        when(sysDictMapper.checkDictCodeUnique("DICT001", null)).thenReturn(0);
        when(sysDictMapper.insertDict(any())).thenReturn(1);
        assertEquals(1, service.createDict(dto));
    }

    @Test
    void testUpdateDict_codeDuplicate() {
        SysDictDTO dto = new SysDictDTO();
        dto.setId(1L);
        dto.setDictCode("DICT001");
        when(sysDictMapper.checkDictCodeUnique("DICT001", 1L)).thenReturn(1);
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.updateDict(dto));
        assertEquals(10002, ex.getCode());
    }

    @Test
    void testUpdateDict_success() {
        SysDictDTO dto = new SysDictDTO();
        dto.setId(1L);
        dto.setDictCode("DICT001");

        SysDictDTO oldDto = new SysDictDTO();
        oldDto.setDictCode("OLD");
        when(sysDictMapper.checkDictCodeUnique("DICT001", 1L)).thenReturn(0);
        when(sysDictMapper.getDictById(1L)).thenReturn(oldDto);

        try (MockedStatic<SxwlDiffUtils> diffUtils = mockStatic(SxwlDiffUtils.class)) {
            diffUtils.when(() -> SxwlDiffUtils.diff(any(), any())).thenReturn("diff");
            diffUtils.when(() -> SxwlDiffUtils.setContextDiff("diff")).then(invocation -> null);
            when(sysDictMapper.updateDict(any())).thenReturn(1);

            assertEquals(1, service.updateDict(dto));
        }
    }

    @Test
    void testUpdateDict_notFound() {
        SysDictDTO dto = new SysDictDTO();
        dto.setId(1L);
        dto.setDictCode("DICT001");

        SysDictDTO oldDto = new SysDictDTO();
        when(sysDictMapper.checkDictCodeUnique("DICT001", 1L)).thenReturn(0);
        when(sysDictMapper.getDictById(1L)).thenReturn(oldDto);

        try (MockedStatic<SxwlDiffUtils> diffUtils = mockStatic(SxwlDiffUtils.class)) {
            diffUtils.when(() -> SxwlDiffUtils.diff(any(), any())).thenReturn(null);
            when(sysDictMapper.updateDict(any())).thenReturn(0);

            SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.updateDict(dto));
            assertEquals(10004, ex.getCode());
        }
    }

    @Test
    void testDeleteDictById_success() {
        when(sysDictMapper.deleteDictById(1L)).thenReturn(1);
        assertEquals(1, service.deleteDictById(1L));
    }

    @Test
    void testDeleteDictById_notFound() {
        when(sysDictMapper.deleteDictById(1L)).thenReturn(0);
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.deleteDictById(1L));
        assertEquals(10004, ex.getCode());
    }

    // ==================== Detail CRUD ====================

    @Test
    void testGetDetailListByDictId() {
        when(sysDictDetailMapper.getDetailListByDictId(1L)).thenReturn(List.of(new SysDictDetailDTO()));
        assertEquals(1, service.getDetailListByDictId(1L).size());
    }

    @Test
    void testCreateDetail_valueDuplicate() {
        SysDictDetailDTO dto = new SysDictDetailDTO();
        dto.setDetailValue("V1");
        when(sysDictDetailMapper.checkDetailValueUnique("V1", null)).thenReturn(1);
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.createDetail(dto));
        assertEquals(10002, ex.getCode());
    }

    @Test
    void testCreateDetail_insertFails() {
        SysDictDetailDTO dto = new SysDictDetailDTO();
        dto.setDetailValue("V1");
        when(sysDictDetailMapper.checkDetailValueUnique("V1", null)).thenReturn(0);
        when(sysDictDetailMapper.insertDetail(any())).thenReturn(0);
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.createDetail(dto));
        assertEquals(10001, ex.getCode());
    }

    @Test
    void testCreateDetail_success() {
        SysDictDetailDTO dto = new SysDictDetailDTO();
        dto.setDetailValue("V1");
        when(sysDictDetailMapper.checkDetailValueUnique("V1", null)).thenReturn(0);
        when(sysDictDetailMapper.insertDetail(any())).thenReturn(1);
        assertEquals(1, service.createDetail(dto));
    }

    @Test
    void testUpdateDetail_valueDuplicate() {
        SysDictDetailDTO dto = new SysDictDetailDTO();
        dto.setId(1L);
        dto.setDetailValue("V1");
        when(sysDictDetailMapper.checkDetailValueUnique("V1", 1L)).thenReturn(1);
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.updateDetail(dto));
        assertEquals(10002, ex.getCode());
    }

    @Test
    void testUpdateDetail_success() {
        SysDictDetailDTO dto = new SysDictDetailDTO();
        dto.setId(1L);
        dto.setDetailValue("V1");

        SysDictDetailDTO oldDto = new SysDictDetailDTO();
        oldDto.setDetailValue("OLD");
        when(sysDictDetailMapper.checkDetailValueUnique("V1", 1L)).thenReturn(0);
        when(sysDictDetailMapper.getDetailById(1L)).thenReturn(oldDto);

        try (MockedStatic<SxwlDiffUtils> diffUtils = mockStatic(SxwlDiffUtils.class)) {
            diffUtils.when(() -> SxwlDiffUtils.diff(any(), any())).thenReturn("diff");
            diffUtils.when(() -> SxwlDiffUtils.setContextDiff("diff")).then(invocation -> null);
            when(sysDictDetailMapper.updateDetail(any())).thenReturn(1);

            assertEquals(1, service.updateDetail(dto));
        }
    }

    @Test
    void testUpdateDetail_notFound() {
        SysDictDetailDTO dto = new SysDictDetailDTO();
        dto.setId(1L);
        dto.setDetailValue("V1");

        SysDictDetailDTO oldDto = new SysDictDetailDTO();
        when(sysDictDetailMapper.checkDetailValueUnique("V1", 1L)).thenReturn(0);
        when(sysDictDetailMapper.getDetailById(1L)).thenReturn(oldDto);

        try (MockedStatic<SxwlDiffUtils> diffUtils = mockStatic(SxwlDiffUtils.class)) {
            diffUtils.when(() -> SxwlDiffUtils.diff(any(), any())).thenReturn(null);
            when(sysDictDetailMapper.updateDetail(any())).thenReturn(0);

            SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.updateDetail(dto));
            assertEquals(10004, ex.getCode());
        }
    }

    @Test
    void testDeleteDetailById_success() {
        when(sysDictDetailMapper.deleteDetailById(1L)).thenReturn(1);
        assertEquals(1, service.deleteDetailById(1L));
    }

    @Test
    void testDeleteDetailById_notFound() {
        when(sysDictDetailMapper.deleteDetailById(1L)).thenReturn(0);
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class, () -> service.deleteDetailById(1L));
        assertEquals(10004, ex.getCode());
    }
}

/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.controller;

import com.github.pagehelper.PageInfo;
import com.sxwl.system.model.dto.SysDictDTO;
import com.sxwl.system.model.dto.SysDictDetailDTO;
import com.sxwl.system.model.params.SysDictPageParams;
import com.sxwl.system.service.SysDictService;
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
@DisplayName("SysDictController 测试")
class SysDictControllerTest {
    private SysDictController controller;
    @Mock private SysDictService sysDictService;

    @BeforeEach
    void setUp() { controller = new SysDictController(sysDictService); }

    @Test void testGetDictById() {
        SysDictDTO dto = new SysDictDTO(); when(sysDictService.getDictById(1L)).thenReturn(dto);
        assertSame(dto, controller.getDictById(1L)); verify(sysDictService).getDictById(1L);
    }
    @Test void testGetDictPageByParams() {
        SysDictPageParams p = new SysDictPageParams(); PageInfo<SysDictDTO> page = new PageInfo<>();
        when(sysDictService.getDictPageByParams(p)).thenReturn(page);
        assertSame(page, controller.getDictPageByParams(p)); verify(sysDictService).getDictPageByParams(p);
    }
    @Test void testCreateDict() { SysDictDTO dto = new SysDictDTO(); controller.createDict(dto); verify(sysDictService).createDict(dto); }
    @Test void testUpdateDict() { SysDictDTO dto = new SysDictDTO(); controller.updateDict(dto); verify(sysDictService).updateDict(dto); }
    @Test void testDeleteDictById() { controller.deleteDictById(1L); verify(sysDictService).deleteDictById(1L); }
    @Test void testGetDetailListByDictId() {
        List<SysDictDetailDTO> list = List.of(new SysDictDetailDTO());
        when(sysDictService.getDetailListByDictId(1L)).thenReturn(list); assertSame(list, controller.getDetailListByDictId(1L));
    }
    @Test void testCreateDetail() { SysDictDetailDTO dto = new SysDictDetailDTO(); controller.createDetail(dto); verify(sysDictService).createDetail(dto); }
    @Test void testUpdateDetail() { SysDictDetailDTO dto = new SysDictDetailDTO(); controller.updateDetail(dto); verify(sysDictService).updateDetail(dto); }
    @Test void testDeleteDetailById() { controller.deleteDetailById(1L); verify(sysDictService).deleteDetailById(1L); }
}

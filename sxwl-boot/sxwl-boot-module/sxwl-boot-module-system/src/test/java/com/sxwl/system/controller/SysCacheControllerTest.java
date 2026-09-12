/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.controller;

import com.sxwl.system.model.dto.SysCacheCategoryDTO;
import com.sxwl.system.model.dto.SysCacheKeyDetailDTO;
import com.sxwl.system.service.SysCacheService;
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
@DisplayName("SysCacheController 测试")
class SysCacheControllerTest {
    private SysCacheController controller;
    @Mock private SysCacheService sysCacheService;

    @BeforeEach
    void setUp() { controller = new SysCacheController(sysCacheService); }

    @Test void testListCategories() {
        List<SysCacheCategoryDTO> list = List.of(new SysCacheCategoryDTO());
        when(sysCacheService.listCategories()).thenReturn(list); assertSame(list, controller.listCategories());
    }
    @Test void testListKeys() {
        List<SysCacheKeyDetailDTO> list = List.of(new SysCacheKeyDetailDTO());
        when(sysCacheService.listKeys("dict:*")).thenReturn(list); assertSame(list, controller.listKeys("dict:*"));
    }
    @Test void testGetKeyDetail() {
        SysCacheKeyDetailDTO dto = new SysCacheKeyDetailDTO();
        when(sysCacheService.getKeyDetail("key")).thenReturn(dto); assertSame(dto, controller.getKeyDetail("key"));
    }
    @Test void testClearByName() { controller.clearByName("dict:*"); verify(sysCacheService).clearByName("dict:*"); }
    @Test void testClearByKey() { controller.clearByKey("key"); verify(sysCacheService).clearByKey("key"); }
}

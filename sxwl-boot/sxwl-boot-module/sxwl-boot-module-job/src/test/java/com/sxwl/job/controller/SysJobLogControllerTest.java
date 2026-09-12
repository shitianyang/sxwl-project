/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.sxwl.job.controller;

import com.github.pagehelper.PageInfo;
import com.sxwl.job.model.dto.SysJobLogDTO;
import com.sxwl.job.model.params.SysJobLogPageParams;
import com.sxwl.job.service.SysJobLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

/**
 * SysJobLogController 单元测试
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SysJobLogController 测试")
class SysJobLogControllerTest {

    @Mock
    private SysJobLogService sysJobLogService;

    private SysJobLogController sysJobLogController;

    @BeforeEach
    void setUp() {
        sysJobLogController = new SysJobLogController(sysJobLogService);
    }

    @Test
    @DisplayName("getLogById 应委托给 Service")
    void getLogById_shouldDelegate() {
        SysJobLogDTO dto = new SysJobLogDTO();
        when(sysJobLogService.getLogById(1L)).thenReturn(dto);

        SysJobLogDTO result = sysJobLogController.getLogById(1L);

        assertSame(dto, result);
        verify(sysJobLogService).getLogById(1L);
    }

    @Test
    @DisplayName("getLogPageByParams 应委托给 Service")
    void getLogPageByParams_shouldDelegate() {
        SysJobLogPageParams params = new SysJobLogPageParams();
        PageInfo<SysJobLogDTO> page = new PageInfo<>();
        when(sysJobLogService.getLogPageByParams(params)).thenReturn(page);

        PageInfo<SysJobLogDTO> result = sysJobLogController.getLogPageByParams(params);

        assertSame(page, result);
        verify(sysJobLogService).getLogPageByParams(params);
    }

    @Test
    @DisplayName("deleteLogById 应委托给 Service")
    void deleteLogById_shouldDelegate() {
        sysJobLogController.deleteLogById(1L);

        verify(sysJobLogService).deleteLogById(1L);
    }

    @Test
    @DisplayName("cleanLogBefore 应委托给 Service")
    void cleanLogBefore_shouldDelegate() {
        sysJobLogController.cleanLogBefore(30);

        verify(sysJobLogService).cleanLogBefore(30);
    }
}

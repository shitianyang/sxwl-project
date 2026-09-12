/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.sxwl.job.controller;

import com.github.pagehelper.PageInfo;
import com.sxwl.job.model.dto.SysJobDTO;
import com.sxwl.job.model.params.SysJobPageParams;
import com.sxwl.job.service.SysJobInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

/**
 * SysJobController 单元测试
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SysJobController 测试")
class SysJobControllerTest {

    @Mock
    private SysJobInfoService sysJobInfoService;

    private SysJobController sysJobController;

    @BeforeEach
    void setUp() {
        sysJobController = new SysJobController(sysJobInfoService);
    }

    @Test
    @DisplayName("getJobById 应委托给 Service")
    void getJobById_shouldDelegate() {
        SysJobDTO dto = new SysJobDTO();
        when(sysJobInfoService.getJobById(1L)).thenReturn(dto);

        SysJobDTO result = sysJobController.getJobById(1L);

        assertSame(dto, result);
        verify(sysJobInfoService).getJobById(1L);
    }

    @Test
    @DisplayName("getJobPageByParams 应委托给 Service")
    void getJobPageByParams_shouldDelegate() {
        SysJobPageParams params = new SysJobPageParams();
        PageInfo<SysJobDTO> page = new PageInfo<>();
        when(sysJobInfoService.getJobPageByParams(params)).thenReturn(page);

        PageInfo<SysJobDTO> result = sysJobController.getJobPageByParams(params);

        assertSame(page, result);
        verify(sysJobInfoService).getJobPageByParams(params);
    }

    @Test
    @DisplayName("createJob 应委托给 Service")
    void createJob_shouldDelegate() {
        SysJobDTO dto = new SysJobDTO();

        sysJobController.createJob(dto);

        verify(sysJobInfoService).createJob(dto);
    }

    @Test
    @DisplayName("updateJob 应委托给 Service")
    void updateJob_shouldDelegate() {
        SysJobDTO dto = new SysJobDTO();

        sysJobController.updateJob(dto);

        verify(sysJobInfoService).updateJob(dto);
    }

    @Test
    @DisplayName("deleteJobById 应委托给 Service")
    void deleteJobById_shouldDelegate() {
        sysJobController.deleteJobById(1L);

        verify(sysJobInfoService).deleteJobById(1L);
    }

    @Test
    @DisplayName("pauseJob 应委托给 Service")
    void pauseJob_shouldDelegate() {
        sysJobController.pauseJob(1L);

        verify(sysJobInfoService).pauseJob(1L);
    }

    @Test
    @DisplayName("resumeJob 应委托给 Service")
    void resumeJob_shouldDelegate() {
        sysJobController.resumeJob(1L);

        verify(sysJobInfoService).resumeJob(1L);
    }

    @Test
    @DisplayName("runOnce 应委托给 Service")
    void runOnce_shouldDelegate() {
        sysJobController.runOnce(1L);

        verify(sysJobInfoService).runOnce(1L);
    }
}

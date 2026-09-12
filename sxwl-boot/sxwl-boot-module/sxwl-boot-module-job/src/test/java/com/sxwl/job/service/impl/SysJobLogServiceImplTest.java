/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.sxwl.job.service.impl;

import com.github.pagehelper.PageInfo;
import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.job.mapper.SysJobLogMapper;
import com.sxwl.job.model.dto.SysJobLogDTO;
import com.sxwl.job.model.params.SysJobLogPageParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * SysJobLogServiceImpl 单元测试
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SysJobLogServiceImpl 测试")
class SysJobLogServiceImplTest {

    @Mock
    private SysJobLogMapper sysJobLogMapper;

    private SysJobLogServiceImpl sysJobLogService;

    @BeforeEach
    void setUp() {
        sysJobLogService = new SysJobLogServiceImpl(sysJobLogMapper);
    }

    @Test
    @DisplayName("getLogById 应返回日志 DTO")
    void getLogById_shouldReturnDto() {
        SysJobLogDTO dto = new SysJobLogDTO();
        dto.setId(1L);
        dto.setJobName("testJob");

        when(sysJobLogMapper.getLogById(1L)).thenReturn(dto);

        SysJobLogDTO result = sysJobLogService.getLogById(1L);

        assertSame(dto, result);
        assertEquals("testJob", result.getJobName());
        verify(sysJobLogMapper).getLogById(1L);
    }

    @Test
    @DisplayName("getLogById 不存在时应抛出异常")
    void getLogById_notFound_shouldThrow() {
        when(sysJobLogMapper.getLogById(1L)).thenReturn(null);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> sysJobLogService.getLogById(1L));
        assertEquals(10004, ex.getCode());
        verify(sysJobLogMapper).getLogById(1L);
    }

    @Test
    @DisplayName("getLogPageByParams 应返回分页结果")
    void getLogPageByParams_shouldReturnPage() {
        SysJobLogDTO dto = new SysJobLogDTO();
        dto.setId(1L);
        List<SysJobLogDTO> rows = List.of(dto);
        SysJobLogPageParams params = new SysJobLogPageParams();

        when(sysJobLogMapper.getLogPageByParams(params)).thenReturn(rows);

        PageInfo<SysJobLogDTO> result = sysJobLogService.getLogPageByParams(params);

        assertEquals(1, result.getList().size());
        assertSame(dto, result.getList().get(0));
        verify(sysJobLogMapper).getLogPageByParams(params);
    }

    @Test
    @DisplayName("getLogPageByParams 无数据应返回空分页")
    void getLogPageByParams_empty_shouldReturnEmptyPage() {
        SysJobLogPageParams params = new SysJobLogPageParams();

        when(sysJobLogMapper.getLogPageByParams(params)).thenReturn(Collections.emptyList());

        PageInfo<SysJobLogDTO> result = sysJobLogService.getLogPageByParams(params);

        assertTrue(result.getList().isEmpty());
        verify(sysJobLogMapper).getLogPageByParams(params);
    }

    @Test
    @DisplayName("deleteLogById 应删除成功")
    void deleteLogById_shouldSucceed() {
        when(sysJobLogMapper.deleteLogById(1L)).thenReturn(1);

        int result = sysJobLogService.deleteLogById(1L);

        assertEquals(1, result);
        verify(sysJobLogMapper).deleteLogById(1L);
    }

    @Test
    @DisplayName("deleteLogById 不存在时应抛出异常")
    void deleteLogById_notFound_shouldThrow() {
        when(sysJobLogMapper.deleteLogById(1L)).thenReturn(0);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> sysJobLogService.deleteLogById(1L));
        assertEquals(10004, ex.getCode());
        verify(sysJobLogMapper).deleteLogById(1L);
    }

    @Test
    @DisplayName("cleanLogBefore 应清理并返回影响条数")
    void cleanLogBefore_shouldClean() {
        when(sysJobLogMapper.cleanLogBefore(30)).thenReturn(100);

        int result = sysJobLogService.cleanLogBefore(30);

        assertEquals(100, result);
        verify(sysJobLogMapper).cleanLogBefore(30);
    }
}

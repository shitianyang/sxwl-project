/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 *
 * This source code is licensed under the license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.sxwl.job.service.impl;

import com.github.pagehelper.PageInfo;
import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.common.utils.SxwlDiffUtils;
import com.sxwl.job.mapper.SysJobInfoMapper;
import com.sxwl.job.model.dto.SysJobDTO;
import com.sxwl.job.model.entity.SysJobInfo;
import com.sxwl.job.model.params.SysJobPageParams;
import com.sxwl.quartz.manager.SysJobManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.SchedulerException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SysJobInfoServiceImpl 单元测试
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SysJobInfoServiceImpl 测试")
class SysJobInfoServiceImplTest {

    @Mock
    private SysJobInfoMapper sysJobInfoMapper;

    @Mock
    private SysJobManager sysJobManager;

    private SysJobInfoServiceImpl sysJobInfoService;

    @BeforeEach
    void setUp() {
        sysJobInfoService = new SysJobInfoServiceImpl(sysJobInfoMapper, sysJobManager);
    }

    // ==================== getJobById ====================

    @Test
    @DisplayName("getJobById 应返回任务 DTO")
    void getJobById_shouldReturnDto() {
        SysJobDTO dto = new SysJobDTO();
        dto.setId(1L);
        dto.setJobName("testJob");

        when(sysJobInfoMapper.getJobById(1L)).thenReturn(dto);

        SysJobDTO result = sysJobInfoService.getJobById(1L);

        assertSame(dto, result);
        assertEquals("testJob", result.getJobName());
        verify(sysJobInfoMapper).getJobById(1L);
    }

    @Test
    @DisplayName("getJobById 不存在时应抛出异常")
    void getJobById_notFound_shouldThrow() {
        when(sysJobInfoMapper.getJobById(1L)).thenReturn(null);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> sysJobInfoService.getJobById(1L));
        assertEquals(10004, ex.getCode());
        verify(sysJobInfoMapper).getJobById(1L);
    }

    // ==================== getJobPageByParams ====================

    @Test
    @DisplayName("getJobPageByParams 应返回分页结果")
    void getJobPageByParams_shouldReturnPage() {
        SysJobDTO dto = new SysJobDTO();
        dto.setId(1L);
        List<SysJobDTO> rows = List.of(dto);
        SysJobPageParams params = new SysJobPageParams();

        when(sysJobInfoMapper.getJobPageByParams(params)).thenReturn(rows);

        PageInfo<SysJobDTO> result = sysJobInfoService.getJobPageByParams(params);

        assertEquals(1, result.getList().size());
        assertSame(dto, result.getList().get(0));
        verify(sysJobInfoMapper).getJobPageByParams(params);
    }

    @Test
    @DisplayName("getJobPageByParams 无数据应返回空分页")
    void getJobPageByParams_empty_shouldReturnEmptyPage() {
        SysJobPageParams params = new SysJobPageParams();

        when(sysJobInfoMapper.getJobPageByParams(params)).thenReturn(Collections.emptyList());

        PageInfo<SysJobDTO> result = sysJobInfoService.getJobPageByParams(params);

        assertTrue(result.getList().isEmpty());
        verify(sysJobInfoMapper).getJobPageByParams(params);
    }

    // ==================== createJob ====================

    @Test
    @DisplayName("createJob 名称分组重复时应抛出异常")
    void createJob_duplicate_shouldThrow() {
        SysJobDTO dto = new SysJobDTO();
        dto.setJobName("testJob");
        dto.setJobGroup("DEFAULT");

        when(sysJobInfoMapper.checkJobUnique("testJob", "DEFAULT", null)).thenReturn(1);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> sysJobInfoService.createJob(dto));
        assertEquals(10002, ex.getCode());
        verify(sysJobInfoMapper).checkJobUnique("testJob", "DEFAULT", null);
    }

    @Test
    @DisplayName("createJob 状态正常时应同步 Quartz")
    void createJob_status1_shouldSyncQuartz() throws SchedulerException {
        SysJobDTO dto = new SysJobDTO();
        dto.setJobName("testJob");
        dto.setJobGroup("DEFAULT");
        dto.setClassName("com.sxwl.TestService");
        dto.setMethodName("execute");
        dto.setCronExpression("0 0/5 * * * ?");
        dto.setStatus(1);

        when(sysJobInfoMapper.checkJobUnique("testJob", "DEFAULT", null)).thenReturn(0);
        when(sysJobInfoMapper.insertJob(any(SysJobInfo.class))).thenReturn(1);

        int result = sysJobInfoService.createJob(dto);

        assertEquals(1, result);
        verify(sysJobManager).createJob("testJob", "DEFAULT",
                "com.sxwl.TestService", "execute",
                "0 0/5 * * * ?", null);
    }

    @Test
    @DisplayName("createJob 状态暂停时应不同步 Quartz")
    void createJob_status0_shouldNotSyncQuartz() throws SchedulerException {
        SysJobDTO dto = new SysJobDTO();
        dto.setJobName("testJob");
        dto.setJobGroup("DEFAULT");
        dto.setClassName("com.sxwl.TestService");
        dto.setMethodName("execute");
        dto.setCronExpression("0 0/5 * * * ?");
        dto.setStatus(0);

        when(sysJobInfoMapper.checkJobUnique("testJob", "DEFAULT", null)).thenReturn(0);
        when(sysJobInfoMapper.insertJob(any(SysJobInfo.class))).thenReturn(1);

        int result = sysJobInfoService.createJob(dto);

        assertEquals(1, result);
        verify(sysJobManager, never()).createJob(anyString(), anyString(), anyString(), anyString(), anyString(), any());
    }

    @Test
    @DisplayName("createJob 插入失败时应抛出异常")
    void createJob_insertFailed_shouldThrow() throws SchedulerException {
        SysJobDTO dto = new SysJobDTO();
        dto.setJobName("testJob");
        dto.setJobGroup("DEFAULT");
        dto.setClassName("com.sxwl.TestService");
        dto.setMethodName("execute");
        dto.setCronExpression("0 0/5 * * * ?");
        dto.setStatus(0);

        when(sysJobInfoMapper.checkJobUnique("testJob", "DEFAULT", null)).thenReturn(0);
        when(sysJobInfoMapper.insertJob(any(SysJobInfo.class))).thenReturn(0);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> sysJobInfoService.createJob(dto));
        assertEquals(10001, ex.getCode());
    }

    // ==================== updateJob ====================

    @Test
    @DisplayName("updateJob 名称分组重复时应抛出异常")
    void updateJob_duplicate_shouldThrow() {
        SysJobDTO dto = new SysJobDTO();
        dto.setId(1L);
        dto.setJobName("testJob");
        dto.setJobGroup("DEFAULT");

        when(sysJobInfoMapper.checkJobUnique("testJob", "DEFAULT", 1L)).thenReturn(1);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> sysJobInfoService.updateJob(dto));
        assertEquals(10002, ex.getCode());
        verify(sysJobInfoMapper).checkJobUnique("testJob", "DEFAULT", 1L);
    }

    @Test
    @DisplayName("updateJob 应更新并计算字段差异，且同步 Quartz")
    void updateJob_shouldUpdateAndDiff() throws SchedulerException {
        try (MockedStatic<SxwlDiffUtils> diffUtils = mockStatic(SxwlDiffUtils.class)) {
            SysJobDTO dto = new SysJobDTO();
            dto.setId(1L);
            dto.setJobName("testJob");
            dto.setJobGroup("DEFAULT");
            dto.setClassName("com.sxwl.TestService");
            dto.setMethodName("execute");
            dto.setCronExpression("0 0/5 * * * ?");
            dto.setMethodParams("param1");
            dto.setDescription("测试任务");
            dto.setStatus(1);

            SysJobDTO oldDto = new SysJobDTO();
            oldDto.setJobName("testJob");
            oldDto.setJobGroup("DEFAULT");
            oldDto.setClassName("com.sxwl.OldService");
            oldDto.setMethodName("oldMethod");
            oldDto.setCronExpression("0 0 * * * ?");
            oldDto.setStatus(0);

            when(sysJobInfoMapper.checkJobUnique("testJob", "DEFAULT", 1L)).thenReturn(0);
            when(sysJobInfoMapper.getJobById(1L)).thenReturn(oldDto);
            when(sysJobInfoMapper.updateJob(any(SysJobInfo.class))).thenReturn(1);
            diffUtils.when(() -> SxwlDiffUtils.diff(any(SysJobInfo.class), any(SysJobInfo.class))).thenReturn("diff-json");

            sysJobInfoService.updateJob(dto);

            verify(sysJobInfoMapper).updateJob(any(SysJobInfo.class));
            diffUtils.verify(() -> SxwlDiffUtils.setContextDiff("diff-json"));
            verify(sysJobManager).deleteJob("testJob", "DEFAULT");
            verify(sysJobManager).createJob("testJob", "DEFAULT",
                    "com.sxwl.TestService", "execute",
                    "0 0/5 * * * ?", "param1");
        }
    }

    @Test
    @DisplayName("updateJob 状态暂停时应只删除不创建 Quartz 任务")
    void updateJob_status0_shouldDeleteOnly() throws SchedulerException {
        try (MockedStatic<SxwlDiffUtils> diffUtils = mockStatic(SxwlDiffUtils.class)) {
            SysJobDTO dto = new SysJobDTO();
            dto.setId(1L);
            dto.setJobName("testJob");
            dto.setJobGroup("DEFAULT");
            dto.setClassName("com.sxwl.TestService");
            dto.setMethodName("execute");
            dto.setCronExpression("0 0/5 * * * ?");
            dto.setStatus(0);

            SysJobDTO oldDto = new SysJobDTO();
            oldDto.setJobName("testJob");
            oldDto.setJobGroup("DEFAULT");

            when(sysJobInfoMapper.checkJobUnique("testJob", "DEFAULT", 1L)).thenReturn(0);
            when(sysJobInfoMapper.getJobById(1L)).thenReturn(oldDto);
            when(sysJobInfoMapper.updateJob(any(SysJobInfo.class))).thenReturn(1);
            // diff returns null by default → setContextDiff not called

            sysJobInfoService.updateJob(dto);

            verify(sysJobManager).deleteJob("testJob", "DEFAULT");
            verify(sysJobManager, never()).createJob(anyString(), anyString(), anyString(), anyString(), anyString(), any());
        }
    }

    @Test
    @DisplayName("updateJob 更新失败时应抛出异常")
    void updateJob_notFound_shouldThrow() {
        SysJobDTO dto = new SysJobDTO();
        dto.setId(1L);
        dto.setJobName("testJob");
        dto.setJobGroup("DEFAULT");

        when(sysJobInfoMapper.checkJobUnique("testJob", "DEFAULT", 1L)).thenReturn(0);
        when(sysJobInfoMapper.updateJob(any(SysJobInfo.class))).thenReturn(0);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> sysJobInfoService.updateJob(dto));
        assertEquals(10004, ex.getCode());
        verifyNoInteractions(sysJobManager);
    }

    // ==================== deleteJobById ====================

    @Test
    @DisplayName("deleteJobById 应删除并同步删除 Quartz 任务")
    void deleteJobById_shouldDelete() throws SchedulerException {
        SysJobDTO dto = new SysJobDTO();
        dto.setId(1L);
        dto.setJobName("testJob");
        dto.setJobGroup("DEFAULT");

        when(sysJobInfoMapper.getJobById(1L)).thenReturn(dto);
        when(sysJobInfoMapper.deleteJobById(1L)).thenReturn(1);

        int result = sysJobInfoService.deleteJobById(1L);

        assertEquals(1, result);
        verify(sysJobInfoMapper).deleteJobById(1L);
        verify(sysJobManager).deleteJob("testJob", "DEFAULT");
    }

    @Test
    @DisplayName("deleteJobById 任务不存在时应抛出异常")
    void deleteJobById_notFound_shouldThrow() {
        when(sysJobInfoMapper.getJobById(1L)).thenReturn(null);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> sysJobInfoService.deleteJobById(1L));
        assertEquals(10004, ex.getCode());
    }

    // ==================== pauseJob ====================

    @Test
    @DisplayName("pauseJob 应暂停成功")
    void pauseJob_shouldSucceed() throws SchedulerException {
        SysJobDTO dto = new SysJobDTO();
        dto.setJobName("testJob");
        dto.setJobGroup("DEFAULT");

        when(sysJobInfoMapper.getJobById(1L)).thenReturn(dto);

        sysJobInfoService.pauseJob(1L);

        verify(sysJobManager).pauseJob("testJob", "DEFAULT");
    }

    @Test
    @DisplayName("pauseJob 暂停失败时应抛出异常")
    void pauseJob_failed_shouldThrow() throws SchedulerException {
        SysJobDTO dto = new SysJobDTO();
        dto.setJobName("testJob");
        dto.setJobGroup("DEFAULT");

        when(sysJobInfoMapper.getJobById(1L)).thenReturn(dto);
        doThrow(new SchedulerException("Quartz error")).when(sysJobManager).pauseJob("testJob", "DEFAULT");

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> sysJobInfoService.pauseJob(1L));
        assertEquals(10001, ex.getCode());
    }

    // ==================== resumeJob ====================

    @Test
    @DisplayName("resumeJob 应恢复成功")
    void resumeJob_shouldSucceed() throws SchedulerException {
        SysJobDTO dto = new SysJobDTO();
        dto.setJobName("testJob");
        dto.setJobGroup("DEFAULT");

        when(sysJobInfoMapper.getJobById(1L)).thenReturn(dto);

        sysJobInfoService.resumeJob(1L);

        verify(sysJobManager).resumeJob("testJob", "DEFAULT");
    }

    @Test
    @DisplayName("resumeJob 恢复失败时应抛出异常")
    void resumeJob_failed_shouldThrow() throws SchedulerException {
        SysJobDTO dto = new SysJobDTO();
        dto.setJobName("testJob");
        dto.setJobGroup("DEFAULT");

        when(sysJobInfoMapper.getJobById(1L)).thenReturn(dto);
        doThrow(new SchedulerException("Quartz error")).when(sysJobManager).resumeJob("testJob", "DEFAULT");

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> sysJobInfoService.resumeJob(1L));
        assertEquals(10001, ex.getCode());
    }

    // ==================== runOnce ====================

    @Test
    @DisplayName("runOnce 应执行成功")
    void runOnce_shouldSucceed() throws SchedulerException {
        SysJobDTO dto = new SysJobDTO();
        dto.setJobName("testJob");
        dto.setJobGroup("DEFAULT");

        when(sysJobInfoMapper.getJobById(1L)).thenReturn(dto);

        sysJobInfoService.runOnce(1L);

        verify(sysJobManager).runOnce("testJob", "DEFAULT");
    }

    @Test
    @DisplayName("runOnce 执行失败时应抛出异常")
    void runOnce_failed_shouldThrow() throws SchedulerException {
        SysJobDTO dto = new SysJobDTO();
        dto.setJobName("testJob");
        dto.setJobGroup("DEFAULT");

        when(sysJobInfoMapper.getJobById(1L)).thenReturn(dto);
        doThrow(new SchedulerException("Quartz error")).when(sysJobManager).runOnce("testJob", "DEFAULT");

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> sysJobInfoService.runOnce(1L));
        assertEquals(10001, ex.getCode());
    }

    // ==================== syncActiveJobsToQuartz ====================

    @Test
    @DisplayName("syncActiveJobsToQuartz 无活动任务应直接返回")
    void syncActiveJobsToQuartz_empty_shouldReturn() {
        when(sysJobInfoMapper.getAllActiveJobs()).thenReturn(Collections.emptyList());

        sysJobInfoService.syncActiveJobsToQuartz();

        verify(sysJobInfoMapper).getAllActiveJobs();
        verifyNoInteractions(sysJobManager);
    }

    @Test
    @DisplayName("syncActiveJobsToQuartz 应同步所有活动任务")
    void syncActiveJobsToQuartz_shouldSyncAll() throws SchedulerException {
        SysJobDTO job1 = new SysJobDTO();
        job1.setJobName("job1");
        job1.setJobGroup("DEFAULT");
        job1.setClassName("com.sxwl.Service1");
        job1.setMethodName("execute1");
        job1.setCronExpression("0 0/5 * * * ?");

        SysJobDTO job2 = new SysJobDTO();
        job2.setJobName("job2");
        job2.setJobGroup("SYSTEM");
        job2.setClassName("com.sxwl.Service2");
        job2.setMethodName("execute2");
        job2.setCronExpression("0 0 * * * ?");

        when(sysJobInfoMapper.getAllActiveJobs()).thenReturn(List.of(job1, job2));

        sysJobInfoService.syncActiveJobsToQuartz();

        verify(sysJobManager).createJob("job1", "DEFAULT",
                "com.sxwl.Service1", "execute1",
                "0 0/5 * * * ?", null);
        verify(sysJobManager).createJob("job2", "SYSTEM",
                "com.sxwl.Service2", "execute2",
                "0 0 * * * ?", null);
    }

    @Test
    @DisplayName("syncActiveJobsToQuartz 部分失败不应中断其余任务")
    void syncActiveJobsToQuartz_partialFail_shouldContinue() throws SchedulerException {
        SysJobDTO job1 = new SysJobDTO();
        job1.setJobName("job1");
        job1.setJobGroup("DEFAULT");
        job1.setClassName("com.sxwl.Service1");
        job1.setMethodName("execute1");
        job1.setCronExpression("0 0/5 * * * ?");

        SysJobDTO job2 = new SysJobDTO();
        job2.setJobName("job2");
        job2.setJobGroup("SYSTEM");
        job2.setClassName("com.sxwl.Service2");
        job2.setMethodName("execute2");
        job2.setCronExpression("0 0 * * * ?");

        when(sysJobInfoMapper.getAllActiveJobs()).thenReturn(List.of(job1, job2));
        doThrow(new SchedulerException("Fail")).when(sysJobManager)
                .createJob(eq("job1"), anyString(), anyString(), anyString(), anyString(), any());

        sysJobInfoService.syncActiveJobsToQuartz();

        verify(sysJobManager).createJob("job1", "DEFAULT",
                "com.sxwl.Service1", "execute1",
                "0 0/5 * * * ?", null);
        verify(sysJobManager).createJob("job2", "SYSTEM",
                "com.sxwl.Service2", "execute2",
                "0 0 * * * ?", null);
    }
}

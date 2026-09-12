package com.sxwl.quartz.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * {@link SysJobManager} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SysJobManager 测试")
class SysJobManagerTest {

    @Mock
    private Scheduler scheduler;

    private SysJobManager manager;

    @BeforeEach
    void setUp() {
        manager = new SysJobManager(scheduler);
    }

    @Test
    @DisplayName("createJob 应创建定时任务")
    void createJob_shouldCreateJob() throws SchedulerException {
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);
        manager.createJob("testJob", "DEFAULT", "com.sxwl.TestService", "execute", "0/5 * * * * ?", null);
        verify(scheduler).scheduleJob(any(JobDetail.class), any(CronTrigger.class));
    }

    @Test
    @DisplayName("createJob 任务已存在时应跳过")
    void createJob_shouldSkip_whenExists() throws SchedulerException {
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(true);
        manager.createJob("testJob", "DEFAULT", "com.sxwl.TestService", "execute", "0/5 * * * * ?", null);
        verify(scheduler, never()).scheduleJob(any(JobDetail.class), any(CronTrigger.class));
    }

    @Test
    @DisplayName("updateCron 应更新 Cron 表达式")
    void updateCron_shouldUpdate() throws SchedulerException {
        manager.updateCron("testJob", "DEFAULT", "0/10 * * * * ?");
        verify(scheduler).rescheduleJob(any(TriggerKey.class), any(CronTrigger.class));
    }

    @Test
    @DisplayName("deleteJob 应删除任务")
    void deleteJob_shouldDelete() throws SchedulerException {
        manager.deleteJob("testJob", "DEFAULT");
        verify(scheduler).deleteJob(JobKey.jobKey("testJob", "DEFAULT"));
    }

    @Test
    @DisplayName("pauseJob 应暂停任务")
    void pauseJob_shouldPause() throws SchedulerException {
        manager.pauseJob("testJob", "DEFAULT");
        verify(scheduler).pauseJob(JobKey.jobKey("testJob", "DEFAULT"));
    }

    @Test
    @DisplayName("resumeJob 应恢复任务")
    void resumeJob_shouldResume() throws SchedulerException {
        manager.resumeJob("testJob", "DEFAULT");
        verify(scheduler).resumeJob(JobKey.jobKey("testJob", "DEFAULT"));
    }

    @Test
    @DisplayName("runOnce 应立即触发任务")
    void runOnce_shouldTrigger() throws SchedulerException {
        manager.runOnce("testJob", "DEFAULT");
        verify(scheduler).triggerJob(JobKey.jobKey("testJob", "DEFAULT"));
    }

    @Test
    @DisplayName("checkExists 应返回 true 当任务存在")
    void checkExists_shouldReturnTrue_whenExists() throws SchedulerException {
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(true);
        assertTrue(manager.checkExists("testJob", "DEFAULT"));
    }

    @Test
    @DisplayName("getScheduler 应返回注入的 Scheduler")
    void getScheduler_shouldReturnScheduler() {
        assertEquals(scheduler, manager.getScheduler());
    }
}

package com.sxwl.quartz.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.quartz.Scheduler;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * {@link SxwlQuartzAutoConfiguration} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("SxwlQuartzAutoConfiguration 测试")
class SxwlQuartzAutoConfigurationTest {

    @Test
    @DisplayName("schedulerFactoryBean 应创建 SchedulerFactoryBean")
    void schedulerFactoryBean_shouldCreateFactory() {
        org.springframework.context.ApplicationContext ctx = mock(org.springframework.context.ApplicationContext.class);
        SxwlQuartzAutoConfiguration autoConfig = new SxwlQuartzAutoConfiguration(ctx);

        DataSource dataSource = mock(DataSource.class);
        var factory = autoConfig.schedulerFactoryBean(dataSource);
        assertNotNull(factory);
    }

    @Test
    @DisplayName("sysJobManager 应创建管理器")
    void sysJobManager_shouldCreateManager() {
        org.springframework.context.ApplicationContext ctx = mock(org.springframework.context.ApplicationContext.class);
        SxwlQuartzAutoConfiguration autoConfig = new SxwlQuartzAutoConfiguration(ctx);

        Scheduler scheduler = mock(Scheduler.class);
        var manager = autoConfig.sysJobManager(scheduler);
        assertNotNull(manager);
    }
}

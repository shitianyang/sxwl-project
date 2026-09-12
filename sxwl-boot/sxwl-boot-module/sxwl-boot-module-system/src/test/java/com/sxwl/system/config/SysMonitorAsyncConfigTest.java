/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SysMonitorAsyncConfig 测试")
class SysMonitorAsyncConfigTest {
    private final SysMonitorAsyncConfig config = new SysMonitorAsyncConfig();

    @Test
    void testMonitorAsyncExecutor() {
        Executor executor = config.monitorAsyncExecutor();
        assertNotNull(executor);
        assertInstanceOf(ThreadPoolTaskExecutor.class, executor);
        ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) executor;
        assertEquals(1, taskExecutor.getCorePoolSize());
        assertEquals(2, taskExecutor.getMaxPoolSize());
        assertTrue(taskExecutor.getThreadNamePrefix().contains("monitor-async-"));
    }
}

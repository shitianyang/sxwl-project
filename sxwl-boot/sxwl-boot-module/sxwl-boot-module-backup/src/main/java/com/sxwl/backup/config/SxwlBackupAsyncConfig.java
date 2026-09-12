package com.sxwl.backup.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 备份异步执行器配置。
 *
 * <p>将 {@code @Async} 备份任务绑定到独立的有界线程池，避免默认
 * {@code SimpleAsyncTaskExecutor} 为每个任务新建线程（无界）拖垮应用（L23）。</p>
 *
 * @author shitianyang
 * @since 0.1.0
 */
@Configuration
public class SxwlBackupAsyncConfig {

    @Bean("backupExecutor")
    public Executor backupExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(8);
        executor.setThreadNamePrefix("backup-");
        // 队列满时由调用线程执行，避免任务被静默丢弃
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}

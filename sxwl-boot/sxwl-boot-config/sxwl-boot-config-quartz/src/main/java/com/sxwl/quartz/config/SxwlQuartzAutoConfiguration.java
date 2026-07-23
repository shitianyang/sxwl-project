package com.sxwl.quartz.config;

import com.sxwl.quartz.factory.SysJobFactory;
import com.sxwl.quartz.manager.SysJobManager;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Quartz 自动配置
 *
 * <p>配置 SchedulerFactoryBean（支持 JDBC 存储）并注册 SysJobManager 供业务层使用。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@AutoConfiguration
@ConditionalOnClass(Scheduler.class)
public class SxwlQuartzAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(SxwlQuartzAutoConfiguration.class);

    private final ApplicationContext applicationContext;

    public SxwlQuartzAutoConfiguration(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    @ConditionalOnMissingBean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setOverwriteExistingJobs(true);

        // === 自定义属性 ===
        Properties props = new Properties();
        props.setProperty("org.quartz.scheduler.instanceName", "SxwlScheduler");
        props.setProperty("org.quartz.jobStore.class", "org.springframework.scheduling.quartz.LocalDataSourceJobStore");
        props.setProperty("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate");
        props.setProperty("org.quartz.jobStore.tablePrefix", "QRTZ_");
        props.setProperty("org.quartz.threadPool.threadCount", "10");
        factory.setQuartzProperties(props);

        // === 自定义 JobFactory，支持 Spring Bean 注入 ===
        factory.setJobFactory(new SysJobFactory(applicationContext));

        log.info("SxwlQuartzAutoConfiguration: SchedulerFactoryBean 初始化完成, threadCount=10");
        return factory;
    }

    @Bean
    @ConditionalOnMissingBean
    public SysJobManager sysJobManager(Scheduler scheduler) {
        return new SysJobManager(scheduler);
    }
}

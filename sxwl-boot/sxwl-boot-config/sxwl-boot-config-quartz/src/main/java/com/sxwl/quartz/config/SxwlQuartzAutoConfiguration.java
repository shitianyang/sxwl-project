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
 * Quartz 自动配置类
 *
 * <p>配置 {@code SchedulerFactoryBean}（支持 PostgreSQL JDBC 存储）并注册 {@code SysJobManager}
 * 供业务模块（module-job）调用。所有 Quartz 属性均已预设合理默认值，无需额外配置。</p>
 *
 * <h3>预设配置项</h3>
 * <ul>
 *   <li>调度器实例名称: SxwlScheduler</li>
 *   <li>任务存储: LocalDataSourceJobStore（PostgreSQLDelegate）</li>
 *   <li>线程池大小: 10</li>
 *   <li>集群模式: 已启用（支持多实例部署）</li>
 * </ul>
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

    /**
     * 配置 SchedulerFactoryBean（支持 PostgreSQL JDBC 存储）
     *
     * <p>所有属性均预设合理默认值，覆盖 application.yaml 中的 sxwl.quartz.* 即可个性化定制。</p>
     */
    @Bean
    @ConditionalOnMissingBean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setOverwriteExistingJobs(true);

        // === 自定义属性 ===
        Properties props = new Properties();
        
        // Scheduler 配置
        props.setProperty("org.quartz.scheduler.instanceName", "SxwlScheduler");
        props.setProperty("org.quartz.scheduler.instanceId", "AUTO");
        
        // JobStore 配置（PostgreSQL JDBC 存储）
        props.setProperty("org.quartz.jobStore.class", 
                "org.springframework.scheduling.quartz.LocalDataSourceJobStore");
        props.setProperty("org.quartz.jobStore.driverDelegateClass", 
                "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate");
        props.setProperty("org.quartz.jobStore.tablePrefix", "QRTZ_");
        props.setProperty("org.quartz.jobStore.isClustered", "true");
        props.setProperty("org.quartz.jobStore.clusterCheckinInterval", "10000");
        props.setProperty("org.quartz.jobStore.misfireThreshold", "60000");
        
        // ThreadPool 配置
        props.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        props.setProperty("org.quartz.threadPool.threadCount", "10");
        props.setProperty("org.quartz.threadPool.threadPriority", "5");
        props.setProperty("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");
        
        factory.setQuartzProperties(props);

        // 自定义 JobFactory，支持 Spring Bean 注入
        factory.setJobFactory(new SysJobFactory(applicationContext));

        log.info("SxwlQuartzAutoConfiguration: SchedulerFactoryBean 初始化完成, threadCount=10");
        return factory;
    }

    /**
     * 创建 SysJobManager 供业务层使用
     */
    @Bean
    @ConditionalOnMissingBean
    public SysJobManager sysJobManager(Scheduler scheduler) {
        return new SysJobManager(scheduler);
    }
}

package com.sxwl.quartz.factory;

import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.AdaptableJobFactory;

/**
 * 定时任务 Job 工厂
 *
 * <p>继承 AdaptableJobFactory，在 Quartz 创建 Job 实例时自动执行 Spring 的依赖注入，
 * 使得 Job 类中可以使用 {@code @Autowired} / {@code @Resource} 等注解。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public class SysJobFactory extends AdaptableJobFactory {

    private static final Logger log = LoggerFactory.getLogger(SysJobFactory.class);

    private final AutowireCapableBeanFactory autowireBeanFactory;

    public SysJobFactory(ApplicationContext applicationContext) {
        this.autowireBeanFactory = applicationContext.getAutowireCapableBeanFactory();
    }

    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        Object jobInstance = super.createJobInstance(bundle);
        // 为 Job 实例执行 Spring 依赖注入
        autowireBeanFactory.autowireBean(jobInstance);
        log.debug("SysJobFactory 已为 Job 实例注入依赖: {}", jobInstance.getClass().getName());
        return jobInstance;
    }
}

package com.sxwl.quartz.manager;

import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * 定时任务管理器
 *
 * <p>封装 Quartz Scheduler 的常用操作，业务模块通过此管理器增删改查任务。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public class SysJobManager {

    private static final Logger log = LoggerFactory.getLogger(SysJobManager.class);

    private final Scheduler scheduler;

    public SysJobManager(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * 获取 Scheduler 实例，供高级用法（如 listenJob 等）
     */
    public Scheduler getScheduler() {
        return scheduler;
    }

    /**
     * 创建定时任务
     *
     * @param jobName       任务名称
     * @param jobGroup      任务分组
     * @param className     目标 Bean 全限定类名
     * @param methodName    目标方法名
     * @param cronExpression Cron 表达式
     * @param params        调用参数（可选）
     */
    public void createJob(String jobName, String jobGroup,
                          String className, String methodName,
                          String cronExpression, String params) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        if (scheduler.checkExists(jobKey)) {
            log.warn("任务已存在, 跳过创建: jobKey={}", jobKey);
            return;
        }

        // 创建前校验目标方法真实存在，避免创建必然失败的任务（L16）
        if (className == null || className.isBlank() || methodName == null || methodName.isBlank()) {
            throw new SchedulerException("className 和 methodName 不能为空");
        }
        try {
            Class<?> clazz = Class.forName(className);
            if (params != null && !params.isEmpty()) {
                clazz.getMethod(methodName, String.class);
            } else {
                clazz.getMethod(methodName);
            }
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new SchedulerException("目标方法不存在: " + className + "#" + methodName, e);
        }

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("className", className);
        jobDataMap.put("methodName", methodName);
        if (params != null) {
            jobDataMap.put("params", params);
        }

        JobDetail jobDetail = JobBuilder.newJob(QuartzJobDelegate.class)
                .withIdentity(jobKey)
                .withDescription("由系统调度创建: " + className + "#" + methodName)
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();

        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobName + "Trigger", jobGroup)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .forJob(jobKey)
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
        log.info("定时任务创建成功: jobName={}, jobGroup={}, cron={}", jobName, jobGroup, cronExpression);
    }

    /**
     * 更新任务 Cron 表达式
     */
    public void updateCron(String jobName, String jobGroup, String cronExpression) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName + "Trigger", jobGroup);
        CronTrigger newTrigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();
        scheduler.rescheduleJob(triggerKey, newTrigger);
        log.info("任务 Cron 已更新: jobName={}, jobGroup={}, newCron={}", jobName, jobGroup, cronExpression);
    }

    /**
     * 删除定时任务
     */
    public void deleteJob(String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        scheduler.deleteJob(jobKey);
        log.info("任务已删除: jobName={}, jobGroup={}", jobName, jobGroup);
    }

    /**
     * 暂停定时任务
     */
    public void pauseJob(String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        scheduler.pauseJob(jobKey);
        log.info("任务已暂停: jobName={}, jobGroup={}", jobName, jobGroup);
    }

    /**
     * 恢复定时任务
     */
    public void resumeJob(String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        scheduler.resumeJob(jobKey);
        log.info("任务已恢复: jobName={}, jobGroup={}", jobName, jobGroup);
    }

    /**
     * 立即执行一次（触发 Job）
     */
    public void runOnce(String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroup);
        scheduler.triggerJob(jobKey);
        log.info("任务已触发立即执行: jobName={}, jobGroup={}", jobName, jobGroup);
    }

    /**
     * 检查任务是否存在
     */
    public boolean checkExists(String jobName, String jobGroup) throws SchedulerException {
        return scheduler.checkExists(JobKey.jobKey(jobName, jobGroup));
    }

    /**
     * 获取所有任务 JobKey 列表
     */
    public java.util.List<JobKey> getAllJobKeys() throws SchedulerException {
        return scheduler.getJobKeys(GroupMatcher.anyGroup()).stream().toList();
    }

    // ==================== 内部 Job 委托类 ====================

    /**
     * Quartz Job 委托类，SysJobManager 创建任务时统一使用此类作为 Job 实现。
     * 实际执行时通过 JobDataMap 中的 className / methodName 反射调用 Spring Bean。
     * <p>由于 {@link com.sxwl.quartz.factory.SysJobFactory} 会在创建实例时自动注入
     * Spring 依赖，此类的 {@code @Autowired ApplicationContext} 会被自动注入。</p>
     */
    @DisallowConcurrentExecution
    public static class QuartzJobDelegate implements Job {

        private static final Logger log = LoggerFactory.getLogger(QuartzJobDelegate.class);

        @Autowired
        private ApplicationContext applicationContext;

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            JobDataMap dataMap = context.getMergedJobDataMap();
            String className = dataMap.getString("className");
            String methodName = dataMap.getString("methodName");
            String params = dataMap.getString("params");

            long startTime = System.currentTimeMillis();
            boolean success = false;
            String errorMsg = null;

            try {
                if (className == null || methodName == null) {
                    errorMsg = "className 或 methodName 为空";
                    log.error("QuartzJobDelegate 执行失败: {}", errorMsg);
                    return;
                }

                // 从 ApplicationContext 获取目标 Bean
                Class<?> clazz = Class.forName(className);
                Object bean = applicationContext.getBean(clazz);

                if (params != null && !params.isEmpty()) {
                    // 带 String 参数的方法
                    var method = bean.getClass().getMethod(methodName, String.class);
                    method.invoke(bean, params);
                } else {
                    // 无参方法
                    var method = bean.getClass().getMethod(methodName);
                    method.invoke(bean);
                }
                success = true;
                log.info("定时任务执行成功: {}.{}({})", className, methodName, params);
            } catch (BeansException e) {
                errorMsg = "获取 Bean 失败: " + e.getMessage();
                log.error("定时任务获取 Bean 失败: className={}", className, e);
                throw new JobExecutionException("获取 Bean 失败: " + className, e);
            } catch (ClassNotFoundException e) {
                errorMsg = "类不存在: " + className;
                log.error("定时任务类不存在: className={}", className, e);
                throw new JobExecutionException("Class not found: " + className, e);
            } catch (NoSuchMethodException e) {
                errorMsg = "方法不存在: " + className + "#" + methodName;
                log.error("定时任务方法不存在: {}.{}", className, methodName, e);
                throw new JobExecutionException("Method not found: " + className + "#" + methodName, e);
            } catch (Exception e) {
                errorMsg = e.getMessage();
                log.error("定时任务反射调用异常: {}.{}", className, methodName, e);
                throw new JobExecutionException(e);
            } finally {
                long durationMs = System.currentTimeMillis() - startTime;
                // 发布执行事件，由 module-job 的监听器落库（sys_job_log_info），
                // 使"任务日志"功能生效。事件发布失败不影响任务本身。
                try {
                    applicationContext.publishEvent(
                            new JobExecutionEvent(this, className, methodName, params, success, errorMsg, durationMs));
                } catch (Exception ex) {
                    log.warn("发布任务执行事件失败: {}.{}", className, methodName, ex);
                }
            }
        }
    }
}


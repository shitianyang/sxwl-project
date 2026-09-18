package com.sxwl.quartz.job;

import com.sxwl.quartz.event.JobExecutionEvent;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Quartz Job 委托执行类
 *
 * <p>所有定时任务统一使用此类作为 Quartz Job 实现，通过 {@code JobDataMap} 中的
 * {@code className} / {@code methodName} / {@code params} 反射调用 Spring Bean。</p>
 *
 * <p>该类由 {@link com.sxwl.quartz.factory.SysJobFactory} 自动注入 Spring 依赖，
 * 因此可以使用 {@code @Autowired}、{@code @Resource} 等注解。</p>
 *
 * @author shitianyang
 * @since 0.1.0
 */
@DisallowConcurrentExecution
public class QuartzJobDelegate implements Job {

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
            if (className == null || className.isBlank() || methodName == null || methodName.isBlank()) {
                errorMsg = "className 或 methodName 为空";
                log.error("QuartzJobDelegate 执行失败: {}", errorMsg);
                return;
            }

            // 从 ApplicationContext 获取目标 Spring Bean
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
            
            // 发布执行事件（异步落库到 sys_job_log_info）
            // 事件发布失败不影响任务本身执行
            try {
                applicationContext.publishEvent(
                        new JobExecutionEvent(this, className, methodName, params, success, errorMsg, durationMs));
            } catch (Exception ex) {
                log.warn("发布任务执行事件失败: {}.{}，但不影响任务执行: {}", 
                        className, methodName, ex.getMessage());
            }
        }
    }
}

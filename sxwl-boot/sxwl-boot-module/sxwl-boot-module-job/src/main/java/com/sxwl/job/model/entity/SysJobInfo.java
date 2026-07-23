package com.sxwl.job.model.entity;

import com.sxwl.common.entity.SxwlBasicField;

/**
 * 定时任务实体
 *
 * <p>对应数据库 {@code sys_job_info} 表。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public class SysJobInfo extends SxwlBasicField {

    /** 任务名称 */
    private String jobName;

    /** 任务分组 */
    private String jobGroup;

    /** 调用目标 Bean 全限定类名 */
    private String className;

    /** 调用目标方法名 */
    private String methodName;

    /** 方法参数 */
    private String methodParams;

    /** Cron 表达式 */
    private String cronExpression;

    /** 描述说明 */
    private String description;

    /** 状态：0=暂停 1=正常 */
    private Integer status;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodParams() {
        return methodParams;
    }

    public void setMethodParams(String methodParams) {
        this.methodParams = methodParams;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

package com.sxwl.job.model.dto;

import java.time.LocalDateTime;

/**
 * 定时任务 DTO（统一请求/响应）
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public class SysJobDTO {

    /** 任务 ID */
    private Long id;

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

    /** 创建时间 */
    private String createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}

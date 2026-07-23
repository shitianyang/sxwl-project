package com.sxwl.job.model.entity;

import com.sxwl.common.entity.SxwlBasicField;

import java.time.LocalDateTime;

/**
 * 定时任务日志实体
 *
 * <p>对应数据库 {@code sys_job_log_info} 表。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public class SysJobLogInfo extends SxwlBasicField {

    /** 任务 ID（关联 sys_job_info.id） */
    private Long jobId;

    /** 任务名称 */
    private String jobName;

    /** 任务分组 */
    private String jobGroup;

    /** 调用目标类名 */
    private String className;

    /** 调用目标方法名 */
    private String methodName;

    /** 方法参数 */
    private String methodParams;

    /** Cron 表达式 */
    private String cronExpression;

    /** 执行状态：0=失败 1=成功 */
    private Integer status;

    /** 执行耗时（毫秒） */
    private Long executeTime;

    /** 错误信息 */
    private String errorMsg;

    /** 执行时间 */
    private LocalDateTime fireTime;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(Long executeTime) {
        this.executeTime = executeTime;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public LocalDateTime getFireTime() {
        return fireTime;
    }

    public void setFireTime(LocalDateTime fireTime) {
        this.fireTime = fireTime;
    }
}

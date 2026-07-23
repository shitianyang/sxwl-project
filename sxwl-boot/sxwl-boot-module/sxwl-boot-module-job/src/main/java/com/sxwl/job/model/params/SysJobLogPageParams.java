package com.sxwl.job.model.params;

import com.sxwl.common.entity.SxwlPageField;

/**
 * 定时任务日志-分页查询参数
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public class SysJobLogPageParams extends SxwlPageField {

    /** 任务 ID */
    private Long jobId;

    /** 任务名称（模糊匹配） */
    private String jobName;

    /** 执行状态：0=失败 1=成功 */
    private Integer status;

    public SysJobLogPageParams() {
        setCurrent(1);
        setPageSize(10);
    }

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

package com.sxwl.job.model.params;

import com.sxwl.common.entity.SxwlPageField;

/**
 * 定时任务-分页查询参数
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public class SysJobPageParams extends SxwlPageField {

    /** 任务名称（模糊匹配） */
    private String jobName;

    /** 任务分组 */
    private String jobGroup;

    /** 状态：0=暂停 1=正常 */
    private Integer status;

    public SysJobPageParams() {
        setCurrent(1);
        setPageSize(10);
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

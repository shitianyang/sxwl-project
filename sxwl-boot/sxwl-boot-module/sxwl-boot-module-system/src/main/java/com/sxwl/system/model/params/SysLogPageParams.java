package com.sxwl.system.model.params;

import com.sxwl.common.entity.SxwlPageField;

/**
 * 系统日志-分页查询参数
 *
 * @author shitianyang
 * @since 0.1.0
 */
public class SysLogPageParams extends SxwlPageField {

    /** 日志类型：1=登录 2=操作 3=异常 4=安全 */
    private Integer logType;

    /** 模块标题（模糊匹配） */
    private String title;

    /** 操作人账号（精确匹配） */
    private String userName;

    /** 操作状态：0=失败 1=成功 */
    private Integer status;

    /** 开始时间（yyyy-MM-dd HH:mm:ss） */
    private String startTime;

    /** 结束时间（yyyy-MM-dd HH:mm:ss） */
    private String endTime;

    public SysLogPageParams() {
        setCurrent(1);
        setPageSize(10);
    }

    public Integer getLogType() { return logType; }
    public void setLogType(Integer logType) { this.logType = logType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}

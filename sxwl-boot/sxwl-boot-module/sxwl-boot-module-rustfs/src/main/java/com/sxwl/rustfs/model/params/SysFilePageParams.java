package com.sxwl.rustfs.model.params;

import com.sxwl.common.entity.SxwlPageField;

/**
 * 系统文件-分页查询参数
 *
 * @author shitianyang
 * @since 0.1.0
 */
public class SysFilePageParams extends SxwlPageField {

    /** 原始文件名（模糊匹配） */
    private String fileName;

    /** 业务类型（精确匹配） */
    private String businessType;

    /** 开始时间（yyyy-MM-dd HH:mm:ss） */
    private String startTime;

    /** 结束时间（yyyy-MM-dd HH:mm:ss） */
    private String endTime;

    public SysFilePageParams() {
        setCurrent(1);
        setPageSize(10);
    }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}

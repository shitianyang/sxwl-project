package com.sxwl.system.model.params;

import com.sxwl.common.entity.SxwlPageField;

/**
 * 系统岗位-分页查询参数
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
public class SysPositionPageParams extends SxwlPageField {

    /** 岗位编码（模糊匹配） */
    private String positionCode;

    /** 状态：0=禁用 1=启用 */
    private Integer status;

    public SysPositionPageParams() {
        setCurrent(1);
        setPageSize(10);
    }

    public String getPositionCode() {
        return positionCode;
    }

    public void setPositionCode(String positionCode) {
        this.positionCode = positionCode;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

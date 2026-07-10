package com.sxwl.system.model.params;

import com.sxwl.common.entity.SxwlPageField;

/**
 * 系统用户-分页查询参数
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public class SysUserPageParams extends SxwlPageField {

    /** 用户名（模糊匹配） */
    private String username;

    /** 状态：0=禁用 1=启用 */
    private Integer status;

    public SysUserPageParams() {
        setCurrent(1);
        setPageSize(10);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

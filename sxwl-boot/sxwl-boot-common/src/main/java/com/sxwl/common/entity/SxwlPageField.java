package com.sxwl.common.entity;

import com.sxwl.common.constants.SxwlSystemConstants;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一分页字段
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public class SxwlPageField implements Serializable {

    @Serial
    private static final long serialVersionUID = SxwlSystemConstants.SERIAL_VERSION_UID;

    /**
     * 当前页
     */
    private Integer current;

    /**
     * 每页大小
     */
    private Integer pageSize;

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public SxwlPageField() {

    }

    public SxwlPageField(Integer current, Integer pageSize) {
        this.current = current;
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "SxwlPageField{" +
                "current=" + current +
                ", pageSize=" + pageSize +
                '}';
    }
}

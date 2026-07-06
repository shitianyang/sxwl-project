package com.sxwl.common.entity;

import com.sxwl.common.constants.SxwlSystemConstants;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

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
    @NotNull(message = "当前页不能为空")
    @Min(value = 1, message = "当前页必须大于等于 1")
    private Integer current;

    /**
     * 每页大小
     */
    @NotNull(message = "每页数量不能为空")
    @Min(value = 1, message = "每页数量必须大于等于 1")
    @Max(value = SxwlSystemConstants.MAX_PAGE_SIZE, message = "每页数量不能超过 " + SxwlSystemConstants.MAX_PAGE_SIZE)
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

package com.sxwl.config.model.params;

import com.sxwl.common.entity.SxwlPageField;

/**
 * 系统参数配置-分页查询参数
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public class SysConfigPageParams extends SxwlPageField {

    /** 参数键名（模糊匹配） */
    private String configKey;

    /** 参数名称（模糊匹配） */
    private String configName;

    /** 参数类型：system=系统参数 notice=通知参数 job=任务参数 */
    private String configType;

    /** 状态：0=禁用 1=启用 */
    private Integer status;

    public SysConfigPageParams() {
        setCurrent(1);
        setPageSize(10);
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

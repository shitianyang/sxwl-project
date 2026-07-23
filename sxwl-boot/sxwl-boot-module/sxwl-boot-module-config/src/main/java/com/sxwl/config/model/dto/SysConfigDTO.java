package com.sxwl.config.model.dto;

/**
 * 系统参数配置 DTO（统一请求/响应）
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public class SysConfigDTO {

    /** 配置 ID */
    private Long id;

    /** 参数键名 */
    private String configKey;

    /** 参数名称 */
    private String configName;

    /** 参数值 */
    private String configValue;

    /** 参数类型：system=系统参数 notice=通知参数 job=任务参数 */
    private String configType;

    /** 描述说明 */
    private String description;

    /** 状态：0=禁用 1=启用 */
    private Integer status;

    /** 创建时间（列表返回时填充） */
    private String createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}

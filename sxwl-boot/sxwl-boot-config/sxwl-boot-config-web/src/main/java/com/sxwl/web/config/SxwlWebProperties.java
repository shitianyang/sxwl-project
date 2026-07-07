package com.sxwl.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Web 配置属性
 *
 * <p>仅暴露随环境变化的配置项。Jackson 日期格式、时区、null 策略等固定约定
 * 在 {@link SxwlWebAutoConfiguration} 中硬编码。</p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@ConfigurationProperties(prefix = "sxwl.web")
public class SxwlWebProperties {

    /**
     * CORS 允许的跨域来源（逗号分隔），默认 *（允许所有）
     */
    private String allowedOrigins = "*";

    /**
     * 是否启用请求日志，默认 true
     */
    private boolean requestLogEnabled = true;

    public String getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(String allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public boolean isRequestLogEnabled() {
        return requestLogEnabled;
    }

    public void setRequestLogEnabled(boolean requestLogEnabled) {
        this.requestLogEnabled = requestLogEnabled;
    }
}

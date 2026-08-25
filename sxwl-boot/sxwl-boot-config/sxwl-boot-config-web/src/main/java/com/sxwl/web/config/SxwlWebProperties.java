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
     * CORS 允许的跨域来源（逗号分隔）。默认拒绝所有跨域请求。
     */
    private String allowedOrigins = "";

    /**
     * 是否启用请求日志，默认 true
     */
    private boolean requestLogEnabled = true;

    /** 是否信任反向代理传入的 X-Forwarded-For / X-Real-IP。默认关闭。 */
    private boolean trustForwardedHeaders = false;

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

    public boolean isTrustForwardedHeaders() {
        return trustForwardedHeaders;
    }

    public void setTrustForwardedHeaders(boolean trustForwardedHeaders) {
        this.trustForwardedHeaders = trustForwardedHeaders;
    }
}

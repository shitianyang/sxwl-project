package com.sxwl.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS 跨域配置
 *
 * <p>开发环境允许所有来源，生产环境通过配置文件限制。</p>
 *
 * <p><b>配置示例（application.yml）：</b></p>
 * <pre>
 * sxwl:
 *   cors:
 *     allowed-origins: "https://admin.example.com,https://app.example.com"
 * </pre>
 *
 * @author shitianyang
 * @since 1.0.0
 */
@Configuration
public class SxwlCorsConfig implements WebMvcConfigurer {

    /**
     * 允许的跨域来源（逗号分隔，默认 * 表示开发环境允许所有）
     */
    @Value("${sxwl.cors.allowed-origins:*}")
    private String allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(allowedOrigins.split(","))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}

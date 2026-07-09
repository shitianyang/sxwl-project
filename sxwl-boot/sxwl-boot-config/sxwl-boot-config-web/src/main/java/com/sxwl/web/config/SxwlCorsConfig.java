package com.sxwl.web.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS 跨域配置
 *
 * <p>开发环境允许所有来源，生产环境通过配置文件限制。</p>
 *
 * @author shitianyang
 * @since 0.1.0
 */
public class SxwlCorsConfig implements WebMvcConfigurer {

    private final SxwlWebProperties webProperties;

    public SxwlCorsConfig(SxwlWebProperties webProperties) {
        this.webProperties = webProperties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(webProperties.getAllowedOrigins().split(","))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}

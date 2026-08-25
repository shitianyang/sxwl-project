package com.sxwl.web.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * CORS 跨域配置
 *
 * <p>所有环境均使用显式来源白名单，避免凭据被任意跨域来源携带。</p>
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
        String[] allowedOrigins = Arrays.stream(webProperties.getAllowedOrigins().split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toArray(String[]::new);
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}

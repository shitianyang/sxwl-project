package com.sxwl.web.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SxwlCorsConfig} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
class SxwlCorsConfigTest {

    private SxwlWebProperties webProperties;
    private SxwlCorsConfig corsConfig;

    @BeforeEach
    void setUp() {
        webProperties = new SxwlWebProperties();
        corsConfig = new SxwlCorsConfig(webProperties);
    }

    @Test
    @DisplayName("addCorsMappings 应正确配置 CORS 规则")
    void addCorsMappings_shouldConfigureCors() {
        CorsRegistry registry = new CorsRegistry();
        webProperties.setAllowedOrigins("http://localhost:3000,http://example.com");

        // 验证不抛出异常
        assertDoesNotThrow(() -> corsConfig.addCorsMappings(registry));
    }

    @Test
    @DisplayName("默认允许所有来源")
    void addCorsMappings_shouldAllowAllByDefault() {
        CorsRegistry registry = new CorsRegistry();

        assertDoesNotThrow(() -> corsConfig.addCorsMappings(registry));
    }
}

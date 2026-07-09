package com.sxwl.security.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/**
 * Security 模块自动装配入口
 * <p>
 * 通过 {@link EnableConfigurationProperties} 激活配置属性绑定，
 * 通过 {@link Import} 导入 {@link SxwlSecurityConfig} 注册所有 Bean。
 * </p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@AutoConfiguration
@EnableConfigurationProperties(SxwlSecurityProperties.class)
@Import(SxwlSecurityConfig.class)
public class SxwlSecurityAutoConfiguration {
}

package com.sxwl.auth.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 微信登录自动配置
 *
 * <p>激活 {@link SxwlWechatProperties} 属性绑定。</p>
 *
 * @author shitianyang
 * @date 2026/7/7
 * @since 0.1.0
 */
@Configuration
@EnableConfigurationProperties(SxwlWechatProperties.class)
public class SxwlWechatAutoConfiguration {
}

package com.sxwl.web.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.sxwl.common.constants.SxwlSystemConstants;
import com.sxwl.web.advice.SxwlGlobalExceptionHandler;
import com.sxwl.web.advice.SxwlResponseBodyAdvice;
import com.sxwl.web.aspect.SxwlLogAspect;
import com.sxwl.web.filter.SxwlRequestLogFilter;
import com.sxwl.web.filter.SxwlTraceIdFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

/**
 * Web 层自动装配
 *
 * <p>统一注册 Jackson 序列化、CORS、全局异常处理、统一响应包装、
 * TraceId、请求日志、操作日志切面等 Web 基础设施组件。</p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@AutoConfiguration
@EnableConfigurationProperties(SxwlWebProperties.class)
public class SxwlWebAutoConfiguration {

    // ==================== Jackson 序列化 ====================

    @Bean
    @ConditionalOnMissingBean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            builder.simpleDateFormat(SxwlSystemConstants.DATE_PATTERN);
            builder.timeZone(SxwlSystemConstants.DEFAULT_TIME_ZONE);
            builder.serializationInclusion(JsonInclude.Include.NON_NULL);
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            builder.serializerByType(Long.class, ToStringSerializer.instance);
            builder.serializerByType(long.class, ToStringSerializer.instance);
        };
    }

    // ==================== CORS ====================

    @Bean
    @ConditionalOnMissingBean
    public SxwlCorsConfig sxwlCorsConfig(SxwlWebProperties webProperties) {
        return new SxwlCorsConfig(webProperties);
    }

    // ==================== 全局异常处理 ====================

    @Bean
    @ConditionalOnMissingBean
    public SxwlGlobalExceptionHandler sxwlGlobalExceptionHandler() {
        return new SxwlGlobalExceptionHandler();
    }

    // ==================== 统一响应包装 ====================

    @Bean
    @ConditionalOnMissingBean
    public SxwlResponseBodyAdvice sxwlResponseBodyAdvice() {
        return new SxwlResponseBodyAdvice();
    }

    // ==================== TraceId 过滤器（最高优先级） ====================

    @Bean
    public FilterRegistrationBean<SxwlTraceIdFilter> traceIdFilter() {
        FilterRegistrationBean<SxwlTraceIdFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new SxwlTraceIdFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

    // ==================== 请求日志过滤器 ====================

    @Bean
    @ConditionalOnMissingBean
    public FilterRegistrationBean<SxwlRequestLogFilter> requestLogFilter(SxwlWebProperties webProperties) {
        FilterRegistrationBean<SxwlRequestLogFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new SxwlRequestLogFilter(webProperties.isRequestLogEnabled()));
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        return registration;
    }

    // ==================== 操作日志切面 ====================

    @Bean
    @ConditionalOnMissingBean
    public SxwlLogAspect sxwlLogAspect(ApplicationEventPublisher eventPublisher, ObjectMapper objectMapper) {
        return new SxwlLogAspect(eventPublisher, objectMapper);
    }
}

package com.sxwl.web.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.sxwl.common.constants.SxwlSystemConstants;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Jackson 序列化配置
 *
 * <p>统一日期格式、时区、null 值处理策略。</p>
 *
 * <p><b>关键决策：</b></p>
 * <ul>
 *   <li>日期格式复用 {@link SxwlSystemConstants#DATE_PATTERN}</li>
 *   <li>时区：{@link SxwlSystemConstants#DEFAULT_TIME_ZONE}</li>
 *   <li>null 值不序列化（前端按需处理默认值）</li>
 *   <li>不输出时间戳格式的日期</li>
 * </ul>
 *
 * @author shitianyang
 * @date 2026/6/28
 * @since 0.1.0
 */
@Configuration
public class SxwlJacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            // 日期格式：复用全局常量
            builder.simpleDateFormat(SxwlSystemConstants.DATE_PATTERN);
            // 时区：Asia/Shanghai
            builder.timeZone(SxwlSystemConstants.DEFAULT_TIME_ZONE);
            // null 值不序列化
            builder.serializationInclusion(JsonInclude.Include.NON_NULL);
            // 不输出时间戳格式的日期
            builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            // long 值序列化
            builder.serializerByType(Long.class, ToStringSerializer.instance);
            builder.serializerByType(long.class, ToStringSerializer.instance);
        };
    }
}

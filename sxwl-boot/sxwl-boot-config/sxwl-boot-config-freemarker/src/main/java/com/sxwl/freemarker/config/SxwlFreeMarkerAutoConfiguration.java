package com.sxwl.freemarker.config;

import freemarker.template.Configuration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * FreeMarker 引擎自动装配
 *
 * <p>注册 FreeMarker {@link Configuration} Bean，配置模板路径、字符集和数字格式，
 * 供 {@code sxwl-boot-module-codegen} 模块的代码生成引擎使用。</p>
 *
 * <p><b>模板源路径：</b>{@code classpath:/templates/codegen/}</p>
 * <p><b>字符集：</b>UTF-8</p>
 * <p><b>数字格式：</b>computer（不输出逗号分隔符）</p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@AutoConfiguration
public class SxwlFreeMarkerAutoConfiguration {

    /**
     * 默认模板加载路径
     */
    public static final String DEFAULT_TEMPLATE_LOADER_PATH = "classpath:/templates/codegen/";

    /**
     * 注册 FreeMarker Configuration Bean
     *
     * <p>配置项：</p>
     * <ul>
     *   <li>{@code templateLoaderPath} — 指向 {@code classpath:/templates/codegen/}</li>
     *   <li>{@code defaultEncoding} — UTF-8</li>
     *   <li>{@code numberFormat} — computer（避免数字输出千分位逗号）</li>
     * </ul>
     *
     * @return 已配置的 FreeMarker Configuration 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public Configuration freemarkerConfiguration() {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_33);
        configuration.setClassLoaderForTemplateLoading(
                getClass().getClassLoader(),
                DEFAULT_TEMPLATE_LOADER_PATH
        );
        configuration.setDefaultEncoding("UTF-8");
        configuration.setNumberFormat("computer");
        return configuration;
    }
}

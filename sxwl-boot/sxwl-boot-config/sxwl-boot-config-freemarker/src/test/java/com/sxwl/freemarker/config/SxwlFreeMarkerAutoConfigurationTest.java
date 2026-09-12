package com.sxwl.freemarker.config;

import freemarker.template.Configuration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SxwlFreeMarkerAutoConfiguration} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("SxwlFreeMarkerAutoConfiguration 测试")
class SxwlFreeMarkerAutoConfigurationTest {

    private final SxwlFreeMarkerAutoConfiguration autoConfig = new SxwlFreeMarkerAutoConfiguration();

    @Test
    @DisplayName("freemarkerConfiguration 应创建 FreeMarker Configuration 实例")
    void freemarkerConfiguration_shouldCreateConfiguration() {
        Configuration config = autoConfig.freemarkerConfiguration();
        assertNotNull(config);
        assertEquals("UTF-8", config.getDefaultEncoding());
        assertEquals("computer", config.getNumberFormat());
    }

    @Test
    @DisplayName("DEFAULT_TEMPLATE_LOADER_PATH 应为 classpath:/templates/codegen/")
    void defaultTemplateLoaderPath_shouldBeCorrect() {
        assertEquals("classpath:/templates/codegen/", SxwlFreeMarkerAutoConfiguration.DEFAULT_TEMPLATE_LOADER_PATH);
    }
}

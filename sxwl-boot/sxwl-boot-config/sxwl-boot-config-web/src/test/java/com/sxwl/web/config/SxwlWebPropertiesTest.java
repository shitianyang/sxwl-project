package com.sxwl.web.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SxwlWebProperties} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
class SxwlWebPropertiesTest {

    @Test
    @DisplayName("默认值应符合预期")
    void defaultValues_shouldBeCorrect() {
        SxwlWebProperties props = new SxwlWebProperties();
        assertEquals("*", props.getAllowedOrigins());
        assertTrue(props.isRequestLogEnabled());
    }

    @Test
    @DisplayName("setter 应正确修改字段值")
    void setter_shouldWork() {
        SxwlWebProperties props = new SxwlWebProperties();
        props.setAllowedOrigins("http://localhost:3000,http://example.com");
        props.setRequestLogEnabled(false);

        assertEquals("http://localhost:3000,http://example.com", props.getAllowedOrigins());
        assertFalse(props.isRequestLogEnabled());
    }
}

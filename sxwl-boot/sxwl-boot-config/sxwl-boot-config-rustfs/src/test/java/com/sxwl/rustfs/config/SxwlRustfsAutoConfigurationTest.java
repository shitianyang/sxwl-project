package com.sxwl.rustfs.config;

import com.sxwl.rustfs.client.SxwlRustfsTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.S3Client;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * {@link SxwlRustfsAutoConfiguration} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("SxwlRustfsAutoConfiguration 测试")
class SxwlRustfsAutoConfigurationTest {

    private final SxwlRustfsAutoConfiguration autoConfig = new SxwlRustfsAutoConfiguration();

    @Test
    @DisplayName("sxwlRustfsTemplate 应创建模板实例")
    void sxwlRustfsTemplate_shouldCreateTemplate() {
        S3Client s3Client = mock(S3Client.class);
        SxwlRustfsProperties properties = new SxwlRustfsProperties();
        SxwlRustfsTemplate template = autoConfig.sxwlRustfsTemplate(s3Client, properties);
        assertNotNull(template);
    }
}

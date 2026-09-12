package com.sxwl.auth.controller;

import com.sxwl.common.utils.SxwlCaptchaUtils;
import com.sxwl.common.utils.SxwlRedisKeyUtils;
import com.sxwl.redis.helper.SxwlRedisHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * {@link CaptchaController} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CaptchaController 测试")
class CaptchaControllerTest {

    @Mock
    private SxwlRedisHelper redisHelper;

    private CaptchaController controller;

    @BeforeEach
    void setUp() {
        controller = new CaptchaController(redisHelper);
    }

    @Test
    @DisplayName("getImageCaptcha 应生成验证码并存入 Redis")
    void getImageCaptcha_shouldGenerateAndStore() {
        try (MockedStatic<SxwlCaptchaUtils> captchaUtils = mockStatic(SxwlCaptchaUtils.class);
             MockedStatic<SxwlRedisKeyUtils> redisKeyUtils = mockStatic(SxwlRedisKeyUtils.class)) {

            SxwlCaptchaUtils.CaptchaResult result = mock(SxwlCaptchaUtils.CaptchaResult.class);
            when(result.getCode()).thenReturn("A1B2");
            when(result.getBase64Image()).thenReturn("data:image/png;base64,abc123");

            captchaUtils.when(SxwlCaptchaUtils::generateImageCaptcha).thenReturn(result);
            redisKeyUtils.when(() -> SxwlRedisKeyUtils.captchaImageKey(anyString()))
                    .thenReturn("captcha:image:test-uuid");

            Map<String, String> response = controller.getImageCaptcha();

            assertNotNull(response);
            assertTrue(response.containsKey("uuid"));
            assertEquals("data:image/png;base64,abc123", response.get("base64Image"));

            verify(redisHelper).set(eq("captcha:image:test-uuid"), eq("A1B2"), any(Duration.class));
        }
    }
}

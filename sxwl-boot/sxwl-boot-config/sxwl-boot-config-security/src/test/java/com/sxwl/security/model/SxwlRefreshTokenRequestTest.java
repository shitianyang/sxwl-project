package com.sxwl.security.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SxwlRefreshTokenRequest} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
class SxwlRefreshTokenRequestTest {

    @Test
    @DisplayName("getter/setter 应正常读写字段")
    void getterAndSetter_shouldWork() {
        SxwlRefreshTokenRequest request = new SxwlRefreshTokenRequest();
        request.setRefreshToken("refresh-token-value");
        request.setDeviceId("device-001");

        assertEquals("refresh-token-value", request.getRefreshToken());
        assertEquals("device-001", request.getDeviceId());
    }

    @Test
    @DisplayName("默认字段值应为 null")
    void defaultValues_shouldBeNull() {
        SxwlRefreshTokenRequest request = new SxwlRefreshTokenRequest();
        assertNull(request.getRefreshToken());
        assertNull(request.getDeviceId());
    }
}

package com.sxwl.security.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SxwlLoginRequest} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
class SxwlLoginRequestTest {

    @Test
    @DisplayName("getter/setter 应正常读写字段")
    void getterAndSetter_shouldWork() {
        SxwlLoginRequest request = new SxwlLoginRequest();
        request.setUsername("admin");
        request.setPassword("sm2_encrypted_password");
        request.setPhone("13800138000");
        request.setSmsCode("123456");
        request.setWxCode("wx_code_123");
        request.setEmail("test@example.com");
        request.setEmailCode("654321");
        request.setCaptchaCode("A1B2");
        request.setCaptchaUuid("uuid-123");
        request.setDeviceId("device-001");

        assertEquals("admin", request.getUsername());
        assertEquals("sm2_encrypted_password", request.getPassword());
        assertEquals("13800138000", request.getPhone());
        assertEquals("123456", request.getSmsCode());
        assertEquals("wx_code_123", request.getWxCode());
        assertEquals("test@example.com", request.getEmail());
        assertEquals("654321", request.getEmailCode());
        assertEquals("A1B2", request.getCaptchaCode());
        assertEquals("uuid-123", request.getCaptchaUuid());
        assertEquals("device-001", request.getDeviceId());
    }

    @Test
    @DisplayName("默认字段值应为 null")
    void defaultValues_shouldBeNull() {
        SxwlLoginRequest request = new SxwlLoginRequest();
        assertNull(request.getUsername());
        assertNull(request.getPassword());
        assertNull(request.getPhone());
        assertNull(request.getSmsCode());
        assertNull(request.getWxCode());
        assertNull(request.getEmail());
        assertNull(request.getEmailCode());
        assertNull(request.getCaptchaCode());
        assertNull(request.getCaptchaUuid());
        assertNull(request.getDeviceId());
    }
}

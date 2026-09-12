package com.sxwl.security.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SxwlLoginSuccessEvent} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
class SxwlLoginSuccessEventTest {

    @Test
    @DisplayName("getter/setter 应正常读写所有字段")
    void getterAndSetter_shouldWork() {
        SxwlLoginSuccessEvent event = new SxwlLoginSuccessEvent();
        LocalDateTime now = LocalDateTime.now();

        event.setUserId(1L);
        event.setUsername("admin");
        event.setIp("192.168.1.1");
        event.setDeviceId("device-001");
        event.setLoginType("password");
        event.setUserAgent("Chrome/120");
        event.setBrowser("Chrome");
        event.setOs("Windows");
        event.setOperateLocation("北京");
        event.setRequestUrl("/api/login");
        event.setRequestMethod("POST");
        event.setTime(now);

        assertEquals(1L, event.getUserId());
        assertEquals("admin", event.getUsername());
        assertEquals("192.168.1.1", event.getIp());
        assertEquals("device-001", event.getDeviceId());
        assertEquals("password", event.getLoginType());
        assertEquals("Chrome/120", event.getUserAgent());
        assertEquals("Chrome", event.getBrowser());
        assertEquals("Windows", event.getOs());
        assertEquals("北京", event.getOperateLocation());
        assertEquals("/api/login", event.getRequestUrl());
        assertEquals("POST", event.getRequestMethod());
        assertEquals(now, event.getTime());
    }
}

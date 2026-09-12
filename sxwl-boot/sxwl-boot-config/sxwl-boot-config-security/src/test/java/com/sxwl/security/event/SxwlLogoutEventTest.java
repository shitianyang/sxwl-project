package com.sxwl.security.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SxwlLogoutEvent} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
class SxwlLogoutEventTest {

    @Test
    @DisplayName("getter/setter 应正常读写所有字段")
    void getterAndSetter_shouldWork() {
        SxwlLogoutEvent event = new SxwlLogoutEvent();
        LocalDateTime now = LocalDateTime.now();

        event.setUserId(1L);
        event.setUsername("admin");
        event.setTime(now);

        assertEquals(1L, event.getUserId());
        assertEquals("admin", event.getUsername());
        assertEquals(now, event.getTime());
    }
}

package com.sxwl.security.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SxwlLoginFailureEvent} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
class SxwlLoginFailureEventTest {

    @Test
    @DisplayName("getter/setter 应正常读写所有字段")
    void getterAndSetter_shouldWork() {
        SxwlLoginFailureEvent event = new SxwlLoginFailureEvent();
        LocalDateTime now = LocalDateTime.now();

        event.setTargetAccount("admin");
        event.setIp("192.168.1.1");
        event.setFailReason("密码错误");
        event.setFailCount(3);
        event.setUserAgent("Chrome/120");
        event.setBrowser("Chrome");
        event.setOs("Windows");
        event.setOperateLocation("北京");
        event.setTime(now);

        assertEquals("admin", event.getTargetAccount());
        assertEquals("192.168.1.1", event.getIp());
        assertEquals("密码错误", event.getFailReason());
        assertEquals(3, event.getFailCount());
        assertEquals("Chrome/120", event.getUserAgent());
        assertEquals("Chrome", event.getBrowser());
        assertEquals("Windows", event.getOs());
        assertEquals("北京", event.getOperateLocation());
        assertEquals(now, event.getTime());
    }
}

package com.sxwl.security.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SxwlPasswordChangedEvent} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
class SxwlPasswordChangedEventTest {

    @Test
    @DisplayName("getter/setter 应正常读写所有字段")
    void getterAndSetter_shouldWork() {
        SxwlPasswordChangedEvent event = new SxwlPasswordChangedEvent();
        LocalDateTime now = LocalDateTime.now();

        event.setUserId(1L);
        event.setOldEncodedPassword("{sm3}salt$oldHash");
        event.setOperatorId(1L);
        event.setTime(now);

        assertEquals(1L, event.getUserId());
        assertEquals("{sm3}salt$oldHash", event.getOldEncodedPassword());
        assertEquals(1L, event.getOperatorId());
        assertEquals(now, event.getTime());
    }
}

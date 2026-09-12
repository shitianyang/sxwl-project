package com.sxwl.security.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SxwlTokenRevokedEvent} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
class SxwlTokenRevokedEventTest {

    @Test
    @DisplayName("getter/setter 应正常读写所有字段")
    void getterAndSetter_shouldWork() {
        SxwlTokenRevokedEvent event = new SxwlTokenRevokedEvent();

        event.setUserId(1L);
        event.setReason("密码已修改");
        event.setOperatorId(2L);

        assertEquals(1L, event.getUserId());
        assertEquals("密码已修改", event.getReason());
        assertEquals(2L, event.getOperatorId());
    }
}

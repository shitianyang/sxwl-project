package com.sxwl.security.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SxwlTokenPair} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
class SxwlTokenPairTest {

    @Test
    @DisplayName("带参构造和 getter 应正确赋值")
    void constructorAndGetter_shouldWork() {
        SxwlTokenPair pair = new SxwlTokenPair("access-token-value", "refresh-token-value");
        assertEquals("access-token-value", pair.getAccessToken());
        assertEquals("refresh-token-value", pair.getRefreshToken());
    }

    @Test
    @DisplayName("无参构造和 setter 应正常读写")
    void noArgConstructorAndSetter_shouldWork() {
        SxwlTokenPair pair = new SxwlTokenPair();
        pair.setAccessToken("new-access");
        pair.setRefreshToken("new-refresh");
        assertEquals("new-access", pair.getAccessToken());
        assertEquals("new-refresh", pair.getRefreshToken());
    }

    @Test
    @DisplayName("默认字段值应为 null")
    void defaultValues_shouldBeNull() {
        SxwlTokenPair pair = new SxwlTokenPair();
        assertNull(pair.getAccessToken());
        assertNull(pair.getRefreshToken());
    }
}

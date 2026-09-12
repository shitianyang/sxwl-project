package com.sxwl.common.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Redis Key 构建工具类单元测试
 *
 * @author shitianyang
 * @since 0.1.0
 */
@DisplayName("SxwlRedisKeyUtils 工具类测试")
class SxwlRedisKeyUtilsTest {

    private static final long USER_ID = 1L;
    private static final String JTI = "abc-123-def";
    private static final String DEVICE_ID = "web-chrome-abc";
    private static final String CLIENT_TYPE = "admin";

    @Test
    @DisplayName("tokenAdminKey：格式正确")
    void testTokenAdminKey() {
        String key = SxwlRedisKeyUtils.tokenAdminKey(USER_ID, JTI);
        assertEquals("token:admin:1:abc-123-def", key);
    }

    @Test
    @DisplayName("refreshAdminKey：格式正确")
    void testRefreshAdminKey() {
        String key = SxwlRedisKeyUtils.refreshAdminKey(USER_ID, JTI);
        assertEquals("refresh:admin:1:abc-123-def", key);
    }

    @Test
    @DisplayName("tokenFrontKey：格式正确")
    void testTokenFrontKey() {
        String key = SxwlRedisKeyUtils.tokenFrontKey(USER_ID, JTI);
        assertEquals("token:front:1:abc-123-def", key);
    }

    @Test
    @DisplayName("onlineUserDeviceKey：格式正确")
    void testOnlineUserDeviceKey() {
        String key = SxwlRedisKeyUtils.onlineUserDeviceKey(USER_ID, DEVICE_ID);
        assertEquals("online:user:1:web-chrome-abc", key);
    }

    @Test
    @DisplayName("onlineDevicesSetKey：格式正确")
    void testOnlineDevicesSetKey() {
        String key = SxwlRedisKeyUtils.onlineDevicesSetKey(USER_ID);
        assertEquals("online:devices:1", key);
    }

    @Test
    @DisplayName("tokenUserSetKey：格式正确")
    void testTokenUserSetKey() {
        String key = SxwlRedisKeyUtils.tokenUserSetKey(CLIENT_TYPE, USER_ID);
        assertEquals("token:user:admin:1", key);
    }

    @Test
    @DisplayName("tokenInfoKey：格式正确")
    void testTokenInfoKey() {
        String key = SxwlRedisKeyUtils.tokenInfoKey(USER_ID);
        assertEquals("token:info:1", key);
    }

    @Test
    @DisplayName("tokenJwtKey：格式正确")
    void testTokenJwtKey() {
        String key = SxwlRedisKeyUtils.tokenJwtKey(CLIENT_TYPE, USER_ID, DEVICE_ID, JTI);
        assertEquals("token:jwt:admin:1:web-chrome-abc:abc-123-def", key);
    }

    @Test
    @DisplayName("captchaImageKey：格式正确")
    void testCaptchaImageKey() {
        String key = SxwlRedisKeyUtils.captchaImageKey("uuid-550e8400");
        assertEquals("captcha:image:uuid-550e8400", key);
    }

    @Test
    @DisplayName("loginCountKey：格式正确")
    void testLoginCountKey() {
        String key = SxwlRedisKeyUtils.loginCountKey("admin");
        assertEquals("login:count:admin", key);
    }

    @Test
    @DisplayName("dictCacheKey：格式正确")
    void testDictCacheKey() {
        String key = SxwlRedisKeyUtils.dictCacheKey("sys_user_status");
        assertEquals("dict:sys_user_status", key);
    }

    @Test
    @DisplayName("repeatSubmitKey：格式正确")
    void testRepeatSubmitKey() {
        String key = SxwlRedisKeyUtils.repeatSubmitKey(USER_ID, "/system/user/add");
        assertEquals("repeat:1:/system/user/add", key);
    }

    @Test
    @DisplayName("lockKey：格式正确")
    void testLockKey() {
        String key = SxwlRedisKeyUtils.lockKey("backup:20260705");
        assertEquals("lock:backup:20260705", key);
    }

    @Test
    @DisplayName("configCacheKey：格式正确")
    void testConfigCacheKey() {
        String key = SxwlRedisKeyUtils.configCacheKey("sys.site.name");
        assertEquals("config:sys.site.name", key);
    }

    @Test
    @DisplayName("loginFailAccountIpKey：格式正确")
    void testLoginFailAccountIpKey() {
        String key = SxwlRedisKeyUtils.loginFailAccountIpKey("admin", "192.168.1.1");
        assertEquals("login:fail:admin:192.168.1.1", key);
    }
}

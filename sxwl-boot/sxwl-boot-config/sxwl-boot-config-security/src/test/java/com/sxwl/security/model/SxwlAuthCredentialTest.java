package com.sxwl.security.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SxwlAuthCredential} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
class SxwlAuthCredentialTest {

    @Test
    @DisplayName("带参构造和 getter 应正确赋值")
    void constructorAndGetter_shouldWork() {
        SxwlLoginUser loginUser = new SxwlLoginUser();
        loginUser.setUserId(1L);
        SxwlAuthCredential credential = new SxwlAuthCredential(loginUser, "{sm3}salt$hash");
        assertEquals(1L, credential.getLoginUser().getUserId());
        assertEquals("{sm3}salt$hash", credential.getEncodedPassword());
    }

    @Test
    @DisplayName("无参构造和 setter 应正常读写")
    void noArgConstructorAndSetter_shouldWork() {
        SxwlAuthCredential credential = new SxwlAuthCredential();
        SxwlLoginUser user = new SxwlLoginUser();
        credential.setLoginUser(user);
        credential.setEncodedPassword("{sm3}newSalt$newHash");
        assertSame(user, credential.getLoginUser());
        assertEquals("{sm3}newSalt$newHash", credential.getEncodedPassword());
    }

    @Test
    @DisplayName("默认字段值应为 null")
    void defaultValues_shouldBeNull() {
        SxwlAuthCredential credential = new SxwlAuthCredential();
        assertNull(credential.getLoginUser());
        assertNull(credential.getEncodedPassword());
    }
}

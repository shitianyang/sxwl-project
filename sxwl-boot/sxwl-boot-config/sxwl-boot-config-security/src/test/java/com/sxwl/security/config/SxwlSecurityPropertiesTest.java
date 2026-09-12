package com.sxwl.security.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SxwlSecurityProperties} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
class SxwlSecurityPropertiesTest {

    @Test
    @DisplayName("默认值应符合预期")
    void defaultValues_shouldBeCorrect() {
        SxwlSecurityProperties props = new SxwlSecurityProperties();

        assertEquals("", props.getJwtSecret());
        assertEquals(1800, props.getAccessTokenExpire());
        assertEquals(604800, props.getRefreshTokenExpire());
        assertEquals(7200, props.getFrontAccessTokenExpire());
        assertEquals(2592000, props.getFrontRefreshTokenExpire());
        assertEquals(300, props.getTokenRenewThreshold());
        assertEquals(8, props.getPasswordMinLength());
        assertEquals(32, props.getPasswordMaxLength());
        assertEquals(90, props.getPasswordExpireDays());
        assertEquals(5, props.getPasswordHistoryCount());
        assertEquals(5, props.getLoginFailMaxCount());
        assertEquals(1800, props.getLockDuration());
        assertEquals(3, props.getCaptchaTriggerCount());
        assertEquals(1440, props.getSm2KeyRotationIntervalMinutes());
        assertEquals(120, props.getSm2KeyGracePeriodMinutes());
        assertEquals(1, props.getSm2KeyMaxHistory());
        assertEquals(0, props.getMaxDevicesPerUser());
    }

    @Test
    @DisplayName("setter 应正确修改字段值")
    void setter_shouldWork() {
        SxwlSecurityProperties props = new SxwlSecurityProperties();

        props.setJwtSecret("my-secret");
        props.setAccessTokenExpire(3600);
        props.setRefreshTokenExpire(86400);
        props.setFrontAccessTokenExpire(14400);
        props.setFrontRefreshTokenExpire(5184000);
        props.setTokenRenewThreshold(600);
        props.setPasswordMinLength(6);
        props.setPasswordMaxLength(20);
        props.setPasswordExpireDays(30);
        props.setPasswordHistoryCount(3);
        props.setLoginFailMaxCount(3);
        props.setLockDuration(900);
        props.setCaptchaTriggerCount(2);
        props.setSm2KeyRotationIntervalMinutes(720);
        props.setSm2KeyGracePeriodMinutes(60);
        props.setSm2KeyMaxHistory(2);
        props.setMaxDevicesPerUser(5);

        assertEquals("my-secret", props.getJwtSecret());
        assertEquals(3600, props.getAccessTokenExpire());
        assertEquals(86400, props.getRefreshTokenExpire());
        assertEquals(14400, props.getFrontAccessTokenExpire());
        assertEquals(5184000, props.getFrontRefreshTokenExpire());
        assertEquals(600, props.getTokenRenewThreshold());
        assertEquals(6, props.getPasswordMinLength());
        assertEquals(20, props.getPasswordMaxLength());
        assertEquals(30, props.getPasswordExpireDays());
        assertEquals(3, props.getPasswordHistoryCount());
        assertEquals(3, props.getLoginFailMaxCount());
        assertEquals(900, props.getLockDuration());
        assertEquals(2, props.getCaptchaTriggerCount());
        assertEquals(720, props.getSm2KeyRotationIntervalMinutes());
        assertEquals(60, props.getSm2KeyGracePeriodMinutes());
        assertEquals(2, props.getSm2KeyMaxHistory());
        assertEquals(5, props.getMaxDevicesPerUser());
    }
}

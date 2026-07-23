package com.sxwl.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 安全配置属性
 * <p>
 * 仅暴露随环境变化的配置项。密码编码规则、Token 格式等固定约定在代码中硬编码。
 * </p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@ConfigurationProperties(prefix = "sxwl.security")
public class SxwlSecurityProperties {

    // ==================== JWT ====================

    /** JWT 签名密钥（SM4 加密后的 Base64），留空则自动生成 */
    private String jwtSecret = "";

    /** B 端 Access Token 过期（秒），默认 1800（30 分钟） */
    private long accessTokenExpire = 1800;

    /** B 端 Refresh Token 过期（秒），默认 604800（7 天） */
    private long refreshTokenExpire = 604800;

    /** C 端 Access Token 过期（秒），默认 7200（2 小时） */
    private long frontAccessTokenExpire = 7200;

    /** C 端 Refresh Token 过期（秒），默认 2592000（30 天） */
    private long frontRefreshTokenExpire = 2592000;

    /** 自动续期阈值（秒），剩余有效期小于此值时自动签发新 Token */
    private long tokenRenewThreshold = 300;

    // ==================== 密码 ====================

    /** 密码最小长度 */
    private int passwordMinLength = 8;

    /** 密码最大长度 */
    private int passwordMaxLength = 32;

    /** 密码过期天数 */
    private int passwordExpireDays = 90;

    /** 密码历史不可重复次数 */
    private int passwordHistoryCount = 5;

    // ==================== 锁定 ====================

    /** 连续失败锁定次数 */
    private int loginFailMaxCount = 5;

    /** 锁定时间（秒） */
    private long lockDuration = 1800;

    // ==================== 验证码 ====================

    /** 触发图形验证码的失败次数 */
    private int captchaTriggerCount = 3;

    // ==================== SM2 密钥轮换 ====================

    /** SM2 密钥轮换间隔（分钟），默认 1440（24 小时轮换一次） */
    private long sm2KeyRotationIntervalMinutes = 1440;

    /** SM2 历史密钥保留宽限期（分钟），默认 120（2 小时），用于解密旧公钥加密的密码 */
    private long sm2KeyGracePeriodMinutes = 120;

    /** SM2 历史密钥最大保留数（不含当前），默认 1 */
    private int sm2KeyMaxHistory = 1;

    // ==================== 在线用户 ====================

    /** 单用户最大并发设备数（0=不限制） */
    private int maxDevicesPerUser = 0;

    // ==================== getters/setters ====================

    public String getJwtSecret() {
        return jwtSecret;
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public long getAccessTokenExpire() {
        return accessTokenExpire;
    }

    public void setAccessTokenExpire(long accessTokenExpire) {
        this.accessTokenExpire = accessTokenExpire;
    }

    public long getRefreshTokenExpire() {
        return refreshTokenExpire;
    }

    public void setRefreshTokenExpire(long refreshTokenExpire) {
        this.refreshTokenExpire = refreshTokenExpire;
    }

    public long getFrontAccessTokenExpire() {
        return frontAccessTokenExpire;
    }

    public void setFrontAccessTokenExpire(long frontAccessTokenExpire) {
        this.frontAccessTokenExpire = frontAccessTokenExpire;
    }

    public long getFrontRefreshTokenExpire() {
        return frontRefreshTokenExpire;
    }

    public void setFrontRefreshTokenExpire(long frontRefreshTokenExpire) {
        this.frontRefreshTokenExpire = frontRefreshTokenExpire;
    }

    public long getTokenRenewThreshold() {
        return tokenRenewThreshold;
    }

    public void setTokenRenewThreshold(long tokenRenewThreshold) {
        this.tokenRenewThreshold = tokenRenewThreshold;
    }

    public int getPasswordMinLength() {
        return passwordMinLength;
    }

    public void setPasswordMinLength(int passwordMinLength) {
        this.passwordMinLength = passwordMinLength;
    }

    public int getPasswordMaxLength() {
        return passwordMaxLength;
    }

    public void setPasswordMaxLength(int passwordMaxLength) {
        this.passwordMaxLength = passwordMaxLength;
    }

    public int getPasswordExpireDays() {
        return passwordExpireDays;
    }

    public void setPasswordExpireDays(int passwordExpireDays) {
        this.passwordExpireDays = passwordExpireDays;
    }

    public int getPasswordHistoryCount() {
        return passwordHistoryCount;
    }

    public void setPasswordHistoryCount(int passwordHistoryCount) {
        this.passwordHistoryCount = passwordHistoryCount;
    }

    public int getLoginFailMaxCount() {
        return loginFailMaxCount;
    }

    public void setLoginFailMaxCount(int loginFailMaxCount) {
        this.loginFailMaxCount = loginFailMaxCount;
    }

    public long getLockDuration() {
        return lockDuration;
    }

    public void setLockDuration(long lockDuration) {
        this.lockDuration = lockDuration;
    }

    public int getCaptchaTriggerCount() {
        return captchaTriggerCount;
    }

    public void setCaptchaTriggerCount(int captchaTriggerCount) {
        this.captchaTriggerCount = captchaTriggerCount;
    }

    public long getSm2KeyRotationIntervalMinutes() {
        return sm2KeyRotationIntervalMinutes;
    }

    public void setSm2KeyRotationIntervalMinutes(long sm2KeyRotationIntervalMinutes) {
        this.sm2KeyRotationIntervalMinutes = sm2KeyRotationIntervalMinutes;
    }

    public long getSm2KeyGracePeriodMinutes() {
        return sm2KeyGracePeriodMinutes;
    }

    public void setSm2KeyGracePeriodMinutes(long sm2KeyGracePeriodMinutes) {
        this.sm2KeyGracePeriodMinutes = sm2KeyGracePeriodMinutes;
    }

    public int getSm2KeyMaxHistory() {
        return sm2KeyMaxHistory;
    }

    public void setSm2KeyMaxHistory(int sm2KeyMaxHistory) {
        this.sm2KeyMaxHistory = sm2KeyMaxHistory;
    }

    public int getMaxDevicesPerUser() {
        return maxDevicesPerUser;
    }

    public void setMaxDevicesPerUser(int maxDevicesPerUser) {
        this.maxDevicesPerUser = maxDevicesPerUser;
    }
}

package com.sxwl.system.model.dto;

import java.time.LocalDateTime;

/**
 * 在线用户 DTO
 *
 * <p>数据完全来自 Redis，不存在对应的 DB 表。
 * <br>每个 DTO 对应一个设备会话（同一用户多端登录会产生多条记录）。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public class SysOnlineUserDTO {

    /** 用户 ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 登录 IP */
    private String ip;

    /** 浏览器 */
    private String browser;

    /** 操作系统 */
    private String os;

    /** 设备 ID */
    private String deviceId;

    /** 登录时间 */
    private LocalDateTime loginTime;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }
}

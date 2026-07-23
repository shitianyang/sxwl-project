package com.sxwl.security.event;

import java.time.LocalDateTime;

/**
 * 登录成功事件
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public class SxwlLoginSuccessEvent {

    private Long userId;
    private String username;
    private String ip;
    private String deviceId;
    private String loginType;
    private String userAgent;
    private String browser;
    private String os;
    private String operateLocation;
    private String requestUrl;
    private String requestMethod;
    private LocalDateTime time;

    public SxwlLoginSuccessEvent() {
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getLoginType() { return loginType; }
    public void setLoginType(String loginType) { this.loginType = loginType; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getBrowser() { return browser; }
    public void setBrowser(String browser) { this.browser = browser; }

    public String getOs() { return os; }
    public void setOs(String os) { this.os = os; }

    public String getOperateLocation() { return operateLocation; }
    public void setOperateLocation(String operateLocation) { this.operateLocation = operateLocation; }

    public String getRequestUrl() { return requestUrl; }
    public void setRequestUrl(String requestUrl) { this.requestUrl = requestUrl; }

    public String getRequestMethod() { return requestMethod; }
    public void setRequestMethod(String requestMethod) { this.requestMethod = requestMethod; }

    public LocalDateTime getTime() { return time; }
    public void setTime(LocalDateTime time) { this.time = time; }
}

package com.sxwl.security.model;

import jakarta.validation.constraints.NotBlank;

/**
 * 刷新 Token 请求体
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public class SxwlRefreshTokenRequest {

    /** 当前 refresh_token */
    @NotBlank(message = "refreshToken 不能为空")
    private String refreshToken;

    /** 设备标识 */
    @NotBlank(message = "设备标识不能为空")
    private String deviceId;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}

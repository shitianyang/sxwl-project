package com.sxwl.security.model;

/**
 * Token 对（access + refresh）
 * <p>
 * 登录成功或刷新 Token 后的返回值，由 AuthController 返回给前端。
 * </p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public class SxwlTokenPair {

    /** Access Token（用于 API 请求鉴权） */
    private String accessToken;

    /** Refresh Token（用于无感续期） */
    private String refreshToken;

    public SxwlTokenPair() {
    }

    public SxwlTokenPair(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}

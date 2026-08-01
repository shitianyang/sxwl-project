package com.sxwl.auth.model.request;

import jakarta.validation.constraints.NotBlank;

/**
 * 微信登录请求
 *
 * @author shitianyang
 * @date 2026/7/7
 * @since 0.1.0
 */
public class SxwlWechatLoginRequest {

    /** uni.login 获取的微信临时凭证 */
    @NotBlank(message = "code 不能为空")
    private String code;

    /** 微信昵称（用户拒绝授权时为空） */
    private String nickname;

    /** 微信头像 URL（用户拒绝授权时为空） */
    private String avatarUrl;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}

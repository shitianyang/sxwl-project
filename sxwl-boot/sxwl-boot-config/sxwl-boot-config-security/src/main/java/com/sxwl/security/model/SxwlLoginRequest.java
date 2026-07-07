package com.sxwl.security.model;

import jakarta.validation.constraints.NotBlank;

/**
 * 登录请求体
 * <p>
 * 不同登录方式使用不同端点，使用对应字段组合。
 * </p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public class SxwlLoginRequest {

    /** 用户名（password 登录必填） */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 密码（password 登录必填，SM2 加密后的 Base64） */
    private String password;

    /** 手机号（sms 登录必填） */
    private String phone;

    /** 短信验证码（sms 登录必填） */
    private String smsCode;

    /** 微信授权码（wechat 登录必填） */
    private String wxCode;

    /** 邮箱（email 登录必填） */
    private String email;

    /** 邮箱验证码（email 登录必填） */
    private String emailCode;

    /** 图形验证码（失败次数 >= 阈值时必填） */
    private String captchaCode;

    /** 图形验证码 UUID（与 captchaCode 配合） */
    private String captchaUuid;

    /** 设备标识（必填，用于多设备管理） */
    @NotBlank(message = "设备标识不能为空")
    private String deviceId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }

    public String getWxCode() {
        return wxCode;
    }

    public void setWxCode(String wxCode) {
        this.wxCode = wxCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailCode() {
        return emailCode;
    }

    public void setEmailCode(String emailCode) {
        this.emailCode = emailCode;
    }

    public String getCaptchaCode() {
        return captchaCode;
    }

    public void setCaptchaCode(String captchaCode) {
        this.captchaCode = captchaCode;
    }

    public String getCaptchaUuid() {
        return captchaUuid;
    }

    public void setCaptchaUuid(String captchaUuid) {
        this.captchaUuid = captchaUuid;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}

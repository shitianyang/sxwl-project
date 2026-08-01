package com.sxwl.auth.model.entity;

import com.sxwl.common.entity.SxwlBasicField;

/**
 * C 端用户实体（对应表 pla_user_info）
 *
 * <p>仅包含认证所需字段，其余业务字段由 C 端应用自行扩展。</p>
 *
 * @author shitianyang
 * @date 2026/7/7
 * @since 0.1.0
 */
public class PlaUserInfo extends SxwlBasicField {

    /** 微信 OpenID（全局唯一） */
    private String wxOpenId;

    /** 昵称 */
    private String nickname;

    /** 头像 URL */
    private String avatar;

    /** 注册来源：wechat=微信注册 phone=手机号注册 */
    private String registerSource;

    /** 最近登录方式 */
    private String loginType;

    /** 状态：0=禁用 1=正常 */
    private Integer status;

    public String getWxOpenId() {
        return wxOpenId;
    }

    public void setWxOpenId(String wxOpenId) {
        this.wxOpenId = wxOpenId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRegisterSource() {
        return registerSource;
    }

    public void setRegisterSource(String registerSource) {
        this.registerSource = registerSource;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

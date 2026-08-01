package com.sxwl.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 微信小程序配置属性
 *
 * <p>绑定 {@code sxwl.wechat.*} 配置项，用于微信 code2session 鉴权。</p>
 *
 * @author shitianyang
 * @date 2026/7/7
 * @since 0.1.0
 */
@ConfigurationProperties(prefix = "sxwl.wechat")
public class SxwlWechatProperties {

    /** 微信小程序 AppID */
    private String appid = "";

    /** 微信小程序 AppSecret */
    private String secret = "";

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}

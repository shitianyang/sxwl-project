package com.sxwl.security.model;

/**
 * 认证凭据
 * <p>
 * 仅在登录策略内部 {@code passwordEncoder.matches()} 这条链路上存在，方法返回后随栈销毁。
 * <b>不存入 SecurityContext，不写入 Redis</b>。
 * </p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public class SxwlAuthCredential {

    /** 脱敏后的用户信息（不含密码） */
    private SxwlLoginUser loginUser;

    /** 编码后的密码（{sm3}{salt}$hash 格式），仅认证比对使用 */
    private String encodedPassword;

    public SxwlAuthCredential() {
    }

    public SxwlAuthCredential(SxwlLoginUser loginUser, String encodedPassword) {
        this.loginUser = loginUser;
        this.encodedPassword = encodedPassword;
    }

    public SxwlLoginUser getLoginUser() {
        return loginUser;
    }

    public void setLoginUser(SxwlLoginUser loginUser) {
        this.loginUser = loginUser;
    }

    public String getEncodedPassword() {
        return encodedPassword;
    }

    public void setEncodedPassword(String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }
}

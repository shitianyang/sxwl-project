package com.sxwl.common.exception;

/**
 * 401 未认证异常
 *
 * <p>当用户未登录或 Token 已过期时抛出此异常。
 * 由全局异常处理器拦截，返回 HTTP 401 状态码。</p>
 *
 * <p><b>触发场景：</b></p>
 * <ul>
 *   <li>未携带 Authorization header</li>
 *   <li>Token 签名无效或被篡改</li>
 *   <li>Token 已过期</li>
 *   <li>Token 不在 Redis 白名单中（已登出或被强制踢出）</li>
 * </ul>
 *
 * <p><b>与 {@link SxwlForbiddenException} 的区别：</b>
 * 401 是"你是谁我不知道"，403 是"我知道你是谁但你没权限"。</p>
 *
 * @author shitianyang
 * @since 0.1.0
 */
public class SxwlUnauthorizedException extends SxwlBusinessException {

    /**
     * 使用默认消息创建 401 异常
     */
    public SxwlUnauthorizedException() {
        super(401, "未登录或 Token 已过期");
    }

    /**
     * 使用自定义消息创建 401 异常
     *
     * @param message 错误描述，如 "Token 已被强制下线"
     */
    public SxwlUnauthorizedException(String message) {
        super(401, message);
    }
}

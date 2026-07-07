package com.sxwl.security.spi;

import com.sxwl.security.model.SxwlLoginRequest;
import com.sxwl.security.model.SxwlLoginUser;

/**
 * 登录认证策略 SPI 接口
 * <p>
 * 由 auth 模块实现（SxwlPasswordAuthStrategy / SxwlSmsAuthStrategy / SxwlWechatAuthStrategy 等）。
 * security 的认证处理器通过 {@link #supportType()} 路由到对应策略。
 * </p>
 *
 * <h3>扩展方式</h3>
 * <pre>
 * &#64;Component
 * public class SxwlWechatAuthStrategy implements SxwlAuthenticationStrategy {
 *     &#64;Override
 *     public String supportType() { return "wechat"; }
 *
 *     &#64;Override
 *     public SxwlLoginUser authenticate(SxwlLoginRequest request) {
 *         // 微信授权码换取用户信息 → 查库/自动注册 → 返回 SxwlLoginUser
 *     }
 * }
 * </pre>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public interface SxwlAuthenticationStrategy {

    /**
     * 本策略支持的登录类型
     *
     * @return loginType 标识（如 "password" / "sms" / "wechat"）
     */
    String supportType();

    /**
     * 执行认证逻辑
     *
     * @param request 登录请求体
     * @return 认证通过后的用户对象（不含密码）
     * @throws com.sxwl.common.exception.SxwlBusinessException 认证失败时抛出
     */
    SxwlLoginUser authenticate(SxwlLoginRequest request);
}

package com.sxwl.security.spi;

import com.sxwl.security.model.SxwlLoginRequest;
import com.sxwl.security.model.SxwlLoginUser;

/**
 * 登录认证策略 SPI 接口
 * <p>
 * 由 auth 模块实现，每种登录方式一个策略类。
 * 不同登录方式对应不同 Controller 端点，Controller 直接调用对应策略。
 * </p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public interface SxwlAuthenticationStrategy {

    /**
     * 执行认证逻辑
     *
     * @param request 登录请求体
     * @return 认证通过后的用户对象（不含密码）
     * @throws com.sxwl.common.exception.SxwlBusinessException 认证失败时抛出
     */
    SxwlLoginUser authenticate(SxwlLoginRequest request);
}

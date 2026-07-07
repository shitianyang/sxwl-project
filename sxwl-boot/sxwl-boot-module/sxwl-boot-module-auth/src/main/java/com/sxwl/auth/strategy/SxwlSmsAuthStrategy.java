package com.sxwl.auth.strategy;

import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.security.model.SxwlLoginRequest;
import com.sxwl.security.model.SxwlLoginUser;
import com.sxwl.security.spi.SxwlAuthenticationStrategy;
import org.springframework.stereotype.Component;

/**
 * 短信登录策略（stub 占位）
 * <p>
 * 预留扩展点，后续对接短信服务商后实现。
 * </p>
 *
 * @author shitianyang
 * @date 2026/7/7
 * @since 0.1.0
 */
@Component
public class SxwlSmsAuthStrategy implements SxwlAuthenticationStrategy {

    @Override
    public SxwlLoginUser authenticate(SxwlLoginRequest request) {
        throw new SxwlBusinessException(501, "短信登录暂未实现");
    }
}

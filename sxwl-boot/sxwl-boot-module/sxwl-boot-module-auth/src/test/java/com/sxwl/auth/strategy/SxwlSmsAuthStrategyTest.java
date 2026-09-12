package com.sxwl.auth.strategy;

import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.security.model.SxwlLoginRequest;
import com.sxwl.security.model.SxwlLoginUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SxwlSmsAuthStrategy} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("SxwlSmsAuthStrategy 测试")
class SxwlSmsAuthStrategyTest {

    private final SxwlSmsAuthStrategy strategy = new SxwlSmsAuthStrategy();

    @Test
    @DisplayName("authenticate 应抛出未实现异常")
    void authenticate_shouldThrowNotImplemented() {
        SxwlLoginRequest request = new SxwlLoginRequest();
        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> strategy.authenticate(request));
        assertEquals(501, ex.getCode());
    }
}

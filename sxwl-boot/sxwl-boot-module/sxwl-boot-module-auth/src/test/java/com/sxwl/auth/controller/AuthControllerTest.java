package com.sxwl.auth.controller;

import com.sxwl.auth.strategy.SxwlPasswordAuthStrategy;
import com.sxwl.auth.strategy.SxwlSmsAuthStrategy;
import com.sxwl.common.entity.SxwlPublicKeyVO;
import com.sxwl.common.entity.SxwlResult;
import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.common.utils.SxwlIpLocationService;
import com.sxwl.common.utils.SxwlJwtUtils;
import com.sxwl.redis.helper.SxwlRedisHelper;
import com.sxwl.security.captcha.SxwlCaptchaValidator;
import com.sxwl.security.config.SxwlSecurityProperties;
import com.sxwl.security.handler.SxwlAuthenticationHandler;
import com.sxwl.security.key.SxwlSM2KeyManager;
import com.sxwl.security.event.SxwlLoginSuccessEvent;
import com.sxwl.security.event.SxwlLogoutEvent;
import com.sxwl.security.model.SxwlLoginRequest;
import com.sxwl.security.model.SxwlLoginUser;
import com.sxwl.security.model.SxwlRefreshTokenRequest;
import com.sxwl.security.model.SxwlTokenPair;
import com.sxwl.security.utils.SxwlSecurityUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * {@link AuthController} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController 测试")
class AuthControllerTest {

    @Mock
    private SxwlAuthenticationHandler handler;

    @Mock
    private SxwlRedisHelper redisHelper;

    @Mock
    private SxwlSecurityProperties properties;

    @Mock
    private SxwlCaptchaValidator captchaValidator;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private SxwlPasswordAuthStrategy passwordAuthStrategy;

    @Mock
    private SxwlSmsAuthStrategy smsAuthStrategy;

    @Mock
    private SxwlSM2KeyManager keyManager;

    @Mock
    private SxwlIpLocationService ipLocationService;

    @Mock
    private HttpServletRequest httpRequest;

    private AuthController controller;

    @BeforeEach
    void setUp() {
        controller = new AuthController(handler, redisHelper, properties, captchaValidator,
                eventPublisher, passwordAuthStrategy, smsAuthStrategy, keyManager,
                Optional.of(ipLocationService));

        lenient().when(httpRequest.getHeader("User-Agent")).thenReturn(null);
        lenient().when(httpRequest.getHeader("X-Forwarded-For")).thenReturn(null);
        lenient().when(httpRequest.getHeader("X-Real-IP")).thenReturn(null);
        lenient().when(httpRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        lenient().when(httpRequest.getHeader("X-Device-Id")).thenReturn("unknown");
        lenient().when(httpRequest.getRequestURI()).thenReturn("/auth/login");
        lenient().when(httpRequest.getMethod()).thenReturn("POST");
        lenient().when(ipLocationService.getLocation(any())).thenReturn(null);
    }

    @Test
    @DisplayName("getPublicKey 应返回公钥")
    void getPublicKey_shouldReturnPublicKey() {
        SxwlPublicKeyVO publicKeyVO = new SxwlPublicKeyVO();
        publicKeyVO.setPublicKey("04abc123");
        when(keyManager.getCurrentPublicKey()).thenReturn(publicKeyVO);

        SxwlResult<SxwlPublicKeyVO> result = controller.getPublicKey();
        assertNotNull(result);
        assertEquals("04abc123", result.getData().getPublicKey());
    }

    private SxwlLoginRequest createLoginRequest() {
        SxwlLoginRequest request = new SxwlLoginRequest();
        request.setUsername("user");
        request.setPassword("enc-pass");
        request.setCaptchaUuid("uuid");
        request.setCaptchaCode("code");
        return request;
    }

    @Test
    @DisplayName("loginByPassword 验证码失败应抛出异常")
    void loginByPassword_captchaFailed_shouldThrow() {
        doThrow(new SxwlBusinessException(400, "验证码错误"))
                .when(captchaValidator).validateImageCaptcha("uuid", "code");

        SxwlLoginRequest request = createLoginRequest();
        assertThrows(SxwlBusinessException.class,
                () -> controller.loginByPassword(request, httpRequest));
    }

    @Test
    @DisplayName("loginByPassword 应成功登录")
    void loginByPassword_shouldSucceed() {
        SxwlLoginUser loginUser = new SxwlLoginUser();
        loginUser.setUserId(1L);
        loginUser.setUsername("user");

        SxwlTokenPair tokenPair = new SxwlTokenPair("access", "refresh");

        SxwlLoginRequest request = createLoginRequest();
        when(passwordAuthStrategy.authenticate(request)).thenReturn(loginUser);
        when(handler.createTokenPair(loginUser, "unknown", "admin", "127.0.0.1", null))
                .thenReturn(tokenPair);

        SxwlResult<SxwlTokenPair> result = controller.loginByPassword(request, httpRequest);

        assertNotNull(result);
        assertEquals("access", result.getData().getAccessToken());
        verify(eventPublisher).publishEvent(any(SxwlLoginSuccessEvent.class));
        verify(captchaValidator).validateImageCaptcha("uuid", "code");
    }

    @Test
    @DisplayName("refresh Token 格式错误时应抛出异常")
    void refresh_invalidToken_shouldThrow() {
        SxwlRefreshTokenRequest request = new SxwlRefreshTokenRequest();
        request.setRefreshToken("invalid-token");
        request.setDeviceId("device-1");

        when(properties.getJwtSecret()).thenReturn("secret");

        try (MockedStatic<SxwlJwtUtils> jwtUtils = mockStatic(SxwlJwtUtils.class)) {
            jwtUtils.when(() -> SxwlJwtUtils.parseClaims("invalid-token", "secret"))
                    .thenThrow(new SxwlBusinessException(400, "token 非法或已过期"));

            assertThrows(SxwlBusinessException.class,
                    () -> controller.refresh(request, httpRequest));
        }
    }

    @Test
    @DisplayName("refresh Token 类型不是 refresh 时应抛出异常")
    void refresh_wrongTokenType_shouldThrow() {
        SxwlRefreshTokenRequest request = new SxwlRefreshTokenRequest();
        request.setRefreshToken("valid-token");
        request.setDeviceId("device-1");

        when(properties.getJwtSecret()).thenReturn("secret");

        try (MockedStatic<SxwlJwtUtils> jwtUtils = mockStatic(SxwlJwtUtils.class)) {
            Claims mockClaims = mock(Claims.class);
            jwtUtils.when(() -> SxwlJwtUtils.parseClaims("valid-token", "secret"))
                    .thenReturn(mockClaims);
            jwtUtils.when(() -> SxwlJwtUtils.resolveTokenType(any()))
                    .thenReturn("access");

            SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                    () -> controller.refresh(request, httpRequest));
            assertEquals(400, ex.getCode());
        }
    }

    @Test
    @DisplayName("logout 未登录时应直接返回成功")
    void logout_notLoggedIn_shouldSucceed() {
        try (MockedStatic<SxwlSecurityUtils> securityUtils = mockStatic(SxwlSecurityUtils.class)) {
            securityUtils.when(SxwlSecurityUtils::getCurrentUser)
                    .thenReturn(Optional.empty());

            SxwlResult<Void> result = controller.logout(httpRequest);
            assertNotNull(result);
            verify(handler, never()).logout(any(), any());
        }
    }

    @Test
    @DisplayName("logout 已登录应登出并发布事件")
    void logout_loggedIn_shouldLogout() {
        SxwlLoginUser loginUser = new SxwlLoginUser();
        loginUser.setUserId(1L);
        loginUser.setUsername("user");

        try (MockedStatic<SxwlSecurityUtils> securityUtils = mockStatic(SxwlSecurityUtils.class)) {
            securityUtils.when(SxwlSecurityUtils::getCurrentUser)
                    .thenReturn(Optional.of(loginUser));

            SxwlResult<Void> result = controller.logout(httpRequest);
            assertNotNull(result);
            verify(handler).logout(eq(1L), any());
            verify(eventPublisher).publishEvent(any(SxwlLogoutEvent.class));
        }
    }
}

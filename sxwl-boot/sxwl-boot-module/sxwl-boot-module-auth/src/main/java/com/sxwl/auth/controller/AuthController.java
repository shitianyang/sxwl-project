package com.sxwl.auth.controller;

import com.sxwl.auth.strategy.SxwlPasswordAuthStrategy;
import com.sxwl.auth.strategy.SxwlSmsAuthStrategy;
import com.sxwl.common.entity.SxwlPublicKeyVO;
import com.sxwl.common.entity.SxwlResult;
import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.common.utils.SxwlIpLocationService;
import com.sxwl.common.utils.SxwlJwtUtils;
import com.sxwl.common.utils.SxwlRedisKeyUtils;
import com.sxwl.redis.helper.SxwlRedisHelper;
import com.sxwl.security.captcha.SxwlCaptchaValidator;
import com.sxwl.security.config.SxwlSecurityProperties;
import com.sxwl.security.event.SxwlLoginFailureEvent;
import com.sxwl.security.event.SxwlLoginSuccessEvent;
import com.sxwl.security.event.SxwlLogoutEvent;
import com.sxwl.security.handler.SxwlAuthenticationHandler;
import com.sxwl.security.key.SxwlSM2KeyManager;
import com.sxwl.security.model.SxwlLoginRequest;
import com.sxwl.security.model.SxwlLoginUser;
import com.sxwl.security.model.SxwlRefreshTokenRequest;
import com.sxwl.security.model.SxwlTokenPair;
import com.sxwl.security.spi.SxwlAuthenticationStrategy;
import com.sxwl.security.utils.SxwlClientTypeUtils;
import com.sxwl.security.utils.SxwlSecurityUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 认证接口控制器
 * <p>
 * 薄层胶水——只做参数校验、调用策略、签发Token、发布事件、返回结果。
 * 每种登录方式对应独立端点。
 * </p>
 *
 * @author shitianyang
 * @date 2026/7/7
 * @since 0.1.0
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final SxwlAuthenticationHandler handler;
    private final SxwlRedisHelper redisHelper;
    private final SxwlSecurityProperties properties;
    private final SxwlCaptchaValidator captchaValidator;
    private final ApplicationEventPublisher eventPublisher;
    private final SxwlPasswordAuthStrategy passwordAuthStrategy;
    private final SxwlSmsAuthStrategy smsAuthStrategy;
    private final SxwlSM2KeyManager keyManager;
    private final Optional<SxwlIpLocationService> ipLocationService;

    public AuthController(SxwlAuthenticationHandler handler,
                          SxwlRedisHelper redisHelper,
                          SxwlSecurityProperties properties,
                          SxwlCaptchaValidator captchaValidator,
                          ApplicationEventPublisher eventPublisher,
                          SxwlPasswordAuthStrategy passwordAuthStrategy,
                          SxwlSmsAuthStrategy smsAuthStrategy,
                          SxwlSM2KeyManager keyManager,
                          Optional<SxwlIpLocationService> ipLocationService) {
        this.handler = handler;
        this.redisHelper = redisHelper;
        this.properties = properties;
        this.captchaValidator = captchaValidator;
        this.eventPublisher = eventPublisher;
        this.passwordAuthStrategy = passwordAuthStrategy;
        this.smsAuthStrategy = smsAuthStrategy;
        this.keyManager = keyManager;
        this.ipLocationService = ipLocationService;
    }

    /**
     * 获取 SM2 公钥（用于前端密码加密）
     *
     * <p>前端在加载登录页时调用此接口获取公钥，代替硬编码在环境变量中。
     * 返回十六进制裸公钥（04||x||y），即 X.509 SubjectPublicKeyInfo 中的 EC 点编码。</p>
     *
     * @return 公钥十六进制字符串，未配置时抛出异常
     */
    @GetMapping("/public-key")
    public SxwlResult<SxwlPublicKeyVO> getPublicKey() {
        SxwlPublicKeyVO publicKey = keyManager.getCurrentPublicKey();
        return SxwlResult.success(publicKey);
    }

    /**
     * 密码登录
     *
     * @param request     登录请求体（username + password + captchaUuid + captchaCode）
     * @param httpRequest HTTP 请求
     * @return Token 对
     */
    @PostMapping("/login/password")
    public SxwlResult<SxwlTokenPair> loginByPassword(@Valid @RequestBody SxwlLoginRequest request,
                                                      HttpServletRequest httpRequest) {
        return doLogin(request, httpRequest, "password", passwordAuthStrategy);
    }

    /**
     * 短信登录
     */
    @PostMapping("/login/sms")
    public SxwlResult<SxwlTokenPair> loginBySms(@Valid @RequestBody SxwlLoginRequest request,
                                                 HttpServletRequest httpRequest) {
        return doLogin(request, httpRequest, "sms", smsAuthStrategy);
    }

    /**
     * 统一登录流程：验证码校验 → 认证 → 签发 Token → 发布事件
     */
    private SxwlResult<SxwlTokenPair> doLogin(SxwlLoginRequest request,
                                               HttpServletRequest httpRequest,
                                               String loginType,
                                               SxwlAuthenticationStrategy strategy) {
        String username = request.getUsername();
        String ip = getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        String location = ipLocationService.map(svc -> svc.getLocation(ip)).orElse(null);
        String failKey = SxwlRedisKeyUtils.loginFailAccountIpKey(username != null ? username : "unknown", ip);

        // 1. 校验图形验证码（每次登录必填）
        captchaValidator.validateImageCaptcha(request.getCaptchaUuid(), request.getCaptchaCode());

        // 2. 认证
        SxwlLoginUser loginUser;
        try {
            loginUser = strategy.authenticate(request);
        } catch (Exception e) {
            long newCount = redisHelper.increment(failKey, Duration.ofSeconds(properties.getLockDuration()));
            log.warn("登录失败: username={}, count={}, reason={}", username, newCount, e.getMessage());

            SxwlLoginFailureEvent failureEvent = new SxwlLoginFailureEvent();
            failureEvent.setTargetAccount(username);
            failureEvent.setIp(ip);
            failureEvent.setFailReason(e.getMessage());
            failureEvent.setFailCount((int) newCount);
            failureEvent.setTime(LocalDateTime.now());
            failureEvent.setUserAgent(userAgent);
            failureEvent.setOperateLocation(location);
            failureEvent.setBrowser(parseBrowser(userAgent));
            failureEvent.setOs(parseOs(userAgent));
            eventPublisher.publishEvent(failureEvent);
            throw e;
        }

        // 3. 成功：清除失败计数
        redisHelper.delete(failKey);

        // 4. 签发 Token
        String deviceId = httpRequest.getHeader("X-Device-Id");
        if (deviceId == null || deviceId.isBlank()) {
            deviceId = "unknown";
        }
        String clientType = SxwlClientTypeUtils.resolve(httpRequest);
        SxwlTokenPair tokenPair = handler.createTokenPair(loginUser, deviceId, clientType, ip, userAgent);

        // 5. 发布登录成功事件
        SxwlLoginSuccessEvent successEvent = new SxwlLoginSuccessEvent();
        successEvent.setUserId(loginUser.getUserId());
        successEvent.setUsername(loginUser.getUsername());
        successEvent.setIp(ip);
        successEvent.setDeviceId(deviceId);
        successEvent.setLoginType(loginType);
        successEvent.setTime(LocalDateTime.now());
        successEvent.setUserAgent(userAgent);
        successEvent.setOperateLocation(location);
        successEvent.setBrowser(parseBrowser(userAgent));
        successEvent.setOs(parseOs(userAgent));
        successEvent.setRequestUrl(httpRequest.getRequestURI());
        successEvent.setRequestMethod(httpRequest.getMethod());
        eventPublisher.publishEvent(successEvent);

        log.info("登录成功: userId={}, username={}, loginType={}", loginUser.getUserId(), username, loginType);
        return SxwlResult.success(tokenPair);
    }

    /**
     * 刷新 Token
     * <p>用 refreshToken 换取新的 access + refresh Token 对，旧 Token 同时失效。</p>
     */
    @PostMapping("/refresh")
    public SxwlResult<SxwlTokenPair> refresh(@Valid @RequestBody SxwlRefreshTokenRequest request,
                                             HttpServletRequest httpRequest) {
        String secret = properties.getJwtSecret();

        // 1. 解析 refreshToken
        Claims claims = SxwlJwtUtils.parseClaims(request.getRefreshToken(), secret);
        Long userId = SxwlJwtUtils.resolveUserId(claims);
        String tokenType = SxwlJwtUtils.resolveTokenType(claims);
        String deviceId = SxwlJwtUtils.resolveDeviceId(claims);
        String jti = SxwlJwtUtils.resolveJwtId(claims);
        String clientType = SxwlClientTypeUtils.normalize(SxwlJwtUtils.resolveClientType(claims));
        if (SxwlJwtUtils.resolveClientType(claims) == null) {
            clientType = SxwlClientTypeUtils.resolve(httpRequest);
        }

        // 2. 校验 Token 类型
        if (!SxwlJwtUtils.TOKEN_TYPE_REFRESH.equals(tokenType)) {
            throw new SxwlBusinessException(400, "仅支持 refreshToken 刷新");
        }
        if (userId == null) {
            throw new SxwlBusinessException(400, "无效的 Token");
        }

        // 3. 校验白名单
        String whiteKey = SxwlRedisKeyUtils.tokenJwtKey(clientType, userId, deviceId, jti);
        if (redisHelper.get(whiteKey).isEmpty()) {
            throw new SxwlBusinessException(401, "Token 已失效或已被吊销");
        }

        // 4. 删除旧白名单
        redisHelper.delete(whiteKey);

        // 5. 签发新 Token 对（不覆盖用户缓存）
        SxwlTokenPair tokenPair = handler.refreshTokenPair(userId, deviceId, clientType);

        log.info("Token 刷新成功: userId={}, deviceId={}", userId, deviceId);
        return SxwlResult.success(tokenPair);
    }

    /**
     * 登出
     *
     * <p>吊销当前用户所有 Token，发布登出事件。
     * 已放行至 permitAll，未登录时静默成功（Token 已失效的场景）。</p>
     */
    @PostMapping("/logout")
    public SxwlResult<Void> logout(HttpServletRequest httpRequest) {
        Optional<SxwlLoginUser> loginUserOpt = SxwlSecurityUtils.getCurrentUser();
        if (loginUserOpt.isEmpty()) {
            // 未登录或 Token 已失效，登出必然是成功的
            return SxwlResult.success();
        }

        SxwlLoginUser loginUser = loginUserOpt.get();
        handler.logout(loginUser.getUserId(), SxwlClientTypeUtils.resolve(httpRequest));

        SxwlLogoutEvent logoutEvent = new SxwlLogoutEvent();
        logoutEvent.setUserId(loginUser.getUserId());
        logoutEvent.setUsername(loginUser.getUsername());
        logoutEvent.setTime(LocalDateTime.now());
        eventPublisher.publishEvent(logoutEvent);

        log.info("登出成功: userId={}, username={}", loginUser.getUserId(), loginUser.getUsername());
        return SxwlResult.success();
    }

    /**
     * 获取客户端真实 IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // X-Forwarded-For 可能包含多个 IP，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /** 从 User-Agent 解析浏览器 */
    private String parseBrowser(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) return "未知";
        if (userAgent.contains("Edg")) return "Edge";
        if (userAgent.contains("Chrome")) return "Chrome";
        if (userAgent.contains("Firefox")) return "Firefox";
        if (userAgent.contains("Safari")) return "Safari";
        return "其他";
    }

    /** 从 User-Agent 解析操作系统 */
    private String parseOs(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) return "未知";
        if (userAgent.contains("Windows NT")) return "Windows";
        if (userAgent.contains("Mac OS X")) return "macOS";
        if (userAgent.contains("Linux") && !userAgent.contains("Android")) return "Linux";
        if (userAgent.contains("Android")) return "Android";
        if (userAgent.contains("iPhone") || userAgent.contains("iPad")) return "iOS";
        return "其他";
    }
}

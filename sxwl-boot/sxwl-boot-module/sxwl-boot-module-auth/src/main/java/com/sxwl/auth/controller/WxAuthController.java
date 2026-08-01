package com.sxwl.auth.controller;

import com.sxwl.auth.model.request.SxwlWechatLoginRequest;
import com.sxwl.auth.strategy.SxwlWechatAuthStrategy;
import com.sxwl.common.entity.SxwlResult;
import com.sxwl.common.utils.SxwlIpLocationService;
import com.sxwl.security.event.SxwlLoginSuccessEvent;
import com.sxwl.security.handler.SxwlAuthenticationHandler;
import com.sxwl.security.model.SxwlLoginUser;
import com.sxwl.security.model.SxwlTokenPair;
import com.sxwl.security.utils.SxwlClientTypeUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 微信登录 Controller（C 端小程序一键登录，首次自动注册）
 *
 * <p>薄层胶水——认证由 {@link SxwlWechatAuthStrategy} 完成，Token 签发复用统一
 * {@link SxwlAuthenticationHandler}，clientType 固定为 {@code front}（C 端）。</p>
 *
 * @author shitianyang
 * @date 2026/7/7
 * @since 0.1.0
 */
@RestController
@RequestMapping("/auth/login")
public class WxAuthController {

    private static final Logger log = LoggerFactory.getLogger(WxAuthController.class);

    private final SxwlWechatAuthStrategy wechatAuthStrategy;
    private final SxwlAuthenticationHandler handler;
    private final ApplicationEventPublisher eventPublisher;
    private final Optional<SxwlIpLocationService> ipLocationService;

    public WxAuthController(SxwlWechatAuthStrategy wechatAuthStrategy,
                            SxwlAuthenticationHandler handler,
                            ApplicationEventPublisher eventPublisher,
                            Optional<SxwlIpLocationService> ipLocationService) {
        this.wechatAuthStrategy = wechatAuthStrategy;
        this.handler = handler;
        this.eventPublisher = eventPublisher;
        this.ipLocationService = ipLocationService;
    }

    /**
     * 微信一键登录（首次自动注册）
     *
     * @param request     { code, nickname, avatarUrl }
     * @param httpRequest HTTP 请求
     * @return { token, refreshToken, userInfo }
     */
    @PostMapping("/wechat")
    public SxwlResult<Map<String, Object>> wxLogin(@Valid @RequestBody SxwlWechatLoginRequest request,
                                                   HttpServletRequest httpRequest) {
        String ip = getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        String location = ipLocationService.map(svc -> svc.getLocation(ip)).orElse(null);

        // 1. 微信认证（code2session → 查/建用户）
        SxwlLoginUser loginUser = wechatAuthStrategy.authenticate(
                request.getCode(), request.getNickname(), request.getAvatarUrl());

        // 2. 签发 C 端 Token（clientType=front，使用 C 端独立过期时间）
        String deviceId = httpRequest.getHeader("X-Device-Id");
        if (deviceId == null || deviceId.isBlank()) {
            deviceId = "unknown";
        }
        SxwlTokenPair tokenPair = handler.createTokenPair(
                loginUser, deviceId, SxwlClientTypeUtils.FRONT, ip, userAgent);

        // 3. 发布登录成功事件
        SxwlLoginSuccessEvent successEvent = new SxwlLoginSuccessEvent();
        successEvent.setUserId(loginUser.getUserId());
        successEvent.setUsername(loginUser.getUsername());
        successEvent.setIp(ip);
        successEvent.setDeviceId(deviceId);
        successEvent.setLoginType("wechat");
        successEvent.setTime(LocalDateTime.now());
        successEvent.setUserAgent(userAgent);
        successEvent.setOperateLocation(location);
        successEvent.setBrowser(parseBrowser(userAgent));
        successEvent.setOs(parseOs(userAgent));
        successEvent.setRequestUrl(httpRequest.getRequestURI());
        successEvent.setRequestMethod(httpRequest.getMethod());
        eventPublisher.publishEvent(successEvent);

        log.info("微信登录成功: userId={}", loginUser.getUserId());

        // 4. 组装返回（前端仅需 accessToken + 用户信息）
        Map<String, Object> data = new HashMap<>();
        data.put("token", tokenPair.getAccessToken());
        data.put("refreshToken", tokenPair.getRefreshToken());
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("nickname", loginUser.getNickname());
        userInfo.put("avatarUrl", request.getAvatarUrl());
        data.put("userInfo", userInfo);
        return SxwlResult.success(data);
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

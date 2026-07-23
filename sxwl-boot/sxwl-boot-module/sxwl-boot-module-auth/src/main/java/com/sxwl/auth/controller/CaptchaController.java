package com.sxwl.auth.controller;

import com.sxwl.common.utils.SxwlCaptchaUtils;
import com.sxwl.common.utils.SxwlRedisKeyUtils;
import com.sxwl.redis.helper.SxwlRedisHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

/**
 * 验证码接口控制器
 * <p>
 * 提供图形验证码的生成接口，验证码文本存入 Redis 供后续校验。
 * </p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    private static final Logger log = LoggerFactory.getLogger(CaptchaController.class);

    /** 图形验证码 Redis TTL（120 秒） */
    private static final Duration CAPTCHA_TTL = Duration.ofSeconds(120);

    private final SxwlRedisHelper redisHelper;

    public CaptchaController(SxwlRedisHelper redisHelper) {
        this.redisHelper = redisHelper;
    }

    /**
     * 获取图形验证码
     * <p>
     * 生成 4 位字母数字混合验证码，将文本存入 Redis（Key = captcha:image:{uuid}），
     * 返回 Base64 图片 URL 和 UUID，前端在登录时一并提交。
     * </p>
     *
     * @return { uuid, base64Image }
     */
    @GetMapping("/image")
    public Map<String, String> getImageCaptcha() {
        // 1. 生成验证码
        SxwlCaptchaUtils.CaptchaResult result = SxwlCaptchaUtils.generateImageCaptcha();
        String code = result.getCode();
        String base64Image = result.getBase64Image();

        // 2. 生成 UUID 并存入 Redis
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String key = SxwlRedisKeyUtils.captchaImageKey(uuid);
        redisHelper.set(key, code, CAPTCHA_TTL);

        log.debug("验证码已生成: uuid={}, code={}", uuid, code);
        return Map.of("uuid", uuid, "base64Image", base64Image);
    }
}

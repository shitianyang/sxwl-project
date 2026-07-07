package com.sxwl.security.captcha;

import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.common.utils.SxwlRedisKeyUtils;
import com.sxwl.redis.helper.SxwlRedisHelper;

import java.util.Optional;

/**
 * 验证码校验器
 * <p>
 * 从 Redis 读取验证码并与用户输入比对，验证成功后立即删除（一次性使用）。
 * </p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public class SxwlCaptchaValidator {

    private final SxwlRedisHelper redisHelper;

    public SxwlCaptchaValidator(SxwlRedisHelper redisHelper) {
        this.redisHelper = redisHelper;
    }

    /**
     * 校验图形验证码
     *
     * @param uuid       验证码 UUID（前端获取验证码时返回）
     * @param captchaCode 用户输入的验证码
     * @throws SxwlBusinessException 验证码错误或已过期时抛出
     */
    public void validateImageCaptcha(String uuid, String captchaCode) {
        if (uuid == null || captchaCode == null) {
            throw new SxwlBusinessException("验证码不能为空");
        }

        String key = SxwlRedisKeyUtils.captchaImageKey(uuid);
        Optional<String> stored = redisHelper.get(key);

        if (stored.isEmpty()) {
            throw new SxwlBusinessException("验证码已过期，请刷新");
        }

        if (!stored.get().equalsIgnoreCase(captchaCode)) {
            throw new SxwlBusinessException("验证码错误");
        }

        // 一次性使用，验证后立即删除
        redisHelper.delete(key);
    }

    /**
     * 校验短信验证码
     *
     * @param phone   手机号
     * @param smsCode 用户输入的验证码
     * @throws SxwlBusinessException 验证码错误或已过期时抛出
     */
    public void validateSmsCaptcha(String phone, String smsCode) {
        if (phone == null || smsCode == null) {
            throw new SxwlBusinessException("短信验证码不能为空");
        }

        String key = SxwlRedisKeyUtils.captchaSmsKey(phone);
        Optional<String> stored = redisHelper.get(key);

        if (stored.isEmpty()) {
            throw new SxwlBusinessException("验证码已过期，请重新发送");
        }

        if (!stored.get().equals(smsCode)) {
            throw new SxwlBusinessException("验证码错误");
        }

        redisHelper.delete(key);
    }

    /**
     * 校验邮箱验证码
     *
     * @param email     邮箱地址
     * @param emailCode 用户输入的验证码
     * @throws SxwlBusinessException 验证码错误或已过期时抛出
     */
    public void validateEmailCaptcha(String email, String emailCode) {
        if (email == null || emailCode == null) {
            throw new SxwlBusinessException("邮箱验证码不能为空");
        }

        String key = SxwlRedisKeyUtils.captchaEmailKey(email);
        Optional<String> stored = redisHelper.get(key);

        if (stored.isEmpty()) {
            throw new SxwlBusinessException("验证码已过期，请重新发送");
        }

        if (!stored.get().equals(emailCode)) {
            throw new SxwlBusinessException("验证码错误");
        }

        redisHelper.delete(key);
    }
}

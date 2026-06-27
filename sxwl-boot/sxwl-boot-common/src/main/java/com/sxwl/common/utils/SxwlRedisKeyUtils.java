package com.sxwl.common.utils;

import com.sxwl.common.constants.SxwlRedisKeyConstants;

/**
 * Redis Key构建工具类
 *
 * @author shitianyang
 * @date 2026/6/28
 * @since 0.1.0
 */
public final class SxwlRedisKeyUtils {

    /**
     * 私有构造函数，防止外部实例化工具类
     */
    private SxwlRedisKeyUtils() {
        throw new UnsupportedOperationException("SxwlRedisKeyUtils 工具类，不允许实例化");
    }

    /**
     * 构建后台 access_token 白名单 Key
     *
     * @param userId 用户 ID
     * @param jti    JWT ID（UUID，确保每次登录的 token 唯一）
     * @return 完整 Redis Key，如 "token:admin:1:abc123-def456"
     */
    public static String tokenAdminKey(long userId, String jti) {
        return SxwlRedisKeyConstants.TOKEN_ADMIN_PREFIX + userId + ":" + jti;
    }

    /**
     * 构建后台 refresh_token 白名单 Key
     */
    public static String refreshAdminKey(long userId, String jti) {
        return SxwlRedisKeyConstants.REFRESH_ADMIN_PREFIX + userId + ":" + jti;
    }

    /**
     * 构建前台 access_token 白名单 Key
     */
    public static String tokenFrontKey(long userId, String jti) {
        return SxwlRedisKeyConstants.TOKEN_FRONT_PREFIX + userId + ":" + jti;
    }

    /**
     * 构建前台 refresh_token 白名单 Key
     */
    public static String refreshFrontKey(long userId, String jti) {
        return SxwlRedisKeyConstants.REFRESH_FRONT_PREFIX + userId + ":" + jti;
    }

    /**
     * 构建在线用户信息 Key
     */
    public static String onlineUserKey(long userId) {
        return SxwlRedisKeyConstants.ONLINE_USER_PREFIX + userId;
    }

    /**
     * 构建图形验证码 Key
     */
    public static String captchaImageKey(String uuid) {
        return SxwlRedisKeyConstants.CAPTCHA_IMAGE_PREFIX + uuid;
    }

    /**
     * 构建短信验证码 Key
     */
    public static String captchaSmsKey(String phone) {
        return SxwlRedisKeyConstants.CAPTCHA_SMS_PREFIX + phone;
    }

    /**
     * 构建邮箱验证码 Key
     */
    public static String captchaEmailKey(String email) {
        return SxwlRedisKeyConstants.CAPTCHA_EMAIL_PREFIX + email;
    }

    /**
     * 构建验证码发送间隔限制 Key
     */
    public static String captchaLimitKey(String target) {
        return SxwlRedisKeyConstants.CAPTCHA_LIMIT_PREFIX + target;
    }

    /**
     * 构建登录失败计数 Key
     */
    public static String loginFailCountKey(String ip) {
        return SxwlRedisKeyConstants.LOGIN_FAIL_COUNT_PREFIX + ip;
    }

    /**
     * 构建 IP 封禁 Key
     */
    public static String loginBlockKey(String ip) {
        return SxwlRedisKeyConstants.LOGIN_BLOCK_PREFIX + ip;
    }

    /**
     * 构建字典缓存 Key
     */
    public static String dictCacheKey(String dictType) {
        return SxwlRedisKeyConstants.DICT_CACHE_PREFIX + dictType;
    }

    /**
     * 构建防重复提交 Key
     */
    public static String repeatSubmitKey(long userId, String uri) {
        return SxwlRedisKeyConstants.REPEAT_SUBMIT_PREFIX + userId + ":" + uri;
    }

    /**
     * 构建分布式锁 Key
     */
    public static String lockKey(String bizKey) {
        return SxwlRedisKeyConstants.LOCK_PREFIX + bizKey;
    }
}

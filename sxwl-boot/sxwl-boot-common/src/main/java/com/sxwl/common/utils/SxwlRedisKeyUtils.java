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
     * 构建在线用户信息 Key（含设备维度，设计文档 8.1 节新版格式）
     * <p>完整 Key：online:user:{userId}:{deviceId}</p>
     */
    public static String onlineUserDeviceKey(long userId, String deviceId) {
        return SxwlRedisKeyConstants.ONLINE_USER_PREFIX + userId + ":" + deviceId;
    }

    /**
     * 构建在线用户信息 Key（旧版格式，不区分设备）
     * @deprecated 请使用 {@link #onlineUserDeviceKey(long, String)} 获取设备级别信息
     */
    @Deprecated
    public static String onlineUserKey(long userId) {
        return SxwlRedisKeyConstants.ONLINE_USER_PREFIX + userId;
    }

    /**
     * 构建在线设备辅助 Set Key
     * <p>完整 Key：online:devices:{userId}，用于精确批量删除在线设备</p>
     */
    public static String onlineDevicesSetKey(long userId) {
        return SxwlRedisKeyConstants.ONLINE_DEVICES_PREFIX + userId;
    }

    /**
     * 构建 Token 辅助索引 Set Key（用于批量吊销）
     * <p>完整 Key：token:user:{clientType}:{userId}，Members = 该用户所有 jti</p>
     */
    public static String tokenUserSetKey(String clientType, long userId) {
        return SxwlRedisKeyConstants.TOKEN_USER_PREFIX + clientType + ":" + userId;
    }

    /**
     * 构建用户信息缓存 Key
     * <p>完整 Key：token:info:{userId}，Hash 存储用户名/角色/权限/数据范围</p>
     */
    public static String tokenInfoKey(long userId) {
        return SxwlRedisKeyConstants.TOKEN_INFO_PREFIX + userId;
    }

    /**
     * 构建 Token 白名单 Key（新版格式，含设备维度）
     * <p>完整 Key：token:jwt:{clientType}:{userId}:{deviceId}:{jti}</p>
     */
    public static String tokenJwtKey(String clientType, long userId, String deviceId, String jti) {
        return SxwlRedisKeyConstants.TOKEN_JWT_PREFIX + clientType + ":" + userId + ":" + deviceId + ":" + jti;
    }

    /**
     * 构建登录失败全局计数 Key
     * <p>完整 Key：login:count:{targetAccount}，不限 IP</p>
     */
    public static String loginCountKey(String targetAccount) {
        return SxwlRedisKeyConstants.LOGIN_COUNT_PREFIX + targetAccount;
    }

    /**
     * 构建登录失败账号+IP 维度计数 Key
     * <p>完整 Key：login:fail:{targetAccount}:{ip}，触发验证码维度</p>
     */
    public static String loginFailAccountIpKey(String targetAccount, String ip) {
        return SxwlRedisKeyConstants.LOGIN_FAIL_COUNT_PREFIX + targetAccount + ":" + ip;
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

package com.sxwl.common.constants;

/**
 * Redis Key 前缀常量
 *
 * <p>集中管理所有 Redis Key 的前缀和构建逻辑，避免散落各处导致：
 * <ul>
 *   <li>Key 命名不一致（如 token:admin:1:xxx vs admin_token_1_xxx）</li>
 *   <li>Key 冲突（不同模块用了相同的 Key 格式）</li>
 *   <li>维护困难（想改 Key 格式需要全局搜索替换）</li>
 * </ul>
 * </p>
 *
 * <h3>Key 命名规范</h3>
 * <pre>
 * 格式：{业务域}:{子域}:{唯一标识}
 * 示例：token:admin:1:abc123  （后台用户 ID=1 的 access_token）
 *       captcha:image:550e8400 （UUID 为 550e8400 的图形验证码）
 * </pre>
 *
 * @author shitianyang
 * @date 2026/6/28
 * @since 0.1.0
 */
public final class SxwlRedisKeyConstants {

    /**
     * 私有构造函数，防止外部实例化工具类
     */
    private SxwlRedisKeyConstants() {
        throw new UnsupportedOperationException("SxwlRedisKeyConstants 是常量工具类，不允许实例化");
    }

    /**
     * 后台 access_token 白名单 Key 前缀
     * <p>完整 Key：token:admin:{userId}:{jti}
     * <br>Value：userId（String 类型）
     * <br>TTL：与 access_token 过期时间一致（30 分钟）
     * <br>用途：JwtAuthenticationFilter 验证 token 是否在白名单中</p>
     */
    public static final String TOKEN_ADMIN_PREFIX = "token:admin:";

    /**
     * 后台 refresh_token 白名单 Key 前缀
     * <p>完整 Key：refresh:admin:{userId}:{jti}
     * <br>TTL：与 refresh_token 过期时间一致（7 天）</p>
     */
    public static final String REFRESH_ADMIN_PREFIX = "refresh:admin:";

    // ==================== 前台 C 端认证域 ====================

    /**
     * 前台 access_token 白名单 Key 前缀
     * <p>完整 Key：token:front:{userId}:{jti}
     * <br>与后台使用不同的前缀和 JWT secret，确保前后台认证域隔离</p>
     */
    public static final String TOKEN_FRONT_PREFIX = "token:front:";

    /**
     * 前台 refresh_token 白名单 Key 前缀
     * <p>完整 Key：refresh:front:{userId}:{jti}
     * <br>TTL：30 天（C 端体验优先）</p>
     */
    public static final String REFRESH_FRONT_PREFIX = "refresh:front:";

    // ==================== Token 辅助索引与缓存 ====================

    /**
     * Token 辅助索引 Key 前缀（用于高效批量吊销）
     * <p>完整 Key：token:user:{clientType}:{userId}
     * <br>数据结构：Set（Members = 该用户所有 jti，access + refresh 不区分设备）
     * <br>用途：修改密码/角色禁用/强制踢人时，SMEMBERS 拿到所有 jti 后批量 UNLINK 白名单 Key
     * <br>TTL：无，随最后一个 jti 删除时清空</p>
     */
    public static final String TOKEN_USER_PREFIX = "token:user:";

    /**
     * 用户信息缓存 Key 前缀
     * <p>完整 Key：token:info:{userId}
     * <br>数据结构：Hash（字段：userId, username, nickname, roles, permissions, dataScope, dataScopeOrgIds）
     * <br>TTL：与 Refresh Token 过期时间一致（B 端 7 天 / C 端 30 天）
     * <br>用途：JwtAuthenticationFilter 从 Redis 读取角色/权限，避免每次请求查 DB</p>
     */
    public static final String TOKEN_INFO_PREFIX = "token:info:";

    // ==================== Token 白名单（新版设计） ====================

    /**
     * Token 白名单 Key 前缀（含设备维度，设计文档 3.4 节新版格式）
     * <p>完整 Key：token:jwt:{clientType}:{userId}:{deviceId}:{jti}
     * <br>数据结构：String（value = "access" 或 "refresh"）
     * <br>TTL：= 对应 Token 的剩余有效期
     * <br>注意：此为新版统一格式，旧版 TOKEN_ADMIN_PREFIX / TOKEN_FRONT_PREFIX 保留兼容，
     * 逐步迁移至 token:jwt:* 格式</p>
     */
    public static final String TOKEN_JWT_PREFIX = "token:jwt:";

    // ==================== 在线用户（辅助索引） ====================

    /**
     * 在线设备辅助 Set Key 前缀
     * <p>完整 Key：online:devices:{userId}
     * <br>数据结构：Set（Members = 该用户当前所有 deviceId）
     * <br>用途：强制踢人时先 SMEMBERS 获取所有 deviceId，再逐个 DEL 对应 Key，
     * 比 SCAN 通配符更精确高效</p>
     */
    public static final String ONLINE_DEVICES_PREFIX = "online:devices:";

    // ==================== 登录风控（多维度） ====================

    /**
     * 登录失败全局计数 Key 前缀（不限 IP）
     * <p>完整 Key：login:count:{targetAccount}
     * <br>Value：失败次数（Integer）
     * <br>TTL：30 分钟
     * <br>用途：按账号维度统计登录失败次数，与 IP 无关</p>
     */
    public static final String LOGIN_COUNT_PREFIX = "login:count:";

    // ==================== 在线用户 ====================

    /**
     * 在线用户信息 Key 前缀
     * <p>完整 Key：online:user:{userId}
     * <br>数据结构：Hash（字段：userId, username, ip, browser, os, loginTime）
     * <br>用途：在线用户列表、强制踢人时删除对应 token 白名单</p>
     */
    public static final String ONLINE_USER_PREFIX = "online:user:";

    /**
     * 在线用户集合 Key
     * <p>数据结构：Set（存储所有在线用户的 userId）
     * <br>用途：快速统计在线人数、清理过期在线用户</p>
     */
    public static final String ONLINE_USERS_KEY = "online:users";

    // ==================== 验证码 ====================

    /**
     * 图形验证码 Key 前缀
     * <p>完整 Key：captcha:image:{uuid}
     * <br>Value：4 位字母数字混合验证码文本
     * <br>TTL：120 秒，一次性使用（验证后立即删除）</p>
     */
    public static final String CAPTCHA_IMAGE_PREFIX = "captcha:image:";

    /**
     * 短信验证码 Key 前缀
     * <p>完整 Key：captcha:sms:{phone}
     * <br>Value：6 位数字验证码
     * <br>TTL：300 秒（5 分钟）</p>
     */
    public static final String CAPTCHA_SMS_PREFIX = "captcha:sms:";

    /**
     * 邮箱验证码 Key 前缀
     * <p>完整 Key：captcha:email:{email}
     * <br>Value：6 位数字验证码
     * <br>TTL：300 秒（5 分钟）</p>
     */
    public static final String CAPTCHA_EMAIL_PREFIX = "captcha:email:";

    /**
     * 短信/邮箱发送间隔限制 Key 前缀
     * <p>完整 Key：captcha:limit:{phoneOrEmail}
     * <br>TTL：60 秒，存在则拒绝发送</p>
     */
    public static final String CAPTCHA_LIMIT_PREFIX = "captcha:limit:";

    // ==================== 限流与风控 ====================

    /**
     * 登录失败计数 Key 前缀
     * <p>完整 Key：login:fail:{ip}
     * <br>Value：失败次数（Integer）
     * <br>TTL：30 分钟
     * <br>用途：同一 IP 连续失败 N 次后触发验证码或封禁</p>
     */
    public static final String LOGIN_FAIL_COUNT_PREFIX = "login:fail:";

    /**
     * IP 封禁 Key 前缀
     * <p>完整 Key：login:block:{ip}
     * <br>Value：封禁原因（如 "连续登录失败 10 次"）
     * <br>TTL：30 分钟</p>
     */
    public static final String LOGIN_BLOCK_PREFIX = "login:block:";

    // ==================== 缓存 ====================

    /**
     * 字典数据缓存 Key 前缀
     * <p>完整 Key：dict:{dictType}
     * <br>Value：该字典类型下的所有字典数据 JSON 数组
     * <br>TTL：1 小时（新增/修改/删除字典数据时主动刷新）</p>
     */
    public static final String DICT_CACHE_PREFIX = "dict:";

    /**
     * 防重复提交 Key 前缀
     * <p>完整 Key：repeat:{userId}:{uri}
     * <br>TTL：3 秒
     * <br>用途：@SxwlRepeatSubmit 注解的底层实现</p>
     */
    public static final String REPEAT_SUBMIT_PREFIX = "repeat:";

    // ==================== 分布式锁 ====================

    /**
     * 分布式锁 Key 前缀
     * <p>完整 Key：lock:{业务标识}
     * <br>用途：定时任务互斥、并发操作控制</p>
     */
    public static final String LOCK_PREFIX = "lock:";

    // ==================== 系统参数配置缓存 ====================

    /**
     * 系统参数配置缓存 Key 前缀
     * <p>完整 Key：config:{configKey}
     * <br>Value：参数值（String）
     * <br>TTL：1 小时
     * <br>用途：SxwlConfigHelper 的 Redis 缓存层，减少 DB 查询</p>
     */
    public static final String CONFIG_CACHE_PREFIX = "config:";
}

package com.sxwl.common.constants;

/**
 * 全局常量
 *
 * <p>集中管理项目中所有硬编码的数值、字符串、时间参数，
 * 避免散落各处导致的维护困难和魔法数字问题。</p>
 *
 * @author shitianyang
 * @date 2026/6/28
 * @since 0.1.0
 */
public final class SxwlSystemConstants {

    /**
     * 私有构造函数，防止外部实例化工具类
     */
    private SxwlSystemConstants() {
        throw new UnsupportedOperationException("SxwlSystemConstants 是常量工具类，不允许实例化");
    }

    /**
     * 默认序列化版本号：2025-03-24 08:00:00
     * <p>减小生成的 ID 数值长度</p>
     */
    public static final long SERIAL_VERSION_UID = 1742774400000L;

    /**
     * 标准日期时间格式：yyyy-MM-dd HH:mm:ss
     * <p>用于 API 响应中的时间字段、数据库 timestamp 格式化</p>
     */
    public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 短日期格式：yyyy-MM-dd
     * <p>用于日期选择器、报表日期范围等场景</p>
     */
    public static final String DATE_PATTERN_SHORT = "yyyy-MM-dd";

    /**
     * 默认时区
     */
    public static final String DEFAULT_TIME_ZONE = "Asia/Shanghai";

    /**
     * 默认页码
     * <p>所有分页查询接口的 pageNum 默认值，与前端 SxwlPagination 组件对齐</p>
     */
    public static final int DEFAULT_PAGE_NUM = 1;

    /**
     * 默认每页条数
     * <p>所有分页查询接口的 pageSize 默认值</p>
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 每页最大数量
     */
    public static final int MAX_PAGE_SIZE = 200;

    /**
     * 后台管理端 access_token 过期时间：30 分钟
     * <p>后台管理安全性优先，较短过期时间降低 token 泄露风险</p>
     */
    public static final long ACCESS_TOKEN_EXPIRE = 30 * 60 * 1000L;

    /**
     * 后台管理端 refresh_token 过期时间：7 天
     * <p>一周内免登录，兼顾安全与便利</p>
     */
    public static final long REFRESH_TOKEN_EXPIRE = 7 * 24 * 60 * 60 * 1000L;

    /**
     * 前台 C 端 access_token 过期时间：2 小时
     * <p>C 端用户体验优先，较长过期时间避免频繁弹登录</p>
     */
    public static final long FRONT_ACCESS_TOKEN_EXPIRE = 2 * 60 * 60 * 1000L;

    /**
     * 前台 C 端 refresh_token 过期时间：30 天
     * <p>移动端常用策略：一个月免登录</p>
     */
    public static final long FRONT_REFRESH_TOKEN_EXPIRE = 30 * 24 * 60 * 60 * 1000L;

    /**
     * Token 自动续期阈值：剩余有效期小于 5 分钟时自动刷新
     * <p>由 JwtAuthenticationFilter 在每次请求时检查，避免用户操作到一半 token 过期</p>
     */
    public static final long TOKEN_REFRESH_THRESHOLD = 5 * 60 * 1000L;

    /**
     * 历史密码不可重复次数
     * <p>修改密码时校验最近 N 次密码，防止用户循环使用旧密码</p>
     */
    public static final int PASSWORD_HISTORY_COUNT = 5;

    /**
     * 默认密码过期天数：90 天
     * <p>超期后强制用户修改密码</p>
     */
    public static final int PASSWORD_EXPIRE_DAYS = 90;

    /**
     * 密码过期前提醒天数：7 天
     * <p>定时任务 passwordExpireWarn 在过期前 N 天发送站内信提醒</p>
     */
    public static final int PASSWORD_EXPIRE_WARN_DAYS = 7;

    /**
     * 连续登录失败最大次数
     * <p>超过此次数后账号自动锁定，需管理员手动解锁</p>
     */
    public static final int LOGIN_FAIL_MAX_COUNT = 5;

    /**
     * 账号锁定时间（分钟）
     * <p>连续失败达到上限后的自动锁定时长</p>
     */
    public static final int LOGIN_LOCK_MINUTES = 30;

    /**
     * IP 封禁阈值：同一 IP 登录失败次数达到此值后触发验证码
     */
    public static final int CAPTCHA_TRIGGER_FAIL_COUNT = 3;

    /**
     * IP 封禁阈值：同一 IP 登录失败次数达到此值后封禁 30 分钟
     */
    public static final int IP_BLOCK_FAIL_COUNT = 10;

    /**
     * 操作日志保留天数：180 天
     * <p>定时任务 cleanOperLog 每天凌晨 3 点清理超期日志</p>
     */
    public static final int OPER_LOG_RETAIN_DAYS = 180;

    /**
     * 登录日志保留天数：90 天
     * <p>定时任务 cleanLoginLog 每天凌晨 3 点清理超期日志</p>
     */
    public static final int LOGIN_LOG_RETAIN_DAYS = 90;

    /**
     * 日志批量清理每批最大条数
     * <p>分批删除避免长事务锁表</p>
     */
    public static final int LOG_CLEAN_BATCH_SIZE = 10000;

    /**
     * 图形验证码有效期：120 秒
     */
    public static final long CAPTCHA_IMAGE_TTL = 120L;

    /**
     * 短信/邮箱验证码有效期：300 秒（5 分钟）
     */
    public static final long CAPTCHA_SMS_TTL = 300L;

    /**
     * 短信/邮箱验证码发送间隔：60 秒
     * <p>防止恶意刷短信接口</p>
     */
    public static final long CAPTCHA_SEND_INTERVAL = 60L;

    /**
     * 雪花算法起始时间戳：2025-03-24 08:00:00
     * <p>减小生成的 ID 数值长度</p>
     */
    public static final long SNOWFLAKE_START_TIMESTAMP = 1742774400000L;

    /**
     * 超级管理员角色标识
     * <p>用于代码中判断是否为超管，进行特殊权限处理</p>
     */
    public static final String ADMIN_ROLE_CODE = "superadmin";

    /**
     * 默认超管用户名
     */
    public static final String DEFAULT_ADMIN_USERNAME = "superadmin";
}

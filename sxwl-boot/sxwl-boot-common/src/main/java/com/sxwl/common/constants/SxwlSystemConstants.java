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
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 短日期格式：yyyy-MM-dd
     * <p>用于日期选择器、报表日期范围等场景</p>
     */
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * 时间格式：HH:mm:ss
     * <p>用于仅显示时间的场景</p>
     */
    public static final String TIME_PATTERN = "HH:mm:ss";

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
     * 后台管理端 access_token 过期时间：30 分钟（1800 秒）
     * <p>后台管理安全性优先，较短过期时间降低 token 泄露风险</p>
     * <p><b>注意：本常量单位是毫秒。</b>传给 {@code SxwlJwtUtils.Builder#expireSeconds} 前需除以 1000</p>
     */
    public static final long ACCESS_TOKEN_EXPIRE = 30 * 60 * 1000L;

    /**
     * 后台管理端 refresh_token 过期时间：7 天（604800 秒）
     * <p>一周内免登录，兼顾安全与便利</p>
     * <p><b>注意：本常量单位是毫秒。</b>传给 {@code SxwlJwtUtils.Builder#expireSeconds} 前需除以 1000</p>
     */
    public static final long REFRESH_TOKEN_EXPIRE = 7 * 24 * 60 * 60 * 1000L;

    /**
     * 前台 C 端 access_token 过期时间：2 小时（7200 秒）
     * <p>C 端用户体验优先，较长过期时间避免频繁弹登录</p>
     * <p><b>注意：本常量单位是毫秒。</b>传给 {@code SxwlJwtUtils.Builder#expireSeconds} 前需除以 1000</p>
     */
    public static final long FRONT_ACCESS_TOKEN_EXPIRE = 2 * 60 * 60 * 1000L;

    /**
     * 前台 C 端 refresh_token 过期时间：30 天（2592000 秒）
     * <p>移动端常用策略：一个月免登录</p>
     * <p><b>注意：本常量单位是毫秒。</b>传给 {@code SxwlJwtUtils.Builder#expireSeconds} 前需除以 1000</p>
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
    public static final String ADMIN_ROLE_CODE = "super_admin";

    /**
     * 默认超管用户名
     */
    public static final String ADMIN_USERNAME = "SuperAdmin";

    // ==================== 日志类型常量 ====================

    /**
     * 登录日志类型
     * <p>用于 SxwlLog 注解的 logType 字段，记录用户登录行为</p>
     */
    public static final int LOG_TYPE_LOGIN = 1;

    /**
     * 操作日志类型
     * <p>用于 SxwlLog 注解的 logType 字段，记录用户操作行为（默认类型）</p>
     */
    public static final int LOG_TYPE_OPERATION = 2;

    /**
     * 异常日志类型
     * <p>用于 SxwlLog 注解的 logType 字段，记录系统异常信息</p>
     */
    public static final int LOG_TYPE_EXCEPTION = 3;

    /**
     * 安全日志类型
     * <p>用于 SxwlLog 注解的 logType 字段，记录安全相关事件（如密码修改、权限变更）</p>
     */
    public static final int LOG_TYPE_SECURITY = 4;

    // ==================== 数据权限范围常量 ====================

    /**
     * 数据权限范围：全部数据
     * <p>角色拥有所有数据的访问权限，不限制组织范围</p>
     */
    public static final int DATA_SCOPE_ALL = 1;

    /**
     * 数据权限范围：本组织数据
     * <p>角色只能访问当前组织的数据</p>
     */
    public static final int DATA_SCOPE_SELF_ORG = 2;

    /**
     * 数据权限范围：本组织和下级组织数据
     * <p>角色可以访问当前组织及其所有下级组织的数据</p>
     */
    public static final int DATA_SCOPE_SELF_AND_SUB_ORG = 3;

    /**
     * 数据权限范围：本部门和本部门下级组织数据
     * <p>角色可以访问所属部门及其挂载的下级组织的数据</p>
     */
    public static final int DATA_SCOPE_DEPT_AND_SUB_ORG = 4;

    /**
     * 数据权限范围：仅本人数据
     * <p>角色只能访问自己创建的数据</p>
     */
    public static final int DATA_SCOPE_ONLY_SELF = 5;

    // ==================== 用户状态常量 ====================

    /**
     * 用户状态：正常
     * <p>用户可以正常登录和使用系统</p>
     */
    public static final int USER_STATUS_NORMAL = 0;

    /**
     * 用户状态：禁用
     * <p>用户被管理员禁用，无法登录系统</p>
     */
    public static final int USER_STATUS_DISABLED = 1;

    // ==================== 性别常量 ====================

    /**
     * 性别：男
     */
    public static final int GENDER_MALE = 1;

    /**
     * 性别：女
     */
    public static final int GENDER_FEMALE = 0;

    /**
     * 性别：未知
     */
    public static final int GENDER_UNKNOWN = 2;

    // ==================== 菜单类型常量 ====================

    /**
     * 菜单类型：目录
     * <p>一级或二级菜单，包含子菜单</p>
     */
    public static final int MENU_TYPE_DIRECTORY = 1;

    /**
     * 菜单类型：菜单
     * <p>具体的页面或功能模块</p>
     */
    public static final int MENU_TYPE_MENU = 2;

    /**
     * 菜单类型：按钮
     * <p>页面上的操作按钮，用于权限控制</p>
     */
    public static final int MENU_TYPE_BUTTON = 3;

    // ==================== 任务状态常量 ====================

    /**
     * 定时任务状态：暂停
     * <p>任务不会被 Quartz 调度执行</p>
     */
    public static final int JOB_STATUS_PAUSE = 0;

    /**
     * 定时任务状态：运行中
     * <p>任务正常被 Quartz 调度执行</p>
     */
    public static final int JOB_STATUS_RUNNING = 1;

    // ==================== 通知类型常量 ====================

    /**
     * 通知公告类型：公告
     * <p>面向全体用户的系统公告</p>
     */
    public static final int NOTICE_TYPE_ANNOUNCEMENT = 1;

    /**
     * 通知公告类型：消息
     * <p>针对特定用户或角色的业务消息</p>
     */
    public static final int NOTICE_TYPE_MESSAGE = 2;

    // ==================== 备份类型常量 ====================

    /**
     * 数据备份类型：手动备份
     * <p>由管理员手动触发的数据备份</p>
     */
    public static final int BACKUP_TYPE_MANUAL = 1;

    /**
     * 数据备份类型：自动备份
     * <p>由定时任务自动触发的数据备份</p>
     */
    public static final int BACKUP_TYPE_AUTO = 2;

    // ==================== 文件管理常量 ====================

    /**
     * 文件上传最大大小：100 MB
     * <p>单个文件的上传大小限制</p>
     */
    public static final long FILE_UPLOAD_MAX_SIZE = 100 * 1024 * 1024L;

    /**
     * 允许的文件扩展名：图片
     */
    public static final String[] FILE_ALLOW_EXTENSIONS_IMAGE = new String[]{".jpg", ".jpeg", ".png", ".gif", ".webp"};

    /**
     * 允许的文件扩展名：文档
     */
    public static final String[] FILE_ALLOW_EXTENSIONS_DOCUMENT = new String[]{".doc", ".docx", ".pdf", ".txt", ".xls", ".xlsx"};

    /**
     * 允许的文件扩展名：所有
     */
    public static final String[] FILE_ALLOW_EXTENSIONS_ALL = new String[]{
            ".jpg", ".jpeg", ".png", ".gif", ".webp",
            ".doc", ".docx", ".pdf", ".txt", ".xls", ".xlsx",
            ".zip", ".rar", ".7z"
    };

    // ==================== 文件大小单位常量 ====================

    /**
     * 文件大小单位：字节（Byte）
     */
    public static final long FILE_SIZE_BYTE = 1L;

    /**
     * 文件大小单位：千字节（KB）
     */
    public static final long FILE_SIZE_KB = FILE_SIZE_BYTE * 1024;

    /**
     * 文件大小单位：兆字节（MB）
     */
    public static final long FILE_SIZE_MB = FILE_SIZE_KB * 1024;

    /**
     * 文件大小单位：吉字节（GB）
     */
    public static final long FILE_SIZE_GB = FILE_SIZE_MB * 1024;

    /**
     * 文件大小单位：太字节（TB）
     */
    public static final long FILE_SIZE_TB = FILE_SIZE_GB * 1024;

    // ==================== 数据脱敏常量 ====================

    /**
     * 默认脱敏字符
     * <p>用于手机号、身份证、邮箱等敏感信息的格式化隐藏</p>
     */
    public static final String MASK_CHARACTER = "*";

    /**
     * 手机号脱敏掩码（4 个星号）
     * <p>用于中间位替换，如 138****5678</p>
     */
    public static final String MASK_MOBILE = "****";

    /**
     * 身份证号脱敏掩码（6 个星号）
     * <p>用于中间位替换，如 110101******123</p>
     */
    public static final String MASK_ID_CARD = "******";

    // ==================== 星期显示常量 ====================

    /**
     * 星期数组（周日到周六）
     * <p>用于日期查询结果的中文星期显示</p>
     */
    public static final String[] WEEK_DAYS = {
            "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"
    };

    // ==================== 文件 MIME 类型扩展名映射 ====================

    /**
     * 图片文件扩展名
     */
    public static final String[] MIME_TYPE_IMAGE_EXTENSIONS = new String[]{
            "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg"
    };

    /**
     * PDF 文件扩展名
     */
    public static final String[] MIME_TYPE_PDF_EXTENSIONS = new String[]{
            "pdf"
    };

    /**
     * Word 文档扩展名
     */
    public static final String[] MIME_TYPE_WORD_EXTENSIONS = new String[]{
            "doc", "docx"
    };

    /**
     * Excel 表格扩展名
     */
    public static final String[] MIME_TYPE_EXCEL_EXTENSIONS = new String[]{
            "xls", "xlsx"
    };

    /**
     * PowerPoint 演示文稿扩展名
     */
    public static final String[] MIME_TYPE_POWERPOINT_EXTENSIONS = new String[]{
            "ppt", "pptx"
    };

    /**
     * 视频文件扩展名
     */
    public static final String[] MIME_TYPE_VIDEO_EXTENSIONS = new String[]{
            "mp4", "avi", "mov", "wmv", "flv"
    };

    /**
     * 音频文件扩展名
     */
    public static final String[] MIME_TYPE_AUDIO_EXTENSIONS = new String[]{
            "mp3", "wav", "flac", "aac"
    };

    // ==================== 国密算法常量 ====================

    /**
     * SM2 椭圆曲线名称
     * <p>用于 SM2 非对称加密的密钥对生成</p>
     */
    public static final String SM2_CURVE_NAME = "sm2p256v1";

    /**
     * SM4 密钥长度（字节）
     * <p>SM4 对称加密固定使用 16 字节（128 位）密钥</p>
     */
    public static final int SM4_KEY_LENGTH = 16;

    /**
     * SM4 IV 长度（字节）
     * <p>SM4 CBC 模式初始化向量长度，与密钥长度相同</p>
     */
    public static final int SM4_IV_LENGTH = 16;

    // ==================== 验证码常量 ====================

    /**
     * 图形验证码字符集（数字 + 大写字母，去掉 0/O/1/I 易混淆字符）
     * <p>用于图像验证码生成的随机字符选择</p>
     */
    public static final String CAPTCHA_IMAGE_CHAR_POOL = "23456789ABCDEFGHJKMNPQRSTUVWXYZ";

    /**
     * 短信/邮箱验证码字符集（纯数字，去掉 0 避免首位为 0 时前端展示异常）
     * <p>用于短信和邮箱验证码生成的随机字符选择</p>
     */
    public static final String CAPTCHA_NUMERIC_CHAR_POOL = "123456789";

    /**
     * 图形验证码文字颜色（深灰 #333/#444 + 品牌橙 #F2711C 系）
     * <p>随机选用，保证浅底可读性</p>
     */
    public static final int[][] CAPTCHA_TEXT_COLORS_RGB = {
            {0x33, 0x33, 0x33},
            {0x44, 0x44, 0x44},
            {0xF2, 0x71, 0x1C},
            {0xC9, 0x5E, 0x0A}
    };

    // ==================== 雪花算法常量 ====================

    /**
     * 数据中心位数
     * <p>默认 5 位，支持最多 31 个数据中心</p>
     */
    public static final long SNOWFLAKE_DATA_CENTER_ID_BITS = 5L;

    /**
     * 机器节点位数
     * <p>默认 5 位，支持最多 31 台机器节点</p>
     */
    public static final long SNOWFLAKE_WORKER_ID_BITS = 5L;

    /**
     * 序列号位数
     * <p>默认 12 位，支持每毫秒最多 4096 个 ID</p>
     */
    public static final long SNOWFLAKE_SEQUENCE_BITS = 12L;

    /**
     * 数据中心最大值
     * <p>计算公式：~(-1L << DATA_CENTER_ID_BITS) = 31</p>
     */
    public static final long SNOWFLAKE_MAX_DATA_CENTER_ID = ~(-1L << SNOWFLAKE_DATA_CENTER_ID_BITS);

    /**
     * 机器节点最大值
     * <p>计算公式：~(-1L << WORKER_ID_BITS) = 31</p>
     */
    public static final long SNOWFLAKE_MAX_WORKER_ID = ~(-1L << SNOWFLAKE_WORKER_ID_BITS);

    /**
     * 序列号掩码
     * <p>计算公式：~(-1L << SEQUENCE_BITS) = 4095</p>
     */
    public static final long SNOWFLAKE_SEQUENCE_MASK = ~(-1L << SNOWFLAKE_SEQUENCE_BITS);

    /**
     * 机器节点左移位数
     * <p>等于序列号位数：12</p>
     */
    public static final long SNOWFLAKE_WORKER_ID_SHIFT = SNOWFLAKE_SEQUENCE_BITS;

    /**
     * 数据中心左移位数
     * <p>等于序列号位数 + 机器节点位数：17</p>
     */
    public static final long SNOWFLAKE_DATA_CENTER_ID_SHIFT = SNOWFLAKE_SEQUENCE_BITS + SNOWFLAKE_WORKER_ID_BITS;

    /**
     * 时间戳左移位数
     * <p>等于序列号位数 + 机器节点位数 + 数据中心位数：22</p>
     */
    public static final long SNOWFLAKE_TIMESTAMP_SHIFT = SNOWFLAKE_SEQUENCE_BITS + SNOWFLAKE_WORKER_ID_BITS + SNOWFLAKE_DATA_CENTER_ID_BITS;

    /**
     * 允许的最大回拨毫秒数
     * <p>超过此阈值将抛出异常，防止时钟回拨导致 ID 重复</p>
     */
    public static final long SNOWFLAKE_MAX_BACKWARD_MS = 5L;

    // ==================== JWT Secret 常量 ====================

    /**
     * JWT Secret 生成时使用的随机字节长度
     * <p>默认 32 字节（256 位），经过 SM3 哈希后生成 64 位十六进制字符串</p>
     */
    public static final int JWT_SECRET_RANDOM_BYTE_LENGTH = 32;
}

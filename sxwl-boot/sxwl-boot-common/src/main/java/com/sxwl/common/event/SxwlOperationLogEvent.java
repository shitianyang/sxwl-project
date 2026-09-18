package com.sxwl.common.event;

import com.sxwl.common.constants.SxwlSystemConstants;

import java.io.Serializable;

/**
 * 操作日志事件
 *
 * <p>由 web 模块的 {@code SxwlLogAspect} 切面在方法执行后发布，
 * 由 module-system 的 {@code SxwlLogEventListener} 异步消费并写入 {@code sys_log_info} 表。</p>
 *
 * <h3>字段映射关系</h3>
 * <ul>
 *   <li>{@code title}, {@code logType}, {@code description} → 日志标题、类型、描述</li>
 *   <li>{@code method}, {@code requestUrl}, {@code requestMethod}, {@code requestParam} → 请求信息</li>
 *   <li>{@code userId}, {@code userName}, {@code operateIp}, {@code browser}, {@code os} → 用户信息</li>
 *   <li>{@code status}, {@code errorMsg}, {@code executeTime} → 执行状态</li>
 *   <li>{@code diff} → 字段级变更差异（JSON 数组格式）</li>
 *   <li>{@code traceId}, {@code userAgent} → 链路追踪与客户端信息</li>
 * </ul>
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * // AOP 切面自动发布
 * new SxwlOperationLogEvent()
 *     .title("用户管理")
 *     .logType(SxwlSystemConstants.LOG_TYPE_OPERATION)
 *     .description("删除用户[#{#id}]")
 *     .userId(loginUser.getUserId())
 *     .userName(loginUser.getUsername())
 *     .executeTime(endTime - startTime)
 *     .status(1)
 *     .traceId(MDC.get("traceId"))
 *     .publish();
 * }</pre>
 *
 * <h3>注意事项</h3>
 * <ul>
 *   <li>请求参数会自动脱敏（如 password 字段），截断至 2000 字符</li>
 *   <li>错误信息仅在实际异常时填充，正常成功路径为空</li>
 *   <li>{@code createBy}, {@code createOrg} 从 SecurityContext 自动获取</li>
 *   <li>事件通过 Spring ApplicationEventPublisher 异步发布，不会阻塞主流程</li>
 * </ul>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 * @see SxwlLogAspect
 * @see SxwlLogEventListener
 * @see com.sxwl.common.constants.SxwlSystemConstants#LOG_TYPE_OPERATION
 * @see sys_log_info
 */
public class SxwlOperationLogEvent implements Serializable {

    /** 模块标题 */
    private String title;

    /** 日志类型：1=登录 2=操作 3=异常 4=安全 */
    private int logType;

    /** 操作描述 */
    private String description;

    /** 调用方法全限定名 */
    private String method;

    /** 请求 URL */
    private String requestUrl;

    /** HTTP 方法：GET/POST/PUT/DELETE */
    private String requestMethod;

    /** 请求参数 JSON（脱敏，截断 2000 字符） */
    private String requestParam;

    /** 响应结果摘要 */
    private String responseResult;

    /** 客户端 IP */
    private String operateIp;

    /** 操作人 ID */
    private Long userId;

    /** 操作人账号（冗余） */
    private String userName;

    /** 执行耗时（毫秒） */
    private long executeTime;

    /** 异常信息（仅失败时有值） */
    private String errorMsg;

    /** 操作状态：0=失败 1=成功 */
    private int status;

    /** 链路追踪 ID */
    private String traceId;

    /** 原始 User-Agent */
    private String userAgent;

    /** 浏览器 */
    private String browser;

    /** 操作系统 */
    private String os;

    /** 操作地点 */
    private String operateLocation;

    /** 字段级变更差异 JSON（如：[{"field":"角色","oldValue":"admin","newValue":"user"}]） */
    private String diff;

    /** 创建者 ID（从 Principal 获取，用于写入 sys_log_info.create_by） */
    private Long createBy;

    /** 创建者组织 ID（从 Principal 获取，用于写入 sys_log_info.create_org） */
    private Long createOrg;

    // ==================== 序列化支持 ====================

    private static final long serialVersionUID = SxwlSystemConstants.SERIAL_VERSION_UID;

    // ==================== 构造器 ====================

    public SxwlOperationLogEvent() {
    }

    // ==================== Builder 模式 ====================

    public SxwlOperationLogEvent title(String title) {
        this.title = title;
        return this;
    }

    public SxwlOperationLogEvent logType(int logType) {
        this.logType = logType;
        return this;
    }

    public SxwlOperationLogEvent description(String description) {
        this.description = description;
        return this;
    }

    public SxwlOperationLogEvent method(String method) {
        this.method = method;
        return this;
    }

    public SxwlOperationLogEvent requestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
        return this;
    }

    public SxwlOperationLogEvent requestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    public SxwlOperationLogEvent requestParam(String requestParam) {
        this.requestParam = requestParam;
        return this;
    }

    public SxwlOperationLogEvent responseResult(String responseResult) {
        this.responseResult = responseResult;
        return this;
    }

    public SxwlOperationLogEvent operateIp(String operateIp) {
        this.operateIp = operateIp;
        return this;
    }

    public SxwlOperationLogEvent userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public SxwlOperationLogEvent userName(String userName) {
        this.userName = userName;
        return this;
    }

    public SxwlOperationLogEvent executeTime(long executeTime) {
        this.executeTime = executeTime;
        return this;
    }

    public SxwlOperationLogEvent errorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }

    public SxwlOperationLogEvent status(int status) {
        this.status = status;
        return this;
    }

    public SxwlOperationLogEvent traceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    public SxwlOperationLogEvent userAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public SxwlOperationLogEvent browser(String browser) {
        this.browser = browser;
        return this;
    }

    public SxwlOperationLogEvent os(String os) {
        this.os = os;
        return this;
    }

    public SxwlOperationLogEvent operateLocation(String operateLocation) {
        this.operateLocation = operateLocation;
        return this;
    }

    public SxwlOperationLogEvent diff(String diff) {
        this.diff = diff;
        return this;
    }

    // ==================== Getter ====================

    public String getTitle() {
        return title;
    }

    public int getLogType() {
        return logType;
    }

    public String getDescription() {
        return description;
    }

    public String getMethod() {
        return method;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getRequestParam() {
        return requestParam;
    }

    public String getResponseResult() {
        return responseResult;
    }

    public String getOperateIp() {
        return operateIp;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public long getExecuteTime() {
        return executeTime;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public int getStatus() {
        return status;
    }

    public String getTraceId() {
        return traceId;
    }

    public String getUserAgent() { return userAgent; }
    public String getBrowser() { return browser; }
    public String getOs() { return os; }
    public String getOperateLocation() { return operateLocation; }
    public String getDiff() { return diff; }
    public Long getCreateBy() { return createBy; }
    public SxwlOperationLogEvent createBy(Long createBy) { this.createBy = createBy; return this; }
    public Long getCreateOrg() { return createOrg; }
    public SxwlOperationLogEvent createOrg(Long createOrg) { this.createOrg = createOrg; return this; }
}

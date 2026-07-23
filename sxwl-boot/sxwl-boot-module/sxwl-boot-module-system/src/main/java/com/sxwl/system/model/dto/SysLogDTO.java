package com.sxwl.system.model.dto;

/**
 * 系统日志 DTO（统一请求/响应）
 *
 * @author shitianyang
 * @since 0.1.0
 */
public class SysLogDTO {

    /** 日志 ID */
    private Long id;

    /** 日志类型：1=登录 2=操作 3=异常 4=安全 */
    private Integer logType;

    /** 模块标题，如：用户管理 */
    private String title;

    /** 操作描述，如：删除用户[zhangsan] */
    private String description;

    /** 调用方法，如 SysUserController.delete() */
    private String method;

    /** 请求URL，如 /sxwl-api/sys/user/1 */
    private String requestUrl;

    /** HTTP方法：GET/POST/PUT/DELETE */
    private String requestMethod;

    /** 请求参数（JSON） */
    private String requestParam;

    /** 响应结果（JSON） */
    private String responseResult;

    /** 操作人IP */
    private String operateIp;

    /** 操作地点（IP反查） */
    private String operateLocation;

    /** 操作人ID */
    private Long userId;

    /** 操作人账号（冗余） */
    private String userName;

    /** 执行耗时（毫秒） */
    private Long executeTime;

    /** 错误信息（异常日志用） */
    private String errorMsg;

    /** 操作状态：0=失败 1=成功 */
    private Integer status;

    /** 链路追踪ID */
    private String traceId;

    /** 用户代理（原始 User-Agent） */
    private String userAgent;

    /** 操作系统 */
    private String browser;

    /** 浏览器 */
    private String os;

    /** 字段级变更差异 JSON */
    private String diff;

    /** 创建时间（仅列表返回时填充） */
    private String createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getLogType() { return logType; }
    public void setLogType(Integer logType) { this.logType = logType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getRequestUrl() { return requestUrl; }
    public void setRequestUrl(String requestUrl) { this.requestUrl = requestUrl; }

    public String getRequestMethod() { return requestMethod; }
    public void setRequestMethod(String requestMethod) { this.requestMethod = requestMethod; }

    public String getRequestParam() { return requestParam; }
    public void setRequestParam(String requestParam) { this.requestParam = requestParam; }

    public String getResponseResult() { return responseResult; }
    public void setResponseResult(String responseResult) { this.responseResult = responseResult; }

    public String getOperateIp() { return operateIp; }
    public void setOperateIp(String operateIp) { this.operateIp = operateIp; }

    public String getOperateLocation() { return operateLocation; }
    public void setOperateLocation(String operateLocation) { this.operateLocation = operateLocation; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public Long getExecuteTime() { return executeTime; }
    public void setExecuteTime(Long executeTime) { this.executeTime = executeTime; }

    public String getErrorMsg() { return errorMsg; }
    public void setErrorMsg(String errorMsg) { this.errorMsg = errorMsg; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getBrowser() { return browser; }
    public void setBrowser(String browser) { this.browser = browser; }

    public String getOs() { return os; }
    public void setOs(String os) { this.os = os; }

    public String getDiff() { return diff; }
    public void setDiff(String diff) { this.diff = diff; }

    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
}

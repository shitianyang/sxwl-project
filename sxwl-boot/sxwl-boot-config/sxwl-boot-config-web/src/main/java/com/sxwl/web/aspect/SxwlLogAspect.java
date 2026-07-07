package com.sxwl.web.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sxwl.common.annotation.SxwlLog;
import com.sxwl.common.event.SxwlOperationLogEvent;
import com.sxwl.common.utils.SxwlPrincipalUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 操作日志 AOP 切面
 *
 * <p>拦截标注了 {@link SxwlLog} 的 Controller 方法，自动采集请求上下文信息
 * 并发布 {@link SxwlOperationLogEvent} 事件。事件由 module-system 的监听器
 * 异步消费，写入 {@code sys_log_info} 表。</p>
 *
 * <h3>自动采集字段</h3>
 * <ul>
 *   <li>method — 类名.方法名</li>
 *   <li>request_url — request.getRequestURI()</li>
 *   <li>request_method — GET/POST/...</li>
 *   <li>request_param — 入参 JSON（敏感字段脱敏，截断 2000 字符）</li>
 *   <li>operate_ip — 客户端 IP</li>
 *   <li>user_id / user_name — 从 SecurityContext 获取</li>
 *   <li>trace_id — 从 MDC 或请求头获取</li>
 * </ul>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@Aspect
public class SxwlLogAspect {

    private static final Logger log = LoggerFactory.getLogger(SxwlLogAspect.class);

    /** 请求参数最大长度（字符） */
    private static final int MAX_PARAM_LENGTH = 2000;

    /** 描述字段最大长度（字符，对应 sys_log_info.description varchar(500)） */
    private static final int MAX_DESC_LENGTH = 500;

    /** 需要脱敏的参数名关键词 */
    private static final Set<String> SENSITIVE_KEYS = Set.of(
            "password", "pwd", "passwd",
            "secret", "token", "accessToken", "refreshToken",
            "idCard", "id_card",
            "oldPassword", "newPassword", "confirmPassword"
    );

    /** JSON 中敏感字段值替换后的占位 */
    private static final String MASK_VALUE = "\"***\"";

    /** 脱敏正则：匹配 "敏感key": "任意值" 并替换为 "敏感key": "***" */
    private static final Pattern SENSITIVE_PATTERN = buildSensitivePattern();

    private static Pattern buildSensitivePattern() {
        String keys = String.join("|", SENSITIVE_KEYS);
        return Pattern.compile(
                "\"(" + keys + ")\"\\s*:\\s*\"[^\"]*\"",
                Pattern.CASE_INSENSITIVE
        );
    }

    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;
    private final ExpressionParser spelParser;
    private final ParameterNameDiscoverer parameterNameDiscoverer;

    public SxwlLogAspect(ApplicationEventPublisher eventPublisher, ObjectMapper objectMapper) {
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
        this.spelParser = new SpelExpressionParser();
        this.parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    }

    @Around("@annotation(sxwlLog)")
    public Object around(ProceedingJoinPoint joinPoint, SxwlLog sxwlLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        SxwlOperationLogEvent event = new SxwlOperationLogEvent();

        // 1. 注解声明的固定字段
        event.title(sxwlLog.title())
             .logType(sxwlLog.logType());

        // 2. 解析 SpEL 描述
        String description = parseDescription(sxwlLog.description(), joinPoint);
        if (description != null && description.length() > MAX_DESC_LENGTH) {
            description = description.substring(0, MAX_DESC_LENGTH);
        }
        event.description(description);

        // 3. 采集方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        event.method(method.getDeclaringClass().getSimpleName() + "." + method.getName());

        // 4. 采集请求信息
        collectRequestInfo(event, joinPoint);

        // 5. 采集用户信息
        collectUserInfo(event);

        // 6. 采集 TraceId
        collectTraceId(event);

        // 7. 执行目标方法
        Object result;
        try {
            result = joinPoint.proceed();
            long costMs = System.currentTimeMillis() - startTime;
            event.status(1)
                 .executeTime(costMs)
                 .responseResult(buildSuccessSummary(result));
            log.debug("[OPERATION-LOG] {} | {}ms | SUCCESS", event.getMethod(), costMs);
        } catch (Throwable e) {
            long costMs = System.currentTimeMillis() - startTime;
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.length() > MAX_PARAM_LENGTH) {
                errorMsg = errorMsg.substring(0, MAX_PARAM_LENGTH);
            }
            event.status(0)
                 .executeTime(costMs)
                 .errorMsg(errorMsg)
                 .responseResult("操作失败: " + (errorMsg != null ? errorMsg : e.getClass().getSimpleName()));
            log.warn("[OPERATION-LOG] {} | {}ms | FAILED: {}", event.getMethod(), costMs, errorMsg);
            // 先发布事件再抛异常（异常失败也要记日志）
            publishEvent(event);
            throw e;
        }

        // 8. 发布事件
        publishEvent(event);
        return result;
    }

    // ==================== 私有方法 ====================

    /**
     * 解析 SpEL 描述表达式
     */
    private String parseDescription(String spel, ProceedingJoinPoint joinPoint) {
        if (spel == null || spel.isEmpty()) {
            return null;
        }
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Object[] args = joinPoint.getArgs();
            String[] paramNames = parameterNameDiscoverer.getParameterNames(method);

            EvaluationContext context = new StandardEvaluationContext();
            if (paramNames != null) {
                for (int i = 0; i < paramNames.length && i < args.length; i++) {
                    context.setVariable(paramNames[i], args[i]);
                }
            }

            Expression expression = spelParser.parseExpression(spel);
            Object value = expression.getValue(context);
            return value != null ? value.toString() : null;
        } catch (Exception e) {
            log.debug("SpEL 表达式解析失败，使用原始文本: {}", spel, e);
            return spel;
        }
    }

    /**
     * 采集 HTTP 请求信息
     */
    private void collectRequestInfo(SxwlOperationLogEvent event, ProceedingJoinPoint joinPoint) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return;
        }
        HttpServletRequest request = attrs.getRequest();
        event.requestUrl(request.getRequestURI())
             .requestMethod(request.getMethod())
             .operateIp(getClientIp(request))
             .requestParam(buildRequestParam(joinPoint));
    }

    /**
     * 采集当前用户信息
     */
    private void collectUserInfo(SxwlOperationLogEvent event) {
        SxwlPrincipalUtils.getCurrentPrincipal().ifPresent(principal -> {
            event.userId(principal.getUserId());
        });
        // userName 从 Authentication.getName() 获取
        try {
            var auth = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication();
            if (auth != null && auth.getName() != null && !"anonymousUser".equals(auth.getName())) {
                event.userName(auth.getName());
            }
        } catch (Exception ignored) {
            // 无 SecurityContext 时跳过
        }
    }

    /**
     * 采集 TraceId
     */
    private void collectTraceId(SxwlOperationLogEvent event) {
        // 优先从 MDC 获取（SxwlTraceIdFilter 已注入）
        String traceId = MDC.get("traceId");
        if (traceId == null || traceId.isEmpty()) {
            // 兜底：从请求头获取
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                traceId = attrs.getRequest().getHeader("X-Request-Id");
            }
        }
        event.traceId(traceId);
    }

    /**
     * 获取客户端真实 IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            // 多层代理时取第一个
            int idx = ip.indexOf(',');
            if (idx > 0) {
                ip = ip.substring(0, idx);
            }
            return ip.trim();
        }
        ip = request.getHeader("X-Real-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip.trim();
        }
        return request.getRemoteAddr();
    }

    /**
     * 构建请求参数 JSON（脱敏 + 截断）
     */
    private String buildRequestParam(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return null;
        }
        try {
            // 单参数直接序列化，多参数用数组
            Object toSerialize = args.length == 1 ? args[0] : args;
            String json = objectMapper.writeValueAsString(toSerialize);
            // 脱敏
            json = maskSensitiveFields(json);
            // 截断
            if (json.length() > MAX_PARAM_LENGTH) {
                json = json.substring(0, MAX_PARAM_LENGTH) + "...[truncated]";
            }
            return json;
        } catch (JsonProcessingException e) {
            log.debug("请求参数序列化失败", e);
            return "[serialization error]";
        }
    }

    /**
     * 对 JSON 字符串中敏感字段值进行脱敏
     */
    String maskSensitiveFields(String json) {
        return SENSITIVE_PATTERN.matcher(json).replaceAll(
                match -> {
                    // 保留 key，value 替换为 ***
                    String keyPart = match.group().replaceAll("\"\\s*:\\s*\"[^\"]*\"", "");
                    return keyPart + ": " + MASK_VALUE;
                }
        );
    }

    /**
     * 构建成功响应的摘要
     */
    private String buildSuccessSummary(Object result) {
        if (result == null) {
            return "操作成功";
        }
        return "操作成功";
    }

    /**
     * 发布事件（异步失败不影响主流程）
     */
    private void publishEvent(SxwlOperationLogEvent event) {
        try {
            eventPublisher.publishEvent(event);
        } catch (Exception e) {
            log.error("操作日志事件发布失败: {}", event.getMethod(), e);
        }
    }
}

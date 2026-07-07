package com.sxwl.redis.aspect;

import com.sxwl.common.annotation.SxwlRepeatSubmit;
import com.sxwl.common.exception.SxwlRepeatSubmitException;
import com.sxwl.common.utils.SxwlPrincipalUtils;
import com.sxwl.common.utils.SxwlRedisKeyUtils;
import com.sxwl.redis.helper.SxwlRedisHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;

/**
 * 防重复提交 AOP 切面
 *
 * <p>拦截标注了 {@link SxwlRepeatSubmit} 的方法，在指定间隔内
 * 同一用户对同一接口的重复请求将被拒绝。</p>
 *
 * <h3>实现</h3>
 * <pre>
 * 首次请求 → SETNX repeat:{userId}:{uri} → 成功 → 放行
 * 重复请求 → SETNX 返回 false（Key 已存在）→ 抛 SxwlRepeatSubmitException
 * </pre>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@Aspect
public class SxwlRepeatSubmitAspect {

    private static final Logger log = LoggerFactory.getLogger(SxwlRepeatSubmitAspect.class);

    private final SxwlRedisHelper redisHelper;

    public SxwlRepeatSubmitAspect(SxwlRedisHelper redisHelper) {
        this.redisHelper = redisHelper;
    }

    @Around("@annotation(repeatSubmit)")
    public Object around(ProceedingJoinPoint joinPoint, SxwlRepeatSubmit repeatSubmit) throws Throwable {
        // 1. 获取当前用户 ID
        long userId = SxwlPrincipalUtils.getCurrentPrincipal()
                .map(p -> p.getUserId())
                .orElse(0L);
        if (userId == 0) {
            log.warn("防重复提交未获取到登录用户，放行");
            return joinPoint.proceed();
        }

        // 2. 获取请求 URI
        String uri = getRequestUri();
        if (uri == null) {
            log.warn("防重复提交未获取到请求 URI，放行");
            return joinPoint.proceed();
        }

        // 3. 构建 Key
        String key = SxwlRedisKeyUtils.repeatSubmitKey(userId, uri);
        int interval = repeatSubmit.interval();

        // 4. SETNX
        Boolean success = redisHelper.setIfAbsent(key, "1", Duration.ofSeconds(interval));
        if (Boolean.TRUE.equals(success)) {
            log.debug("防重复提交放行: userId={}, uri={}, interval={}s", userId, uri, interval);
            return joinPoint.proceed();
        }

        // 5. 重复提交
        log.warn("防重复提交拦截: userId={}, uri={}, interval={}s", userId, uri, interval);
        throw new SxwlRepeatSubmitException(repeatSubmit.message());
    }

    /**
     * 从当前请求获取 URI 路径
     */
    private String getRequestUri() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getRequestURI();
            }
        } catch (Exception ignored) {
            // ignore
        }
        return null;
    }
}

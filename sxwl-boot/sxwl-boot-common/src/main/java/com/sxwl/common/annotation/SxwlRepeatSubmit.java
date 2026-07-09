package com.sxwl.common.annotation;

import java.lang.annotation.*;

/**
 * 防重复提交注解
 *
 * <p>标记在 Controller 方法上，在指定时间间隔内同一用户对同一接口的重复请求将被拦截。
 * 由 redis 模块的 {@code SxwlRepeatSubmitAspect} 切面实现。</p>
 *
 * <p><b>实现原理：</b></p>
 * <pre>
 * 首次请求 → Redis SETNX repeat:{userId}:{uri} = "1"，TTL = interval 秒 → 放行
 * 重复请求 → SETNX 返回 false（Key 已存在）→ 抛 SxwlRepeatSubmitException
 * </pre>
 *
 * <p><b>使用示例：</b></p>
 * <pre>{@code
 * @PostMapping("/save")
 * @SxwlRepeatSubmit(interval = 5, message = "操作过于频繁，请稍后重试")
 * public SxwlResult<Void> save(@RequestBody SaveDTO dto) {
 *     // ...
 * }
 * }</pre>
 *
 * <p><b>适用范围：</b>仅用于 POST / PUT / DELETE 等状态变更接口，GET 类幂等请求不需要。</p>
 * <p><b>拦截粒度：</b>userId + URI 路径，同一用户同一接口在 interval 秒内不可重复请求。</p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SxwlRepeatSubmit {

    /**
     * 防重复提交时间间隔（秒），默认 3 秒
     */
    int interval() default 3;

    /**
     * 提示消息
     */
    String message() default "请勿重复提交";
}

package com.sxwl.web.advice;

import com.sxwl.common.annotation.SxwlNoWrap;
import com.sxwl.common.entity.SxwlResult;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 统一 Result 包装切面
 *
 * <p>对所有 Controller 返回值自动包装为 {@link SxwlResult#success(Object)} 格式。
 * 被 {@link SxwlNoWrap} 标记的方法跳过包装。</p>
 *
 * <p><b>包装规则：</b></p>
 * <ul>
 *   <li>返回值已经是 {@link SxwlResult} → 不包装</li>
 *   <li>方法有 {@link SxwlNoWrap} 注解 → 不包装</li>
 *   <li>其他情况 → 包装为 {@code SxwlResult.success(returnValue)}</li>
 * </ul>
 *
 * @author shitianyang
 * @date 2026/6/28
 * @since 0.1.0
 */
@ControllerAdvice
public class SxwlResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        // 有 @SxwlNoWrap 注解的方法不包装
        return !returnType.hasMethodAnnotation(SxwlNoWrap.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  MediaType contentType, Class converterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        // 已经是 SxwlResult 则不重复包装
        if (body instanceof SxwlResult) {
            return body;
        }
        // 其他情况包装为成功返回
        return SxwlResult.success(body);
    }
}

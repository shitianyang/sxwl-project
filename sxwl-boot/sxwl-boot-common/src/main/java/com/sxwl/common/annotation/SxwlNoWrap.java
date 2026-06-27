package com.sxwl.common.annotation;

import java.lang.annotation.*;

/**
 * 跳过 Result 包装注解
 *
 * <p>标记在 Controller 方法上，表示该方法的返回值不需要被
 * {@code SxwlResponseBodyAdvice} 包装为 {@link com.sxwl.common.entity.SxwlResult} 格式，
 * 直接返回原始响应体。</p>
 *
 * <p><b>适用场景：</b></p>
 * <ul>
 *   <li>文件下载：返回 byte[] 或 InputStream，需要设置 Content-Disposition 头</li>
 *   <li>SSE（Server-Sent Events）流式响应：返回 SseEmitter</li>
 *   <li>第三方回调接口：需要按第三方要求的格式返回</li>
 *   <li>验证码图片：返回 BufferedImage 字节流</li>
 * </ul>
 *
 * <p><b>使用示例：</b></p>
 * <pre>{@code
 * @SxwlNoWrap
 * @GetMapping("/export")
 * public void export(HttpServletResponse response) {
 *     // 直接写入 response.getOutputStream()，不需要 SxwlResult 包装
 *     excelService.exportUsers(response);
 * }
 * }</pre>
 *
 * @author shitianyang
 * @date 2026/6/28
 * @since 0.1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SxwlNoWrap {

    /**
     * 跳过包装的原因说明（可选，仅用于代码可读性）
     *
     * @return 原因描述
     */
    String value() default "";
}

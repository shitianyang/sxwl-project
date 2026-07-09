package com.sxwl.common.exception;

/**
 * 重复提交异常
 *
 * <p>由 {@code SxwlRepeatSubmitAspect} 切面在检测到重复请求时抛出，
 * 由全局异常处理器统一拦截返回提示信息。</p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public final class SxwlRepeatSubmitException extends RuntimeException {

    public SxwlRepeatSubmitException(String message) {
        super(message);
    }
}

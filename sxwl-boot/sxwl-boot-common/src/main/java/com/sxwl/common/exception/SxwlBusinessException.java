package com.sxwl.common.exception;

import com.sxwl.common.enums.SxwlStatusEnum;

/**
 * 业务异常
 *
 * <p>所有业务逻辑校验失败时抛出此异常，由全局异常处理器
 * {@code SxwlGlobalExceptionHandler} 统一拦截并转换为
 * {@link com.sxwl.common.entity.SxwlResult} 格式返回给前端。</p>
 *
 * <p><b>使用示例：</b></p>
 * <pre>{@code
 * // 使用默认错误码 10001（业务校验失败）
 * throw new SxwlBusinessException("用户名已存在");
 *
 * // 使用自定义错误码
 * throw new SxwlBusinessException(10002, "密码强度不足，必须包含大小写字母和数字");
 *
 * // 包装原始异常
 * try {
 *     // ... 业务逻辑 ...
 * } catch (Exception e) {
 *     throw new SxwlBusinessException(10001, "操作失败", e);
 * }
 * }</pre>
 *
 * <p><b>错误码规范：</b></p>
 * <ul>
 *   <li>200：成功</li>
 *   <li>401：未认证（token 过期或无效）</li>
 *   <li>403：无权限</li>
 *   <li>500：服务内部错误</li>
 *   <li>10001：业务校验失败（默认）</li>
 *   <li>10002~19999：自定义业务错误码</li>
 * </ul>
 *
 * @author shitianyang
 * @date 2026/6/28
 * @since 0.1.0
 */
public class SxwlBusinessException extends RuntimeException {

    /**
     * 业务状态码
     */
    private final Integer code;

    /**
     * 使用默认错误码创建业务异常
     *
     * @param message 错误描述
     */
    public SxwlBusinessException(String message) {
        super(message);
        this.code = SxwlStatusEnum.FAIL.getCode();
    }

    /**
     * 使用自定义错误码创建业务异常
     *
     * @param code    错误码
     * @param message 错误描述
     */
    public SxwlBusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 使用自定义错误码创建业务异常，并包装原始异常
     *
     * @param code    错误码
     * @param message 错误描述
     * @param cause   原始异常（用于日志排查）
     */
    public SxwlBusinessException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /**
     * 获取错误码
     *
     * @return 错误码
     */
    public int getCode() {
        return code;
    }
}

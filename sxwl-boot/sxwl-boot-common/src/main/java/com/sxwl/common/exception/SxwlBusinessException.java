package com.sxwl.common.exception;

import com.sxwl.common.enums.SxwlStatusEnum;

/**
 * 业务异常
 *
 * <p>所有业务逻辑校验失败时抛出此异常，由全局异常处理器
 * {@code SxwlGlobalExceptionHandler} 统一拦截并转换为
 * {@link com.sxwl.common.entity.SxwlResult} 格式返回给前端。</p>
 *
 * <h3>使用场景</h3>
 * <ul>
 *   <li>参数校验失败：如必填字段为空、格式不正确</li>
 *   <li>业务规则违反：如用户名已存在、库存不足</li>
 *   <li>数据状态异常：如记录不存在、状态不允许操作</li>
 *   <li>外部服务调用失败：如文件上传、短信发送失败</li>
 * </ul>
 *
 * <h3>与子类区别</h3>
 * <ul>
 *   <li>{@link SxwlUnauthorizedException}（401）：认证失败，继承自此类，固定 code=401</li>
 *   <li>{@link SxwlForbiddenException}（403）：权限不足，继承自此类，固定 code=403</li>
 *   <li>其他业务异常直接使用此类，code 默认 10001 或自定义</li>
 * </ul>
 *
 * <h3>全局异常处理器映射</h3>
 * <pre>{@code
 * // SxwlBusinessException → SxwlResult.error(code, message)
 * throw new SxwlBusinessException(10002, "密码强度不足")
 * → {"code": 10002, "message": "密码强度不足", "data": null}
 *
 * // SxwlUnauthorizedException → SxwlResult.unauthorized(message)
 * throw new SxwlUnauthorizedException() 
 * → {"code": 401, "message": "未登录或 Token 已过期", "data": null}
 *
 * // SxwlForbiddenException → SxwlResult.forbidden(message)
 * throw new SxwlForbiddenException()
 * → {"code": 403, "message": "无访问权限", "data": null}
 * }</pre>
 *
 * <h3>错误码规范</h3>
 * <ul>
 *   <li><b>200</b>：成功</li>
 *   <li><b>401</b>：未认证（由 SxwlUnauthorizedException 抛出）</li>
 *   <li><b>403</b>：无权限（由 SxwlForbiddenException 抛出）</li>
 *   <li><b>429</b>：重复提交（由 SxwlRepeatSubmitException 抛出）</li>
 *   <li><b>10001</b>：业务校验失败（默认错误码）</li>
 *   <li><b>10002~19999</b>：自定义业务错误码（按模块分配）</li>
 *   <li><b>20000~29999</b>：预留财务模块错误码</li>
 *   <li><b>30000~39999</b>：预留工作流模块错误码</li>
 * </ul>
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * // 使用默认错误码 10001（业务校验失败）
 * throw new SxwlBusinessException("用户名已存在");
 *
 * // 使用自定义错误码（推荐按模块分配）
 * throw new SxwlBusinessException(10002, "密码强度不足，必须包含大小写字母和数字");
 * 
 * // 文件上传失败
 * throw new SxwlBusinessException(10001, "读取分片数据失败: uploadId=" + uploadId, e);
 *
 * // 包装原始异常（便于日志排查）
 * try {
 *     fileService.uploadFile();
 * } catch (IOException e) {
 *     log.error("文件上传失败: fileName={}", fileName, e);
 *     throw new SxwlBusinessException(10001, "文件上传失败", e);
 * }
 * }</pre>
 *
 * <h3>注意事项</h3>
 * <ul>
 *   <li><b>必须在 catch 块中打印完整堆栈</b>：{@code log.error("msg", e)}，而非 {@code log.warn("msg")}</li>
 *   <li><b>错误信息要对用户友好</b>：不要暴露 SQL 语句、堆栈跟踪等敏感信息</li>
 *   <li><b>细节要记录到日志</b>：如 userId、fileName、uploadId 等业务上下文</li>
 *   <li><b>不要捕获后吞掉异常</b>：要么重新抛出，要么记录日志后继续处理</li>
 * </ul>
 *
 * @author shitianyang
 * @date 2026/6/28
 * @since 0.1.0
 * @see SxwlGlobalExceptionHandler
 * @see SxwlUnauthorizedException
 * @see SxwlForbiddenException
 * @see SxwlRepeatSubmitException
 * @see com.sxwl.common.entity.SxwlResult
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

package com.sxwl.common.exception;

import com.sxwl.common.enums.SxwlStatusEnum;

/**
 * 重复提交异常
 *
 * <p>由 {@code SxwlRepeatSubmitAspect} 切面在检测到重复请求时抛出，
 * 由全局异常处理器统一拦截并返回 HTTP 429 状态码（Too Many Requests）。</p>
 *
 * <h3>触发机制</h3>
 * <ul>
 *   <li>基于 {@code userId + URI} 生成 Redis Key</li>
 *   <li>在设定的时间窗口内（如 3 秒），同一用户对同一接口只能提交一次</li>
 *   <li>超过时间窗口后自动释放，允许再次提交</li>
 * </ul>
 *
 * <h3>适用场景</h3>
 * <ul>
 *   <li><b>推荐</b>：POST / PUT / DELETE 等会产生副作用的操作</li>
 *   <li><b>不推荐</b>：GET 请求、幂等性操作的 POST 请求</li>
 * </ul>
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * // Controller 方法上加注解
 * @PostMapping("/submit")
 * @SxwlRepeatSubmit(interval = 5, message = "操作过于频繁，请稍后重试")
 * public SxwlResult<Void> submit(@RequestBody OrderDTO dto) {
 *     orderService.createOrder(dto);
 *     return SxwlResult.success();
 * }
 *
 * // 或使用默认值（3 秒间隔）
 * @DeleteMapping("/{id}")
 * @SxwlRepeatSubmit
 * public SxwlResult<Void> delete(@PathVariable Long id) {
 *     orderService.deleteOrder(id);
 *     return SxwlResult.success();
 * }
 * }</pre>
 *
 * <h3>与全局异常处理器的配合</h3>
 * <pre>{@code
 * // SxwlRepeatSubmitException → SxwlResult.error(429, message)
 * throw new SxwlRepeatSubmitException("操作过于频繁，请稍后重试")
 * → {"code": 429, "message": "操作过于频繁，请稍后重试", "data": null}
 * }</pre>
 *
 * <h3>注意事项</h3>
 * <ul>
 *   <li><b>前端处理</b>：429 错误码在前端可能需要特殊处理（当前前端未对 429 做特殊处理，作为普通错误展示给用户）</li>
 *   <li><b>时间窗口选择</b>：密码修改、订单提交建议使用较长的时间窗口（5-10 秒）；简单查询操作不需要加此注解</li>
 *   <li><b>误用风险</b>：不要对批量操作或长时间运行的任务使用此注解，会导致合法的请求被拦截</li>
 *   <li><b>分布式支持</b>：当前实现依赖 Redis SETNX，支持分布式环境下的防重复提交</li>
 * </ul>
 *
 * <h3>错误码规范</h3>
 * <ul>
 *   <li><b>429</b>：重复提交（HTTP 标准状态码，Too Many Requests）</li>
 *   <li>由全局异常处理器统一转换，业务代码中无需关心具体 code 值</li>
 * </ul>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 * @see SxwlGlobalExceptionHandler
 * @see com.sxwl.common.annotation.SxwlRepeatSubmit
 */
public final class SxwlRepeatSubmitException extends RuntimeException {

    /**
     * 使用自定义消息创建重复提交异常
     *
     * @param message 错误描述，如 "操作过于频繁，请稍后重试"
     */
    public SxwlRepeatSubmitException(String message) {
        super(message);
    }
}

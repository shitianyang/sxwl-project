package com.sxwl.common.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 *
 * <p>标记在 Controller 方法上，由 web 模块的 {@code SxwlLogAspect} 切面拦截，
 * 自动采集请求上下文并发布 {@link com.sxwl.common.event.SxwlOperationLogEvent} 事件，
 * 最终由 module-system 的 {@code SxwlLogEventListener} 异步写入 {@code sys_log_info} 表。</p>
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * @PostMapping("/save")
 * @SxwlLog(title = "用户管理", description = "新增用户[#{[0].username}]")
 * public SxwlResult<Void> save(@RequestBody SysUserSaveDTO dto) {
 *     // ...
 * }
 *
 * @DeleteMapping("/{id}")
 * @SxwlLog(title = "用户管理", logType = 2, description = "删除用户[#{#id}]")
 * public SxwlResult<Void> delete(@PathVariable Long id) {
 *     // ...
 * }
 * }</pre>
 *
 * <h3>字段映射（→ sys_log_info 表）</h3>
 * <table>
 *   <tr><td>title</td><td>→ title</td></tr>
 *   <tr><td>logType</td><td>→ log_type</td></tr>
 *   <tr><td>description</td><td>→ description（支持 SpEL）</td></tr>
 * </table>
 * 其余字段（method/request_url/operate_ip/user_id/trace_id 等）由切面自动采集。
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SxwlLog {

    /**
     * 模块标题（必填），如：用户管理、角色管理
     *
     * @return 模块标题，写入 sys_log_info.title
     */
    String title();

    /**
     * 日志类型
     * <ul>
     *   <li>1 = 登录日志</li>
     *   <li>2 = 操作日志（默认）</li>
     *   <li>3 = 异常日志</li>
     *   <li>4 = 安全日志</li>
     * </ul>
     *
     * @return 日志类型，写入 sys_log_info.log_type
     */
    int logType() default 2;

    /**
     * 操作描述（可选，支持 SpEL 表达式）
     *
     * <p>可使用方法参数引用：</p>
     * <ul>
     *   <li>{@code #{#id}} — 引用名为 id 的方法参数</li>
     *   <li>{@code #{[0].username}} — 引用第一个参数的 username 属性</li>
     * </ul>
     *
     * @return 操作描述，写入 sys_log_info.description
     */
    String description() default "";
}

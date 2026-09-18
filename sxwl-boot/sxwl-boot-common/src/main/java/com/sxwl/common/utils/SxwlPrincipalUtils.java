package com.sxwl.common.utils;

import com.sxwl.common.principal.SxwlPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * 从 Spring Security 上下文获取当前登录用户身份的工具类
 *
 * <p>封装了从 {@link SecurityContextHolder} 读取 Authentication 并安全转换为
 * {@link SxwlPrincipal} 的逻辑。mybatis 拦截器、redis 模块、security 模块、auth 模块
 * 等所有需要获取当前用户的场景均可复用。</p>
 *
 * <h3>使用场景</h3>
 * <ul>
 *   <li><b>数据权限拦截器</b>：{@code SxwlDataScopeInterceptor} 读取用户可见组织列表</li>
 *   <li><b>操作日志监听器</b>：{@code SxwlLogEventListener} 记录当前操作用户</li>
 *   <li><b>SSE 推送控制器</b>：{@code SxwlSseController} 确定推送目标用户</li>
 *   <li><b>防重复提交</b>：{@code SxwlRepeatSubmitAspect} 生成 Redis Key（userId + URI）</li>
 * </ul>
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * // 方式一：Optional 模式（推荐）
 * SxwlPrincipalUtils.getCurrentPrincipal().ifPresent(principal -> {
 *     Long userId = principal.getUserId();
 *     Long orgId = principal.getOrgId();
 * });
 *
 * // 方式二：直接获取（适合业务逻辑）
 * Long userId = SxwlPrincipalUtils.getCurrentPrincipal()
 *         .map(SxwlPrincipal::getUserId)
 *         .orElseThrow(() -> new SxwlUnauthorizedException());
 * }</pre>
 *
 * <h3>线程安全性</h3>
 * <p>本工具类是线程安全的：</p>
 * <ul>
 *   <li>{@link SecurityContextHolder} 默认使用 {@code ThreadLocal} 存储 Authentication</li>
 *   <li>每个请求线程独立存储，不会发生并发冲突</li>
 *   <li>登录过滤器在请求开始时写入，在请求结束时清除</li>
 * </ul>
 *
 * <h3>注意事项</h3>
 * <ul>
 *   <li><b>未登录返回 empty</b>：定时任务、匿名接口等无登录场景会返回 {@code Optional.empty()}</li>
 *   <li><b>不要强转 Authentication.getPrincipal()</b>：必须先做 instanceof SxwlPrincipal 检查</li>
 *   <li><b>不要在静态代码块中缓存 Principal</b>：每次调用都会获取最新的上下文</li>
 *   <li><b>跨进程时需要显式传递</b>：如果需要在异步线程池中传递，需手动传入 userId</li>
 * </ul>
 *
 * <h3>与 SxwlPrincipal 的关系</h3>
 * <pre>{@code
 * // 请求流程：
 * 1. 用户输入用户名密码 → SxwlAuthenticationFilter
 * 2. 认证成功 → 创建 SxwlLoginUser 实例 → 存入 SecurityContext
 * 3. 业务代码调用 → SxwlPrincipalUtils.getCurrentPrincipal() → 读取 SxwlLoginUser
 * 4. 请求结束 → LogoutFilter 清除 SecurityContext
 * }</pre>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 * @see SxwlPrincipal
 * @see com.sxwl.security.model.SxwlLoginUser
 * @see com.sxwl.mybatis.interceptor.SxwlDataScopeInterceptor
 */
public final class SxwlPrincipalUtils {

    private SxwlPrincipalUtils() {
        throw new UnsupportedOperationException("SxwlPrincipalUtils 工具类，不允许实例化");
    }

    /**
     * 从 Spring Security 上下文中获取当前登录用户身份
     *
     * @return 当前用户身份；未登录 / 非 SxwlPrincipal 实例时返回 empty
     */
    public static Optional<SxwlPrincipal> getCurrentPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            return Optional.empty();
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof SxwlPrincipal p) {
            return Optional.of(p);
        }
        return Optional.empty();
    }
}

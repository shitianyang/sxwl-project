package com.sxwl.common.principal;

import java.io.Serializable;
import java.util.Set;

/**
 * 最小用户身份接口
 *
 * <p>定义跨模块通用的用户身份标识，供 mybatis 拦截器、数据权限等不依赖 security 全量上下文的场景使用。</p>
 *
 * <p><b>职责边界：</b></p>
 * <ul>
 *   <li>本接口只回答"你是谁"（userId）、"你属于哪个组织"（orgId）、"你能看哪些组织的数据"（dataScopeOrgIds）</li>
 *   <li>不包含角色、权限、用户名等安全上下文信息——这些是 {@code SxwlLoginUser} 的职责</li>
 *   <li>{@code SxwlLoginUser}（security 模块）实现本接口，附加完整的认证授权信息</li>
 * </ul>
 *
 * <h3>使用场景</h3>
 * <ul>
 *   <li><b>数据权限拦截器</b>：{@link com.sxwl.mybatis.interceptor.SxwlDataScopeInterceptor} 读取 {@code dataScopeOrgIds} 拼 SQL</li>
 *   <li><b>操作日志监听器</b>：{@link com.sxwl.system.listener.SxwlLogEventListener} 获取当前用户 ID</li>
 *   <li><b>SSE 推送</b>：{@link com.sxwl.sse.controller.SxwlSseController} 确定推送目标用户</li>
 *   <li><b>防重复提交</b>：{@link com.sxwl.redis.aspect.SxwlRepeatSubmitAspect} 生成 Redis Key（userId + URI）</li>
 * </ul>
 *
 * <h3>与 SxwlLoginUser 的区别</h3>
 * <table>
 *   <tr><th>属性</th><th>SxwlPrincipal</th><th>SxwlLoginUser</th></tr>
 *   <tr><td>所在模块</td><td>common</td><td>security</td></tr>
 *   <tr><td>依赖</td><td>无外部依赖</td><td>依赖 Spring Security</td></tr>
 *   <tr><td>职责</td><td>用户身份标识（最小集）</td><td>完整认证授权信息</td></tr>
 *   <tr><td>字段</td><td>userId, orgId, dataScopeOrgIds</td><td>userId, username, nickname, roles, perms, dataScope...</td></tr>
 *   <tr><td>使用场景</td><td>数据权限、日志、SSE 等基础设施</td><td>业务逻辑、@PreAuthorize 权限校验</td></tr>
 * </table>
 *
 * <h3>注意事项</h3>
 * <ul>
 *   <li><b>实现类必须添加 {@code implements Serializable}</b>：为未来序列化到 Redis 预留支持</li>
 *   <li><b> getUserId() / getOrgId() 不得返回 null</b>：使用雪花算法生成的 userId 必定有值</li>
 *   <li><b>getDataScopeOrgIds() 返回 null 表示全部数据</b>：空集表示无可见组织（1=0 兜底）</li>
 *   <li><b>不要在业务逻辑中直接强转 Authentication.getPrincipal()</b>：先做 instanceof 检查</li>
 * </ul>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 * @see com.sxwl.security.model.SxwlLoginUser
 * @see com.sxwl.common.utils.SxwlPrincipalUtils
 * @see com.sxwl.mybatis.interceptor.SxwlDataScopeInterceptor
 */
public interface SxwlPrincipal extends Serializable {

    /**
     * 获取当前用户唯一标识
     *
     * @return 用户 ID（雪花算法生成），不会为 null
     */
    Long getUserId();

    /**
     * 获取当前用户所属主组织标识
     *
     * @return 组织 ID，不会为 null
     */
    Long getOrgId();

    /**
     * 获取数据权限可见的组织 ID 集合
     * <p>登录时由 auth 模块计算好后写入，mybatis 拦截器直接读取拼 SQL。</p>
     *
     * @return null=全部数据（不限制），空集=无可见组织（1=0 兜底）
     */
    default Set<Long> getDataScopeOrgIds() {
        return null;
    }

    /**
     * 是否仅限本人数据（scope=4）
     * <p>当此值为 true 时，拦截器会在 WHERE 中追加 {@code create_by = <userId>} 条件，
     * 代替或补充 {@code create_org IN (...)} 组织维度过滤。</p>
     *
     * @return true=仅查看本人创建的数据
     */
    default boolean getDataScopeSelf() {
        return false;
    }
}

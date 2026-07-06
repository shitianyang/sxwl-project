package com.sxwl.common.principal;

/**
 * 最小用户身份接口
 *
 * <p>定义跨模块通用的用户身份标识，只包含最核心的两个字段（userId + orgId），
 * 供 mybatis 拦截器、数据权限等不依赖 security 全量上下文的场景使用。</p>
 *
 * <p><b>职责边界：</b></p>
 * <ul>
 *   <li>本接口只回答"你是谁"（userId）和"你属于哪个组织"（orgId）</li>
 *   <li>不包含角色、权限、用户名等安全上下文信息——这些是 {@code SxwlLoginUser} 的职责</li>
 *   <li>{@code SxwlLoginUser}（security 模块）实现本接口，附加完整的认证授权信息</li>
 * </ul>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public interface SxwlPrincipal {

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
}

package com.sxwl.common.spi;

import com.sxwl.common.principal.SxwlPrincipal;

import java.util.List;

/**
 * 数据权限规则计算 SPI 接口
 *
 * <p>本接口定义在 common 模块（而非 security），使 mybatis 拦截器无需依赖 security
 * 即可获取数据权限规则。具体实现由 module-system 提供，从 Redis 组织树缓存 / 角色配置
 * 计算当前用户可见的组织 ID 列表。</p>
 *
 * <h3>调用链</h3>
 * <pre>
 * SxwlDataScopeInterceptor（mybatis 模块）
 *     └── 调用本接口 .getVisibleOrgIds(principal)
 *     └── 拼接 WHERE create_org IN (...) 条件
 *
 * SxwlDataScopeProviderImpl（module-system 模块）
 *     └── 实现本接口
 *     └── 从 Redis 组织树缓存计算可见组织
 * </pre>
 *
 * <h3>为空含义</h3>
 * 返回空列表表示该用户无任何组织的数据可见权限，拦截器应追加 {@code AND 1=0}。
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public interface SxwlDataScopeProvider {

    /**
     * 获取当前用户可见的组织 ID 列表
     *
     * <p>根据用户的 data_scope 配置计算：</p>
     * <ul>
     *   <li>data_scope=1（全部数据）→ 返回 null，拦截器不追加条件</li>
     *   <li>data_scope=2（本组织）→ 返回 [user.orgId]</li>
     *   <li>data_scope=3（本组织及下级）→ 返回 [user.orgId + 所有递归下级]</li>
     *   <li>data_scope=4（仅本人）→ 返回 []（空列表），拦截器追加 AND create_by = #{userId}</li>
     *   <li>data_scope=5（自定义）→ 返回角色配置的可见组织列表</li>
     * </ul>
     *
     * @param principal 当前登录用户身份，不会为 null
     * @return 可见组织 ID 列表；null 表示全部数据可见（不追加条件）；空列表表示无可见组织
     */
    List<Long> getVisibleOrgIds(SxwlPrincipal principal);
}

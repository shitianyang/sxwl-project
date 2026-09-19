package com.sxwl.security.model;

import com.sxwl.common.constants.SxwlSystemConstants;
import com.sxwl.common.principal.SxwlPrincipal;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * 认证用户对象
 * <p>
 * 存入 SecurityContext 的用户主体，实现 {@link SxwlPrincipal} 接口。
 * <b>不含密码</b>——防止序列化到 Redis 或日志时意外泄露。
 * </p>
 *
 * <h3>职责范围</h3>
 * <ul>
 *   <li><b>用户身份</b>：userId、username、nickname</li>
 *   <li><b>账号状态</b>：status（0=禁用，1=启用）</li>
 *   <li><b>认证信息</b>：roles（角色编码）、perms（权限标识）</li>
 *   <li><b>组织架构</b>：createOrg（主组织 ID）、dataScope（数据范围类型）</li>
 *   <li><b>数据权限</b>：dataScopeOrgIds（可见组织）、dataScopeSelf（仅本人标志）</li>
 * </ul>
 *
 * <h3>与 SxwlPrincipal 的关系</h3>
 * <pre>{@code
 * // 推荐使用 Builder 模式创建
 * SxwlLoginUser loginUser = SxwlLoginUser.builder()
 *     .userId(1L)
 *     .username("admin")
 *     .createOrg(100L)
 *     .build();
 * 
 * // 向上转型为 SxwlPrincipal（用于数据权限等基础设施场景）
 * SxwlPrincipal principal = loginUser;
 * Long userId = principal.getUserId();      // 1
 * Long orgId = principal.getOrgId();        // 100
 * Set<Long> orgs = principal.getDataScopeOrgIds(); // 数据权限
 * }</pre>
 *
 * <h3>生命周期</h3>
 * <ol>
 *   <li><b>认证成功</b>：{@code SxwlAuthenticationProvider} 验证用户名密码后创建实例</li>
 *   <li><b>写入 Context</b>：存入 {@code SecurityContextHolder.getContext().setAuthentication()}</li>
 *   <li><b>读取身份</b>：其他模块通过 {@code SxwlPrincipalUtils.getCurrentPrincipal()} 获取</li>
 *   <li><b>登出清除</b>：{@code LogoutFilter} 调用 {@code SecurityContextHolder.clearContext()}</li>
 * </ol>
 *
 * <h3>字段映射说明</h3>
 * <table>
 *   <tr><th>字段</th><th>来源</th><th>说明</th></tr>
 *   <tr><td>userId</td><td>数据库 user_info.user_id</td><td>雪花算法生成，全局唯一</td></tr>
 *   <tr><td>username</td><td>数据库 user_info.user_name</td><td>登录账号，唯一索引</td></tr>
 *   <tr><td>nickname</td><td>数据库 user_info.nickname</td><td>显示名称，可为空</td></tr>
 *   <tr><td>createOrg</td><td>数据库 user_info.create_org</td><td>用户所属主组织</td></tr>
 *   <tr><td>dataScope</td><td>角色表的 data_scope 字段</td><td>1=全部 2=本组织 3=本组织及下级 4=仅本人 5=自定义</td></tr>
 *   <tr><td>dataScopeOrgIds</td><td>登录时由 auth 模块计算</td><td>根据 dataScope 动态填充</td></tr>
 * </table>
 *
 * <h3>注意事项</h3>
 * <ul>
 *   <li><b>不含密码</b>：构造实例前必须从 User 实体中移除 password 字段</li>
 *   <li><b>不可变原则</b>：一旦创建，核心字段不应修改（userId、username 等）</li>
 *   <li><b>线程安全</b>：每个请求独立一个 Authentication 实例，无需担心并发问题</li>
 *   <li><b>序列化风险</b>：实现类已添加 {@code implements Serializable}，防止跨节点传输异常</li>
 * </ul>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 * @see SxwlPrincipal
 * @see com.sxwl.common.utils.SxwlPrincipalUtils
 * @see com.sxwl.security.filter.SxwlAuthenticationFilter
 */
public class SxwlLoginUser implements SxwlPrincipal, Serializable {

    @Serial
    private static final long serialVersionUID = SxwlSystemConstants.SERIAL_VERSION_UID;

    /** 用户 ID */
    private Long userId;

    /** 用户名（登录账号） */
    private String username;

    /** 用户昵称 */
    private String nickname;

    /** 账号状态：0=禁用 1=启用 */
    private Integer status;

    /** 角色编码集合 */
    private Set<String> roles;

    /** 权限标识集合 */
    private Set<String> perms;

    /** 所属主组织 ID */
    private Long createOrg;

    /** 数据范围类型 */
    private Integer dataScope;

    /** 数据范围可见组织 ID 集合 */
    private Set<Long> dataScopeOrgIds;

    /** 是否仅限本人数据（scope=4） */
    private boolean dataScopeSelf;

    public SxwlLoginUser() {
    }

    /**
     * 创建认证用户实例（Builder 模式）
     *
     * <p>推荐使用 Builder 模式创建实例，避免多次 setter 调用：</p>
     * <pre>{@code
     * SxwlLoginUser loginUser = SxwlLoginUser.builder()
     *     .userId(1L)
     *     .username("admin")
     *     .nickname("管理员")
     *     .status(1)
     *     .createOrg(100L)
     *     .dataScope(2)
     *     .dataScopeOrgIds(Set.of(100L, 200L))
     *     .build();
     * }</pre>
     *
     * @return Builder 实例
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Set<String> getPerms() {
        return perms;
    }

    public void setPerms(Set<String> perms) {
        this.perms = perms;
    }

    @Override
    public Long getOrgId() {
        return createOrg;
    }

    public Long getCreateOrg() {
        return createOrg;
    }

    public void setCreateOrg(Long createOrg) {
        this.createOrg = createOrg;
    }

    public Integer getDataScope() {
        return dataScope;
    }

    public void setDataScope(Integer dataScope) {
        this.dataScope = dataScope;
    }

    public Set<Long> getDataScopeOrgIds() {
        return dataScopeOrgIds;
    }

    public void setDataScopeOrgIds(Set<Long> dataScopeOrgIds) {
        this.dataScopeOrgIds = dataScopeOrgIds;
    }

    @Override
    public boolean getDataScopeSelf() {
        return dataScopeSelf;
    }

    public void setDataScopeSelf(boolean dataScopeSelf) {
        this.dataScopeSelf = dataScopeSelf;
    }

    // ==================== Builder 模式 ====================

    /**
     * SxwlLoginUser 构建器
     *
     * <p>提供流式 API 创建 SxwlLoginUser 实例，简化代码并减少 setter 调用。</p>
     *
     * @author shitianyang
     * @since 0.1.0
     */
    public static class Builder {
        private final SxwlLoginUser user = new SxwlLoginUser();

        public Builder userId(Long userId) {
            user.userId = userId;
            return this;
        }

        public Builder username(String username) {
            user.username = username;
            return this;
        }

        public Builder nickname(String nickname) {
            user.nickname = nickname;
            return this;
        }

        public Builder status(Integer status) {
            user.status = status;
            return this;
        }

        public Builder roles(Set<String> roles) {
            user.roles = roles;
            return this;
        }

        public Builder perms(Set<String> perms) {
            user.perms = perms;
            return this;
        }

        /**
         * 设置用户所属主组织 ID
         *
         * @param createOrg 组织 ID（即数据库中的 create_org 字段）
         */
        public Builder createOrg(Long createOrg) {
            user.createOrg = createOrg;
            return this;
        }

        public Builder dataScope(Integer dataScope) {
            user.dataScope = dataScope;
            return this;
        }

        public Builder dataScopeOrgIds(Set<Long> dataScopeOrgIds) {
            user.dataScopeOrgIds = dataScopeOrgIds;
            return this;
        }

        public Builder dataScopeSelf(boolean dataScopeSelf) {
            user.dataScopeSelf = dataScopeSelf;
            return this;
        }

        /**
         * 构建最终的 SxwlLoginUser 实例
         *
         * @return 已填充数据的 SxwlLoginUser 对象
         */
        public SxwlLoginUser build() {
            return user;
        }
    }
}

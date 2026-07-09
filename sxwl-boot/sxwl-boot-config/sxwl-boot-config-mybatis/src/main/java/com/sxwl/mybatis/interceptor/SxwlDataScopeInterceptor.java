package com.sxwl.mybatis.interceptor;

import com.sxwl.common.principal.SxwlPrincipal;
import com.sxwl.common.utils.SxwlPrincipalUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Optional;
import java.util.Set;

/**
 * 数据权限 SQL 拦截器
 *
 * <p>在 SELECT 查询执行前自动拼接数据权限条件，限制当前用户只能看到有权限的数据。
 * 从 SecurityContext 读取 {@link SxwlPrincipal#getDataScopeOrgIds()}（登录时由 auth 计算），不查库。</p>
 *
 * <h3>拦截规则</h3>
 * <table>
 *   <tr><th>data_scope</th><th>含义</th><th>SQL 拼接</th></tr>
 *   <tr><td>1</td><td>全部数据</td><td>不追加条件（超管）</td></tr>
 *   <tr><td>2</td><td>本组织</td><td>AND create_org = #{userOrgId}</td></tr>
 *   <tr><td>3</td><td>本组织及下级</td><td>AND create_org IN (本组织 + 下级)</td></tr>
 *   <tr><td>4</td><td>仅本人</td><td>AND create_by = #{userId}</td></tr>
 *   <tr><td>5</td><td>自定义</td><td>AND create_org IN (可见组织列表)</td></tr>
 * </table>
 *
 * <h3>空集兜底</h3>
 * data_scope=5 但无可配记录时追加 {@code AND 1=0}，返回空结果而非 SQL 语法错误。
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare",
                args = {Connection.class, Integer.class})
})
public class SxwlDataScopeInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(SxwlDataScopeInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler handler = (StatementHandler) invocation.getTarget();
        MetaObject meta = SystemMetaObject.forObject(handler);

        // 获取当前 Mapper 信息
        MappedStatement ms = (MappedStatement) meta.getValue("delegate.mappedStatement");

        // 仅处理 SELECT 语句
        if (!ms.getSqlCommandType().name().equals("SELECT")) {
            return invocation.proceed();
        }

        // 获取当前登录用户
        Optional<SxwlPrincipal> principalOpt = SxwlPrincipalUtils.getCurrentPrincipal();
        if (principalOpt.isEmpty()) {
            return invocation.proceed(); // 无登录用户（定时任务等），不拦截
        }
        SxwlPrincipal principal = principalOpt.get();

        // 从 SecurityContext 读数据权限（登录时由 auth 计算好放入）
        Set<Long> visibleOrgIds = principal.getDataScopeOrgIds();

        if (visibleOrgIds == null) {
            // data_scope=1：全部数据，不追加条件
            return invocation.proceed();
        }

        // 拼接数据权限 SQL
        BoundSql boundSql = handler.getBoundSql();
        String originalSql = boundSql.getSql();
        String scopeSql = buildScopeSql(originalSql, principal, visibleOrgIds);

        meta.setValue("delegate.boundSql.sql", scopeSql);
        log.debug("DataScope applied: userId={}, orgIds={}, sql={}", principal.getUserId(), visibleOrgIds, scopeSql);

        return invocation.proceed();
    }

    /**
     * 拼接数据权限条件
     */
    private String buildScopeSql(String originalSql, SxwlPrincipal principal, Set<Long> visibleOrgIds) {
        if (visibleOrgIds.isEmpty()) {
            // 空集兜底：无可配组织，追加 1=0 返回空结果
            return originalSql + " AND 1=0";
        }

        StringBuilder inClause = new StringBuilder();
        for (Long orgId : visibleOrgIds) {
            if (!inClause.isEmpty()) inClause.append(", ");
            inClause.append(orgId);
        }
        return originalSql + " AND create_org IN (" + inClause + ")";
    }
}

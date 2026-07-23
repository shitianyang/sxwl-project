package com.sxwl.mybatis.interceptor;

import com.sxwl.common.annotation.SxwlDataScope;
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
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 数据权限 SQL 拦截器
 *
 * <p>仅在 Mapper 方法标注了 {@link SxwlDataScope @SxwlDataScope} 时生效。
 * 从 SecurityContext 读取 {@link SxwlPrincipal#getDataScopeOrgIds()}（登录时由 auth 计算），不查库。</p>
 *
 * <h3>SQL 插入规则</h3>
 * <ul>
 *   <li>已有 WHERE → 追加 {@code AND <column> IN (...)}</li>
 *   <li>无 WHERE → 在 ORDER BY / LIMIT / FOR UPDATE 前插入 {@code WHERE <column> IN (...)}</li>
 *   <li>空集兜底 → 插入 {@code 1=0} 返回空结果</li>
 * </ul>
 *
 * <h3>列别名支持</h3>
 * <p>通过 {@link SxwlDataScope#columnAlias()} 指定前缀，如 {@code @SxwlDataScope(columnAlias = "u")}
 * 生成 {@code u.create_org IN (...)}，适用于多表 JOIN 场景。</p>
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

    // 匹配 ORDER BY / LIMIT / FOR UPDATE / OFFSET / FETCH / GROUP BY / HAVING
    private static final Pattern CLAUSE_PATTERN = Pattern.compile(
            "(?i)\\b(ORDER\\s+BY|LIMIT|FOR\\s+UPDATE|OFFSET|FETCH|GROUP\\s+BY|HAVING)\\b");

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

        // 检查 Mapper 方法是否标注 @SxwlDataScope（同时获取 columnAlias）
        SxwlDataScope dataScope = getDataScopeAnnotation(ms);
        if (dataScope == null) {
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
        String scopeSql = buildScopeSql(originalSql, visibleOrgIds, dataScope.columnAlias());

        meta.setValue("delegate.boundSql.sql", scopeSql);
        log.debug("DataScope applied: userId={}, orgIds={}, columnAlias={}",
                principal.getUserId(), visibleOrgIds, dataScope.columnAlias());

        return invocation.proceed();
    }

    /**
     * 获取 Mapper 方法上的 {@link SxwlDataScope} 注解
     *
     * @return 注解实例（含 columnAlias），未标注时返回 null
     */
    private SxwlDataScope getDataScopeAnnotation(MappedStatement ms) {
        String id = ms.getId();
        // id 格式：com.sxwl.system.mapper.SysUserMapper.getUserPageByParams
        String className = id.substring(0, id.lastIndexOf('.'));
        String methodName = id.substring(id.lastIndexOf('.') + 1);

        try {
            Class<?> mapperClass = Class.forName(className);
            for (var method : mapperClass.getDeclaredMethods()) {
                if (method.getName().equals(methodName)) {
                    SxwlDataScope annotation = method.getAnnotation(SxwlDataScope.class);
                    if (annotation != null) {
                        return annotation;
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            log.warn("无法获取 Mapper 类: {}", className);
        }
        return null;
    }

    /**
     * 拼接数据权限条件
     *
     * <p>通过正则识别 WHERE 子句位置和 ORDER BY / LIMIT 等后缀子句，
     * 在正确位置插入 {@code AND/WHERE <column> IN (...)}，
     * 避免破坏 SQL 语法结构。</p>
     *
     * @param columnAlias 列别名，为空时直接用 {@code create_org}
     */
    private String buildScopeSql(String originalSql, Set<Long> visibleOrgIds, String columnAlias) {
        // 构建条件表达式
        String columnName = columnAlias.isEmpty() ? "create_org" : columnAlias + ".create_org";
        String scopeCondition;
        if (visibleOrgIds.isEmpty()) {
            scopeCondition = "1=0";
        } else {
            StringJoiner sj = new StringJoiner(", ");
            for (Long orgId : visibleOrgIds) {
                sj.add(String.valueOf(orgId));
            }
            scopeCondition = columnName + " IN (" + sj + ")";
        }

        // 确定插入位置和前缀
        // 先找 ORDER BY / LIMIT 等后缀子句的开始位置
        Matcher clauseMatcher = CLAUSE_PATTERN.matcher(originalSql);
        int insertPos = originalSql.length();
        if (clauseMatcher.find()) {
            insertPos = clauseMatcher.start();
        }

        // 检查 WHERE 是否存在
        String beforeClause = originalSql.substring(0, insertPos);
        boolean hasWhere = hasTopLevelWhere(beforeClause);

        String prefix = hasWhere ? " AND " : " WHERE ";

        return originalSql.substring(0, insertPos)
                + prefix + scopeCondition
                + " "
                + originalSql.substring(insertPos);
    }

    /**
     * 粗略判断是否包含顶层 WHERE 子句（忽略子查询中的 WHERE）
     */
    private boolean hasTopLevelWhere(String sql) {
        int depth = 0;
        // 不区分大小写的 WHERE 匹配
        String upper = sql.toUpperCase();
        for (int i = 0; i < upper.length(); i++) {
            char c = upper.charAt(i);
            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
            } else if (depth == 0 && upper.startsWith("WHERE", i)) {
                // 检查是否是完整单词
                if ((i == 0 || !Character.isLetterOrDigit(upper.charAt(i - 1)))
                        && (i + 5 >= upper.length() || !Character.isLetterOrDigit(upper.charAt(i + 5)))) {
                    return true;
                }
            }
        }
        return false;
    }
}


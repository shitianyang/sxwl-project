package com.sxwl.mybatis.interceptor;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Statement;

/**
 * SQL 慢查询监控拦截器
 *
 * <p>超过阈值的 SQL 语句输出 WARN 级别日志，用于开发/测试阶段定位慢查询。
 * 生产环境建议通过 {@code sxwl.mybatis.slow-sql-threshold=0} 关闭。</p>
 *
 * <h3>日志输出格式</h3>
 * <pre>[SLOW-SQL] 耗时=1523ms, Mapper=com.sxwl.module.system.mapper.SysUserMapper.selectList, SQL=SELECT ...</pre>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "query",
                args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class})
})
public class SxwlSqlMonitorInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(SxwlSqlMonitorInterceptor.class);

    /**
     * 慢查询阈值（毫秒），超过此值打 WARN 日志
     */
    private final long thresholdMs;

    public SxwlSqlMonitorInterceptor(long thresholdMs) {
        this.thresholdMs = thresholdMs;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (thresholdMs <= 0) {
            return invocation.proceed();
        }

        long start = System.currentTimeMillis();
        Object result = invocation.proceed();
        long elapsed = System.currentTimeMillis() - start;

        if (elapsed >= thresholdMs) {
            StatementHandler handler = (StatementHandler) invocation.getTarget();
            MetaObject meta = SystemMetaObject.forObject(handler);
            MappedStatement ms = (MappedStatement) meta.getValue("delegate.mappedStatement");
            BoundSql boundSql = handler.getBoundSql();

            log.warn("[SLOW-SQL] 耗时={}ms, Mapper={}, SQL={}",
                    elapsed,
                    ms.getId(),
                    boundSql.getSql().replaceAll("\\s+", " "));
        }

        return result;
    }
}

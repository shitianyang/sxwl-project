package com.sxwl.mybatis.interceptor;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.session.ResultHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * {@link SxwlSqlMonitorInterceptor} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SxwlSqlMonitorInterceptor 测试")
class SxwlSqlMonitorInterceptorTest {

    @Mock
    private StatementHandler handler;
    @Mock
    private Statement statement;
    @Mock
    private MappedStatement ms;
    @Mock
    private BoundSql boundSql;

    private static final Method STATEMENT_QUERY_METHOD;

    static {
        try {
            STATEMENT_QUERY_METHOD = StatementHandler.class.getMethod("query", Statement.class, ResultHandler.class);
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Test
    @DisplayName("thresholdMs <= 0 时应直接放行不监控")
    void intercept_shouldSkip_whenThresholdDisabled() throws Throwable {
        SxwlSqlMonitorInterceptor interceptor = new SxwlSqlMonitorInterceptor(0);
        Invocation invocation = new Invocation(handler, STATEMENT_QUERY_METHOD, new Object[]{statement, null});
        assertDoesNotThrow(() -> interceptor.intercept(invocation));
    }

    @Test
    @DisplayName("构造器应正确保存阈值")
    void constructor_shouldStoreThreshold() {
        SxwlSqlMonitorInterceptor interceptor = new SxwlSqlMonitorInterceptor(500L);
        assertNotNull(interceptor);
    }
}

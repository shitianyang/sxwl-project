package com.sxwl.mybatis.interceptor;

import com.sxwl.common.principal.SxwlPrincipal;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * {@link SxwlDataScopeInterceptor} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SxwlDataScopeInterceptor 测试")
class SxwlDataScopeInterceptorTest {

    @Mock
    private MappedStatement ms;
    @Mock
    private BoundSql boundSql;
    @Mock
    private Executor executor;
    @Mock
    private Configuration configuration;

    private final SxwlDataScopeInterceptor interceptor = new SxwlDataScopeInterceptor();
    private static final Method STATEMENT_PREPARE_METHOD;

    static {
        try {
            STATEMENT_PREPARE_METHOD = StatementHandler.class.getMethod("prepare", Connection.class, Integer.class);
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @BeforeEach
    void setUp() {
        // 构造 SxwlPrincipal 模拟登录用户
        SxwlPrincipal principal = new SxwlPrincipal() {
            @Override public Long getUserId() { return 1L; }
            @Override public Long getOrgId() { return 10L; }
            @Override public Set<Long> getDataScopeOrgIds() { return Set.of(10L, 20L, 30L); }
        };
        SecurityContextHolder.setContext(new SecurityContextImpl(
                new UsernamePasswordAuthenticationToken(principal, null)));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private StatementHandler createRoutingHandler() {
        try {
            // BaseStatementHandler 构造器需要 configuration
            when(ms.getConfiguration()).thenReturn(configuration);
            StatementHandler realHandler = spy(new RoutingStatementHandler(executor, ms, null, RowBounds.DEFAULT, null, boundSql));
            // 避免 proceed 调用 prepare 时因无真实数据库连接而失败
            doReturn(null).when(realHandler).prepare(any(Connection.class), anyInt());
            return realHandler;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("非 SELECT 应直接放行")
    void intercept_shouldProceed_whenNotSelect() throws Throwable {
        when(ms.getSqlCommandType()).thenReturn(SqlCommandType.INSERT);
        when(ms.getStatementType()).thenReturn(StatementType.PREPARED);
        StatementHandler realHandler = createRoutingHandler();
        Invocation invocation = new Invocation(realHandler, STATEMENT_PREPARE_METHOD, new Object[]{mock(Connection.class), 1});
        assertDoesNotThrow(() -> interceptor.intercept(invocation));
    }

    // 由于数据权限拦截器用到了反射获取 Mapper 类注解，在单元测试中难以构造 MappedStatement，
    // 通过 mock 验证基本流程
    @Test
    @DisplayName("intercept 在 mock 环境下不会 NPE")
    void intercept_shouldNotThrowNpe() {
        when(ms.getSqlCommandType()).thenReturn(SqlCommandType.SELECT);
        when(ms.getStatementType()).thenReturn(StatementType.PREPARED);
        // getDataScopeAnnotation 需要 ms.getId() 来解析 Mapper 类名
        when(ms.getId()).thenReturn("com.example.mapper.TestMapper.selectAll");
        StatementHandler realHandler = createRoutingHandler();
        assertDoesNotThrow(() -> {
            Invocation invocation = new Invocation(realHandler, STATEMENT_PREPARE_METHOD, new Object[]{mock(Connection.class), 1});
            interceptor.intercept(invocation);
        });
    }
}

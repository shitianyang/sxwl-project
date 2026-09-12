package com.sxwl.mybatis.interceptor;

import com.sxwl.common.entity.SxwlBasicField;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Invocation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * {@link SxwlAutoFillInterceptor} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SxwlAutoFillInterceptor 测试")
class SxwlAutoFillInterceptorTest {

    @Mock
    private Executor executor;
    @Mock
    private MappedStatement ms;

    private final SxwlAutoFillInterceptor interceptor = new SxwlAutoFillInterceptor();
    private static final Method EXECUTOR_UPDATE_METHOD;

    static {
        try {
            EXECUTOR_UPDATE_METHOD = Executor.class.getMethod("update", MappedStatement.class, Object.class);
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Test
    @DisplayName("参数少于 2 个时应直接放行")
    void intercept_shouldProceed_whenArgsLessThan2() throws Throwable {
        Invocation invocation = new Invocation(executor, EXECUTOR_UPDATE_METHOD, new Object[]{ms, null});
        assertDoesNotThrow(() -> interceptor.intercept(invocation));
    }

    @Test
    @DisplayName("参数非 SxwlBasicField 时应直接放行")
    void intercept_shouldProceed_whenNotBasicField() throws Throwable {
        Invocation invocation = new Invocation(executor, EXECUTOR_UPDATE_METHOD, new Object[]{ms, "string-param"});
        assertDoesNotThrow(() -> interceptor.intercept(invocation));
    }

    @Test
    @DisplayName("INSERT 时应填充审计字段")
    void intercept_shouldFillInsertFields() throws Throwable {
        TestEntity entity = new TestEntity();
        Invocation invocation = new Invocation(executor, EXECUTOR_UPDATE_METHOD, new Object[]{ms, entity});
        when(ms.getSqlCommandType()).thenReturn(SqlCommandType.INSERT);

        assertDoesNotThrow(() -> interceptor.intercept(invocation));

        assertNotNull(entity.getCreateTime());
        assertNotNull(entity.getDeleteFlag());
        assertEquals(0, entity.getDeleteFlag().intValue());
    }

    @Test
    @DisplayName("UPDATE 时应填充 updateTime")
    void intercept_shouldFillUpdateFields() throws Throwable {
        TestEntity entity = new TestEntity();
        Invocation invocation = new Invocation(executor, EXECUTOR_UPDATE_METHOD, new Object[]{ms, entity});
        when(ms.getSqlCommandType()).thenReturn(SqlCommandType.UPDATE);

        assertDoesNotThrow(() -> interceptor.intercept(invocation));

        assertNotNull(entity.getUpdateTime());
    }

    /**
     * 测试用的 SxwlBasicField 子类
     */
    private static class TestEntity extends SxwlBasicField {
    }
}

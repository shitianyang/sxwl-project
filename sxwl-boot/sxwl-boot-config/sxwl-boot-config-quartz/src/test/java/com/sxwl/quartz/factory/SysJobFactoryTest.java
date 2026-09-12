package com.sxwl.quartz.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * {@link SysJobFactory} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("SysJobFactory 测试")
class SysJobFactoryTest {

    @Test
    @DisplayName("构造器应能正常初始化")
    void constructor_shouldInitialize() {
        ApplicationContext ctx = mock(ApplicationContext.class);
        when(ctx.getAutowireCapableBeanFactory()).thenReturn(mock(org.springframework.beans.factory.config.AutowireCapableBeanFactory.class));
        SysJobFactory factory = new SysJobFactory(ctx);
        assertNotNull(factory);
    }
}

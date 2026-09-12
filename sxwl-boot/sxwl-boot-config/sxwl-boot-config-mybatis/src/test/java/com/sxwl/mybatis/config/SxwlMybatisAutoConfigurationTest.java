package com.sxwl.mybatis.config;

import com.github.pagehelper.PageInterceptor;
import com.sxwl.mybatis.interceptor.SxwlAutoFillInterceptor;
import com.sxwl.mybatis.interceptor.SxwlDataScopeInterceptor;
import com.sxwl.mybatis.interceptor.SxwlSqlMonitorInterceptor;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SxwlMybatisAutoConfiguration} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("SxwlMybatisAutoConfiguration 测试")
class SxwlMybatisAutoConfigurationTest {

    private final MybatisProperties mybatisProperties = new MybatisProperties();
    private final SxwlMybatisAutoConfiguration autoConfig = new SxwlMybatisAutoConfiguration(mybatisProperties);

    @Test
    @DisplayName("mybatisConfigurationCustomizer 应配置驼峰映射和 SLF4J 日志")
    void mybatisConfigurationCustomizer_shouldConfigureCamelCaseAndLogging() {
        org.apache.ibatis.session.Configuration ibatisConfig = new org.apache.ibatis.session.Configuration();
        autoConfig.mybatisConfigurationCustomizer().customize(ibatisConfig);
        assertTrue(ibatisConfig.isMapUnderscoreToCamelCase());
        assertEquals(Slf4jImpl.class, ibatisConfig.getLogImpl());
    }

    @Test
    @DisplayName("pageInterceptor 应创建 PageHelper 分页插件")
    void pageInterceptor_shouldCreatePageHelperPlugin() {
        PageInterceptor interceptor = autoConfig.pageInterceptor();
        assertNotNull(interceptor);
    }

    @Test
    @DisplayName("dataScopeInterceptor 应创建数据权限拦截器")
    void dataScopeInterceptor_shouldCreateInterceptor() {
        SxwlDataScopeInterceptor interceptor = autoConfig.dataScopeInterceptor();
        assertNotNull(interceptor);
    }

    @Test
    @DisplayName("autoFillInterceptor 应创建自动填充拦截器")
    void autoFillInterceptor_shouldCreateInterceptor() {
        SxwlAutoFillInterceptor interceptor = autoConfig.autoFillInterceptor();
        assertNotNull(interceptor);
    }

    @Test
    @DisplayName("sqlMonitorInterceptor 应创建 SQL 监控拦截器")
    void sqlMonitorInterceptor_shouldCreateInterceptor() {
        SxwlMybatisProperties props = new SxwlMybatisProperties();
        props.setSlowSqlThreshold(1000L);
        SxwlSqlMonitorInterceptor interceptor = autoConfig.sqlMonitorInterceptor(props);
        assertNotNull(interceptor);
    }
}

package com.sxwl.integration;

import com.sxwl.security.config.SxwlSecurityProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 启动完整性 + 配置加载集成测试
 * <p>
 * 启动完整 Spring 容器（连接真实 PostgreSQL + Redis），验证：
 * 1. Spring 容器正常启动
 * 2. 数据库连接正常
 * 3. Redis 连接正常
 * 4. 自定义配置属性绑定正确
 * 5. 关键 Bean 全部加载
 * </p>
 *
 * @author shitianyang
 * @since 0.1.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class StartupAndConfigIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SxwlSecurityProperties securityProperties;

    // ==================== 1. Spring 容器启动验证 ====================

    @Test
    void testApplicationContextLoads() {
        assertNotNull(applicationContext, "Spring 容器应成功启动");
        assertTrue(applicationContext.getBeanDefinitionCount() > 0,
                "容器中应有已注册的 Bean");
        System.out.println("✓ Spring 容器启动成功，已注册 Bean 数量: "
                + applicationContext.getBeanDefinitionCount());
    }

    // ==================== 2. 数据库连接验证 ====================

    @Test
    void testDatabaseConnection() throws Exception {
        assertNotNull(dataSource, "DataSource Bean 应已注入");
        try (Connection conn = dataSource.getConnection()) {
            assertNotNull(conn, "数据库连接不应为空");
            assertFalse(conn.isClosed(), "数据库连接应处于打开状态");
            System.out.println("✓ 数据库连接成功: " + conn.getMetaData().getURL());
        }
    }

    // ==================== 3. Redis 连接验证 ====================

    @Test
    void testRedisConnection() {
        assertNotNull(stringRedisTemplate, "StringRedisTemplate Bean 应已注入");
        try {
            String pingResult = stringRedisTemplate.getConnectionFactory()
                    .getConnection().ping();
            assertEquals("PONG", pingResult, "Redis PING 应返回 PONG");
            System.out.println("✓ Redis 连接成功: PING -> " + pingResult);
        } catch (Exception e) {
            fail("Redis 连接失败: " + e.getMessage());
        }
    }

    @Test
    void testRedisReadWrite() {
        String testKey = "integration-test:startup:ping";
        String testValue = "hello-integration-test";
        stringRedisTemplate.opsForValue().set(testKey, testValue);
        String result = stringRedisTemplate.opsForValue().get(testKey);
        assertEquals(testValue, result, "Redis 读写应一致");
        stringRedisTemplate.delete(testKey);
        System.out.println("✓ Redis 读写验证通过");
    }

    // ==================== 4. 配置属性绑定验证 ====================

    @Test
    void testSecurityPropertiesBinding() {
        assertNotNull(securityProperties, "SxwlSecurityProperties Bean 应已注入");
        assertNotNull(securityProperties.getJwtSecret(), "jwt-secret 不应为空");
        assertFalse(securityProperties.getJwtSecret().isBlank(), "jwt-secret 不应为空白");
        assertEquals(64, securityProperties.getJwtSecret().length(),
                "jwt-secret 应为 256 位十六进制（64 字符）");
        System.out.println("✓ SxwlSecurityProperties 绑定成功");
        System.out.println("  jwt-secret 长度: " + securityProperties.getJwtSecret().length());
        System.out.println("  sm2-key-rotation-interval-minutes: "
                + securityProperties.getSm2KeyRotationIntervalMinutes());
    }

    @LocalServerPort
    private int actualPort;

    @Test
    void testServerPortConfiguration() {
        // RANDOM_PORT 模式下，环境变量中的 server.port=0，实际端口从 @LocalServerPort 获取
        assertTrue(actualPort > 0, "实际运行端口应 > 0");
        assertTrue(actualPort >= 1024, "实际端口应 >= 1024");
        System.out.println("✓ server 实际运行端口: " + actualPort + " (配置: 30101，测试随机分配)");
    }

    @Test
    void testContextPathConfiguration() {
        String contextPath = applicationContext.getEnvironment()
                .getProperty("server.servlet.context-path");
        assertNotNull(contextPath, "context-path 应已配置");
        assertEquals("/sxwl-api", contextPath, "context-path 应为 /sxwl-api");
        System.out.println("✓ context-path = " + contextPath);
    }

    // ==================== 5. 关键 Bean 存在性验证 ====================

    @Test
    void testKeyBeansExist() {
        // 通过这些 Bean 的存在性，验证自动配置和各模块是否正确加载
        String[] keyBeans = {
                // 数据源 & 事务
                "dataSource",
                "transactionManager",
                // MyBatis
                "sqlSessionFactory",
                // Redis
                "stringRedisTemplate",
                // Web
                "dispatcherServlet",
                // Jackson
                "jacksonObjectMapper",
                // Security Filter Chain
                "springSecurityFilterChain",
        };
        for (String beanName : keyBeans) {
            try {
                Object bean = applicationContext.getBean(beanName);
                assertNotNull(bean, "Bean [" + beanName + "] 不应为 null");
            } catch (NoSuchBeanDefinitionException e) {
                fail("关键 Bean [" + beanName + "] 未注册: " + e.getMessage());
            }
        }
        System.out.println("✓ 所有关键 Bean 验证通过 (" + keyBeans.length + " 个)");
    }
}

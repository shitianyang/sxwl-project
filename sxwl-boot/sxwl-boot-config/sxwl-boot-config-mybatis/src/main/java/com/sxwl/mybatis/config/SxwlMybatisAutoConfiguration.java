package com.sxwl.mybatis.config;

import com.github.pagehelper.PageInterceptor;
import com.sxwl.common.constants.SxwlSystemConstants;
import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.mybatis.interceptor.SxwlAutoFillInterceptor;
import com.sxwl.mybatis.interceptor.SxwlDataScopeInterceptor;
import com.sxwl.mybatis.interceptor.SxwlSqlMonitorInterceptor;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.Properties;

/**
 * MyBatis + PageHelper 自动装配
 *
 * <p>负责：</p>
 * <ul>
 *   <li>MyBatis 核心配置（驼峰映射、SQL 日志、XML 映射文件路径）—— 硬编码，不走 YAML</li>
 *   <li>PageHelper 分页插件 —— 硬编码固定参数</li>
 *   <li>数据权限拦截器 —— 从 SecurityContext 读取 dataScopeOrgIds，不查库</li>
 *   <li>自动填充拦截器 —— INSERT/UPDATE 自动填审计字段</li>
 *   <li>慢查询监控 —— 通过 {@code sxwl.mybatis.slow-sql-threshold} 控制</li>
 * </ul>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@AutoConfiguration
@EnableConfigurationProperties({SxwlMybatisProperties.class, MybatisProperties.class})
public class SxwlMybatisAutoConfiguration {

    public SxwlMybatisAutoConfiguration(MybatisProperties mybatisProperties) {
        // XML 映射文件路径（不变量，硬编码不走 YAML）
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources("classpath*:mappers/**/*.xml");
            String[] locations = new String[resources.length];
            for (int i = 0; i < resources.length; i++) {
                locations[i] = resources[i].getURI().toString();
            }
            mybatisProperties.setMapperLocations(locations);
        } catch (IOException e) {
            throw new SxwlBusinessException(500, "无法加载 MyBatis XML 映射文件", e);
        }
    }

    // ==================== MyBatis 核心配置 ====================

    /**
     * MyBatis 全局配置（硬编码，项目约定，不随环境变化）
     */
    @Bean
    public ConfigurationCustomizer mybatisConfigurationCustomizer() {
        return config -> {
            config.setMapUnderscoreToCamelCase(true);   // 驼峰映射：create_org → createOrg
            config.setLogImpl(Slf4jImpl.class);          // SQL 日志走 SLF4J
        };
    }

    // ==================== PageHelper 分页插件 ====================

    /**
     * PageHelper 分页插件（硬编码固定行为参数）
     */
    @Bean
    public PageInterceptor pageInterceptor() {
        Properties props = new Properties();
        props.setProperty("reasonable", "false");             // 不修正非法页码
        props.setProperty("supportMethodsArguments", "true"); // 支持方法参数分页
        props.setProperty("params", "pageNum=current;pageSize=pageSize;count=countSql");
        props.setProperty("pageSizeZero", "false");           // pageSize=0 不返回全部
        props.setProperty("defaultPageSize", "10");
        props.setProperty("maxPageSize",
                String.valueOf(SxwlSystemConstants.MAX_PAGE_SIZE)); // 复用全局常量

        PageInterceptor interceptor = new PageInterceptor();
        interceptor.setProperties(props);
        return interceptor;
    }

    // ==================== 自定义拦截器 ====================

    /**
     * 数据权限拦截器
     *
     * <p>从 SecurityContext 读取 {@code SxwlPrincipal.dataScopeOrgIds}（登录时由 auth 计算好写入）。</p>
     */
    @Bean
    @ConditionalOnMissingBean
    public SxwlDataScopeInterceptor dataScopeInterceptor() {
        return new SxwlDataScopeInterceptor();
    }

    /**
     * 自动填充拦截器
     */
    @Bean
    @ConditionalOnMissingBean
    public SxwlAutoFillInterceptor autoFillInterceptor() {
        return new SxwlAutoFillInterceptor();
    }

    /**
     * SQL 慢查询监控拦截器
     *
     * <p>仅当 {@code sxwl.mybatis.slow-sql-threshold > 0} 时启用。</p>
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "sxwl.mybatis", name = "slow-sql-threshold")
    public SxwlSqlMonitorInterceptor sqlMonitorInterceptor(SxwlMybatisProperties properties) {
        return new SxwlSqlMonitorInterceptor(properties.getSlowSqlThreshold());
    }
}

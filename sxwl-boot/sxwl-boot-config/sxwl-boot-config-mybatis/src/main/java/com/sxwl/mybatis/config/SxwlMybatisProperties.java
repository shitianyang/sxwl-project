package com.sxwl.mybatis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MyBatis 配置属性
 *
 * <p>仅暴露随环境变化的配置项（如慢查询阈值）。驼峰映射、分页参数等固定约定在
 * {@link SxwlMybatisAutoConfiguration} 中硬编码。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@ConfigurationProperties(prefix = "sxwl.mybatis")
public class SxwlMybatisProperties {

    /**
     * SQL 慢查询阈值（毫秒），&lt;=0 表示关闭
     * <p>dev/test 环境建议 1000ms，prod 建议 0 或 3000ms</p>
     */
    private long slowSqlThreshold = 0;

    public long getSlowSqlThreshold() {
        return slowSqlThreshold;
    }

    public void setSlowSqlThreshold(long slowSqlThreshold) {
        this.slowSqlThreshold = slowSqlThreshold;
    }
}

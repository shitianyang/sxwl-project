package com.sxwl.mybatis.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SxwlMybatisProperties} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("SxwlMybatisProperties 测试")
class SxwlMybatisPropertiesTest {

    @Test
    @DisplayName("默认 slowSqlThreshold 应为 0")
    void defaultSlowSqlThreshold_shouldBeZero() {
        SxwlMybatisProperties props = new SxwlMybatisProperties();
        assertEquals(0L, props.getSlowSqlThreshold());
    }

    @Test
    @DisplayName("setter/getter 应正常读写")
    void testGetterSetter() {
        SxwlMybatisProperties props = new SxwlMybatisProperties();
        props.setSlowSqlThreshold(1000L);
        assertEquals(1000L, props.getSlowSqlThreshold());
    }
}

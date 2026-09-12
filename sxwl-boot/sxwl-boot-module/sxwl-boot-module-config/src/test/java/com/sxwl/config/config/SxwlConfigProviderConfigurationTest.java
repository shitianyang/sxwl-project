package com.sxwl.config.config;

import com.sxwl.common.utils.SxwlConfigHelper;
import com.sxwl.common.utils.SxwlRedisKeyUtils;
import com.sxwl.config.mapper.SysConfigInfoMapper;
import com.sxwl.config.model.dto.SysConfigDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * {@link SxwlConfigProviderConfiguration} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SxwlConfigProviderConfiguration 测试")
class SxwlConfigProviderConfigurationTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private SysConfigInfoMapper sysConfigInfoMapper;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private SxwlConfigProviderConfiguration configuration;

    @BeforeEach
    void setUp() {
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        configuration = new SxwlConfigProviderConfiguration(stringRedisTemplate, sysConfigInfoMapper);
    }

    @AfterEach
    void tearDown() {
        SxwlConfigHelper.setProvider(null);
    }

    @Test
    @DisplayName("registerProvider 应注册 Provider 并预加载配置到 Redis")
    void registerProvider_shouldRegisterAndPreload() {
        SysConfigDTO config = new SysConfigDTO();
        config.setConfigKey("sys.siteName");
        config.setConfigValue("Sxwl");
        config.setStatus(1);

        when(sysConfigInfoMapper.selectAllEnabledConfigs()).thenReturn(List.of(config));

        try (MockedStatic<SxwlRedisKeyUtils> redisKeyUtils = mockStatic(SxwlRedisKeyUtils.class)) {
            redisKeyUtils.when(() -> SxwlRedisKeyUtils.configCacheKey("sys.siteName"))
                    .thenReturn("sys:config:sys.siteName");

            configuration.registerProvider();

            verify(stringRedisTemplate.opsForValue())
                    .set("sys:config:sys.siteName", "Sxwl", 3600L, TimeUnit.SECONDS);
        }
    }

    @Test
    @DisplayName("registerProvider 预加载无配置时应跳过")
    void registerProvider_emptyConfigs_shouldSkip() {
        when(sysConfigInfoMapper.selectAllEnabledConfigs()).thenReturn(List.of());

        configuration.registerProvider();

        verify(stringRedisTemplate.opsForValue(), never()).set(anyString(), anyString(), anyLong(), any());
    }

    @Test
    @DisplayName("registerProvider 配置值为 null 时应缓存空标记")
    void registerProvider_nullValue_shouldCacheEmpty() {
        SysConfigDTO config = new SysConfigDTO();
        config.setConfigKey("sys.siteName");
        config.setConfigValue(null);
        config.setStatus(1);

        when(sysConfigInfoMapper.selectAllEnabledConfigs()).thenReturn(List.of(config));

        try (MockedStatic<SxwlRedisKeyUtils> redisKeyUtils = mockStatic(SxwlRedisKeyUtils.class)) {
            redisKeyUtils.when(() -> SxwlRedisKeyUtils.configCacheKey("sys.siteName"))
                    .thenReturn("sys:config:sys.siteName");

            configuration.registerProvider();

            verify(stringRedisTemplate.opsForValue())
                    .set("sys:config:sys.siteName", "", 300L, TimeUnit.SECONDS);
        }
    }

    @Test
    @DisplayName("registerProvider 注册的 Provider 应查询 Redis 缓存")
    void provider_shouldQueryRedis() {
        when(sysConfigInfoMapper.selectAllEnabledConfigs()).thenReturn(List.of());
        configuration.registerProvider();

        try (MockedStatic<SxwlRedisKeyUtils> redisKeyUtils = mockStatic(SxwlRedisKeyUtils.class)) {
            redisKeyUtils.when(() -> SxwlRedisKeyUtils.configCacheKey("sys.test"))
                    .thenReturn("sys:config:sys.test");
            when(valueOperations.get("sys:config:sys.test")).thenReturn("cached-value");

            String value = SxwlConfigHelper.getConfigValue("sys.test");
            assertEquals("cached-value", value);
        }
    }

    @Test
    @DisplayName("registerProvider 注册的 Provider Redis 未命中时应查询 DB")
    void provider_cacheMiss_shouldQueryDb() {
        when(sysConfigInfoMapper.selectAllEnabledConfigs()).thenReturn(List.of());
        configuration.registerProvider();

        SysConfigDTO config = new SysConfigDTO();
        config.setConfigKey("sys.test");
        config.setConfigValue("db-value");
        config.setStatus(1);

        try (MockedStatic<SxwlRedisKeyUtils> redisKeyUtils = mockStatic(SxwlRedisKeyUtils.class)) {
            redisKeyUtils.when(() -> SxwlRedisKeyUtils.configCacheKey("sys.test"))
                    .thenReturn("sys:config:sys.test");
            when(valueOperations.get("sys:config:sys.test")).thenReturn(null);
            when(sysConfigInfoMapper.getConfigByKey("sys.test")).thenReturn(config);

            String value = SxwlConfigHelper.getConfigValue("sys.test");
            assertEquals("db-value", value);

            verify(valueOperations).set("sys:config:sys.test", "db-value", 3600L, TimeUnit.SECONDS);
        }
    }

    @Test
    @DisplayName("registerProvider 注册的 Provider 配置禁用时应返回 null")
    void provider_disabledConfig_shouldReturnNull() {
        when(sysConfigInfoMapper.selectAllEnabledConfigs()).thenReturn(List.of());
        configuration.registerProvider();

        SysConfigDTO config = new SysConfigDTO();
        config.setConfigKey("sys.test");
        config.setConfigValue("value");
        config.setStatus(0);

        try (MockedStatic<SxwlRedisKeyUtils> redisKeyUtils = mockStatic(SxwlRedisKeyUtils.class)) {
            redisKeyUtils.when(() -> SxwlRedisKeyUtils.configCacheKey("sys.test"))
                    .thenReturn("sys:config:sys.test");
            when(valueOperations.get("sys:config:sys.test")).thenReturn(null);
            when(sysConfigInfoMapper.getConfigByKey("sys.test")).thenReturn(config);

            String value = SxwlConfigHelper.getConfigValue("sys.test");
            assertNull(value);
        }
    }
}

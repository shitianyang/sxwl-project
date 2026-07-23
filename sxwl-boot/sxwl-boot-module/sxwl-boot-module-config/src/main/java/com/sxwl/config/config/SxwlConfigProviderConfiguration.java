package com.sxwl.config.config;

import com.sxwl.common.utils.SxwlConfigHelper;
import com.sxwl.common.utils.SxwlRedisKeyUtils;
import com.sxwl.config.mapper.SysConfigInfoMapper;
import com.sxwl.config.model.dto.SysConfigDTO;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * SxwlConfigHelper Provider 配置
 *
 * <p>注册 {@link SxwlConfigHelper.ConfigProvider} 实现，提供带 Redis 缓存的参数查询能力。
 * Redis 缓存 TTL=1小时，DB 查询结果自动回填缓存。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@Configuration
@ConditionalOnClass({StringRedisTemplate.class, SysConfigInfoMapper.class})
public class SxwlConfigProviderConfiguration {

    private static final Logger log = LoggerFactory.getLogger(SxwlConfigProviderConfiguration.class);
    private static final long CACHE_TTL_SECONDS = 3600L;

    private final StringRedisTemplate stringRedisTemplate;
    private final SysConfigInfoMapper sysConfigInfoMapper;

    public SxwlConfigProviderConfiguration(StringRedisTemplate stringRedisTemplate,
                                           SysConfigInfoMapper sysConfigInfoMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.sysConfigInfoMapper = sysConfigInfoMapper;
    }

    @PostConstruct
    public void registerProvider() {
        SxwlConfigHelper.setProvider(configKey -> {
            // 1. 优先查 Redis 缓存
            String cacheKey = SxwlRedisKeyUtils.configCacheKey(configKey);
            String cachedValue = stringRedisTemplate.opsForValue().get(cacheKey);
            if (cachedValue != null) {
                return cachedValue;
            }

            // 2. 缓存未命中，查 DB
            SysConfigDTO config = sysConfigInfoMapper.getConfigByKey(configKey);
            if (config == null || config.getStatus() == null || config.getStatus() != 1) {
                return null;
            }

            String value = config.getConfigValue();

            // 3. 回填缓存（含空值保护：value 为 null 时缓存空标记 5 分钟，防止缓存穿透）
            if (value != null) {
                stringRedisTemplate.opsForValue().set(cacheKey, value, CACHE_TTL_SECONDS, TimeUnit.SECONDS);
            } else {
                stringRedisTemplate.opsForValue().set(cacheKey, "", 300, TimeUnit.SECONDS);
            }

            return value;
        });

        log.info("SxwlConfigHelper.ConfigProvider 已注册（Redis + DB 双缓存）");

        // 预加载所有已启用配置到 Redis，确保缓存管理页面可见
        preloadAllConfigs();
    }

    /**
     * 预加载所有已启用配置到 Redis 缓存
     */
    private void preloadAllConfigs() {
        List<SysConfigDTO> allConfigs = sysConfigInfoMapper.selectAllEnabledConfigs();
        if (allConfigs == null || allConfigs.isEmpty()) {
            log.info("系统参数表为空，跳过预加载");
            return;
        }

        for (SysConfigDTO config : allConfigs) {
            String cacheKey = SxwlRedisKeyUtils.configCacheKey(config.getConfigKey());
            String value = config.getConfigValue();
            if (value != null) {
                stringRedisTemplate.opsForValue().set(cacheKey, value, CACHE_TTL_SECONDS, TimeUnit.SECONDS);
            } else {
                stringRedisTemplate.opsForValue().set(cacheKey, "", 300, TimeUnit.SECONDS);
            }
        }
        log.info("系统参数预加载完成，共 {} 条", allConfigs.size());
    }
}

package com.sxwl.redis.config;

import com.sxwl.redis.aspect.SxwlRepeatSubmitAspect;
import com.sxwl.redis.helper.SxwlRedisHelper;
import com.sxwl.redis.lock.SxwlRedisLockManager;
import com.sxwl.redis.rate.SxwlSlidingWindowRateLimiter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 自动装配
 *
 * <p>负责 Redis 序列化配置及所有工具类 Bean 的注册。</p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@AutoConfiguration
@EnableConfigurationProperties(SxwlRedisProperties.class)
public class SxwlRedisAutoConfiguration {

    // ==================== 序列化 ====================

    /**
     * RedisTemplate：Key 用 String，Value 用 Jackson2Json
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Key 用 String 序列化（可读性好）
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // Value 用 Jackson2Json（自动处理对象/集合/基本类型）
        Jackson2JsonRedisSerializer<Object> jsonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    // ==================== 工具类 ====================

    @Bean
    @ConditionalOnMissingBean
    public SxwlRedisHelper sxwlRedisHelper(StringRedisTemplate stringRedisTemplate) {
        return new SxwlRedisHelper(stringRedisTemplate);
    }

    // ==================== 分布式锁 ====================

    @Bean
    @ConditionalOnMissingBean
    public SxwlRedisLockManager sxwlRedisLockManager(SxwlRedisHelper redisHelper) {
        return new SxwlRedisLockManager(redisHelper);
    }

    // ==================== 限流 ====================

    @Bean
    @ConditionalOnMissingBean
    public SxwlSlidingWindowRateLimiter sxwlSlidingWindowRateLimiter(SxwlRedisHelper redisHelper) {
        return new SxwlSlidingWindowRateLimiter(redisHelper);
    }

    // ==================== 防重复提交 ====================

    @Bean
    @ConditionalOnMissingBean
    public SxwlRepeatSubmitAspect sxwlRepeatSubmitAspect(SxwlRedisHelper redisHelper) {
        return new SxwlRepeatSubmitAspect(redisHelper);
    }
}

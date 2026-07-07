package com.sxwl.redis.rate;

import com.sxwl.redis.helper.SxwlRedisHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 滑动窗口限流器
 *
 * <p>基于 Redis ZSET 实现，「接口限流」。</p>
 *
 * <h3>算法</h3>
 * <pre>
 * 1. ZREMRANGEBYSCORE key 0 (now - window)
 * 2. ZCARD key → 统计窗口内请求数
 * 3. 若 count &lt; maxCount → ZADD 当前请求 → 放行
 * 4. 否则 → 限流拒绝
 * </pre>
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * boolean allowed = rateLimiter.tryAcquire("rate:login:192.168.1.1", 10, 60);
 * if (!allowed) {
 *     throw new SxwlBusinessException("请求过于频繁，请稍后重试");
 * }
 * }</pre>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public class SxwlSlidingWindowRateLimiter {

    private static final Logger log = LoggerFactory.getLogger(SxwlSlidingWindowRateLimiter.class);

    private final SxwlRedisHelper redisHelper;

    public SxwlSlidingWindowRateLimiter(SxwlRedisHelper redisHelper) {
        this.redisHelper = redisHelper;
    }

    /**
     * 尝试获取许可
     *
     * @param key           限流 Key（建议格式：rate:{维度}:{标识}，如 rate:login:192.168.1.1）
     * @param maxCount      窗口内最大请求数
     * @param windowSeconds 窗口大小（秒）
     * @return true=允许通过，false=被限流
     */
    public boolean tryAcquire(String key, long maxCount, long windowSeconds) {
        long now = System.currentTimeMillis();
        long windowStart = now - windowSeconds * 1000;

        // 1. 删除窗口外的过期记录
        redisHelper.zremrangeByScore(key, 0, windowStart);

        // 2. 统计窗口内请求数
        Long count = redisHelper.zcard(key);

        if (count != null && count < maxCount) {
            // 3. 添加当前请求记录（member = now + 随机后缀防覆盖）
            String member = now + ":" + ThreadLocalRandom.current().nextLong();
            redisHelper.zadd(key, member, now);

            // 4. 设置 Key 过期时间（窗口 + 缓冲 10 秒）
            redisHelper.expire(key, Duration.ofSeconds(windowSeconds + 10));

            log.debug("限流放行: key={}, count={}/{}, window={}s", key, count + 1, maxCount, windowSeconds);
            return true;
        }

        log.warn("限流拒绝: key={}, count={}/{}, window={}s", key, count, maxCount, windowSeconds);
        return false;
    }
}

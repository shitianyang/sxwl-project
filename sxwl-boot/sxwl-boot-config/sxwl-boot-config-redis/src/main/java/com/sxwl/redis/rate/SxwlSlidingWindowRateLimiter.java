package com.sxwl.redis.rate;

import com.sxwl.redis.helper.SxwlRedisHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
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
        String member = now + ":" + ThreadLocalRandom.current().nextLong();

        // Lua 脚本原子执行：清理过期记录 + 计数判断 + 添加记录
        // KEYS[1] = redis key
        // ARGV[1] = windowStart (毫秒)
        // ARGV[2] = maxCount
        // ARGV[3] = member (当前请求唯一标识)
        // ARGV[4] = now (当前时间戳毫秒)
        // ARGV[5] = ttlSeconds (Key 过期时间)
        String script = """
                redis.call('ZREMRANGEBYSCORE', KEYS[1], 0, tonumber(ARGV[1]))
                local count = redis.call('ZCARD', KEYS[1])
                if tonumber(count) < tonumber(ARGV[2]) then
                    redis.call('ZADD', KEYS[1], tonumber(ARGV[4]), ARGV[3])
                    redis.call('EXPIRE', KEYS[1], tonumber(ARGV[5]))
                    return 1
                end
                return 0
                """;

        Boolean result = redisHelper.executeScript(script, List.of(key),
                String.valueOf(windowStart),
                String.valueOf(maxCount),
                member,
                String.valueOf(now),
                String.valueOf(windowSeconds + 10));

        boolean allowed = Boolean.TRUE.equals(result);
        if (allowed) {
            log.debug("限流放行: key={}, window={}s", key, windowSeconds);
        } else {
            log.warn("限流拒绝: key={}, window={}s", key, windowSeconds);
        }
        return allowed;
    }
}

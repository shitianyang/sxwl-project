package com.sxwl.redis.lock;

import com.sxwl.redis.helper.SxwlRedisHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * 分布式锁管理器
 *
 * <p>基于 Redis SETNX + Lua 解锁实现，用于并发控制（如 Token 刷新）等场景。</p>
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * // 非阻塞获取
 * try (SxwlRedisLock lock = lockManager.tryLock("refresh:123", 5)) {
 *     if (lock != null) {
 *         // 执行业务逻辑
 *     }
 * }
 *
 * // 阻塞获取
 * try (SxwlRedisLock lock = lockManager.lock("refresh:123", 5, 3)) {
 *     // 执行业务逻辑
 * }
 * }</pre>
 *
 * <h3>设计决策</h3>
 * <ul>
 *   <li>不实现看门狗续期：当前场景锁持有时间短（毫秒级），不需要</li>
 *   <li>锁值用 UUID 防误删：解锁时校验 lockValue 一致才 DEL，防止线程 A 的锁被线程 B 误删</li>
 * </ul>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public class SxwlRedisLockManager {

    private static final Logger log = LoggerFactory.getLogger(SxwlRedisLockManager.class);

    private final SxwlRedisHelper redisHelper;

    public SxwlRedisLockManager(SxwlRedisHelper redisHelper) {
        this.redisHelper = redisHelper;
    }

    /**
     * 尝试非阻塞获取锁
     *
     * @param key     锁 Key
     * @param timeout 锁持有超时（秒）
     * @return 获取成功返回锁实例（记得 try-with-resources），失败返回 null
     */
    public SxwlRedisLock tryLock(String key, long timeout) {
        String lockValue = UUID.randomUUID().toString();
        Boolean success = redisHelper.setIfAbsent(key, lockValue, Duration.ofSeconds(timeout));
        if (Boolean.TRUE.equals(success)) {
            log.debug("获取分布式锁成功: key={}", key);
            return new SxwlRedisLock(redisHelper, key, lockValue);
        }
        log.debug("获取分布式锁失败（已被占用）: key={}", key);
        return null;
    }

    /**
     * 阻塞获取锁
     *
     * @param key         锁 Key
     * @param timeout     锁持有超时（秒）
     * @param waitSeconds 最大等待时间（秒）
     * @return 锁实例（实现 AutoCloseable）
     * @throws com.sxwl.common.exception.SxwlBusinessException 等待超时
     */
    public SxwlRedisLock lock(String key, long timeout, long waitSeconds) {
        long deadline = System.currentTimeMillis() + waitSeconds * 1000;
        while (System.currentTimeMillis() < deadline) {
            SxwlRedisLock lock = tryLock(key, timeout);
            if (lock != null) {
                return lock;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new com.sxwl.common.exception.SxwlBusinessException(500, "获取分布式锁被中断: " + key, e);
            }
        }
        throw new com.sxwl.common.exception.SxwlBusinessException("获取分布式锁超时: " + key);
    }

    /**
     * 分布式锁实例
     */
    public static class SxwlRedisLock implements AutoCloseable {

        private final SxwlRedisHelper redisHelper;
        private final String key;
        private final String lockValue;

        private SxwlRedisLock(SxwlRedisHelper redisHelper, String key, String lockValue) {
            this.redisHelper = redisHelper;
            this.key = key;
            this.lockValue = lockValue;
        }

        @Override
        public void close() {
            // 使用 Lua 脚本原子解锁：仅当 lockValue 匹配时才 DEL
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            redisHelper.executeScript(script, List.of(key), lockValue);
            log.debug("释放分布式锁: key={}", key);
        }
    }
}

package com.sxwl.integration;

import com.sxwl.redis.helper.SxwlRedisHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Redis 集成测试
 * <p>
 * 连接真实 Redis（localhost:30011），验证：
 * 1. String 读写操作
 * 2. SxwlRedisHelper 封装层正确性
 * 3. Hash 操作
 * 4. Set 操作
 * 5. ZSet 操作（限流场景）
 * 6. 过期时间（TTL）
 * 7. SETNX（分布式锁基础）
 * </p>
 *
 * @author shitianyang
 * @since 0.1.0
 */
@SpringBootTest
@ActiveProfiles("test")
class RedisIntegrationTest {

    private static final String TEST_PREFIX = "integration-test:";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SxwlRedisHelper redisHelper;

    @AfterEach
    void cleanup() {
        // 清理测试用的 Key
        Set<String> testKeys = stringRedisTemplate.keys(TEST_PREFIX + "*");
        if (testKeys != null && !testKeys.isEmpty()) {
            stringRedisTemplate.delete(testKeys);
        }
    }

    // ==================== String 操作 ====================

    @Test
    void testSetAndGet() {
        String key = TEST_PREFIX + "string:basic";
        String value = "hello-redis-integration";

        redisHelper.set(key, value, Duration.ofMinutes(1));
        Optional<String> result = redisHelper.get(key);

        assertTrue(result.isPresent(), "值应存在");
        assertEquals(value, result.get(), "读写应一致");
        System.out.println("✓ Redis set/get 成功: " + key + " = " + result.get());
    }

    @Test
    void testGetNonExistentKey() {
        Optional<String> result = redisHelper.get(TEST_PREFIX + "non-existent-key");
        assertTrue(result.isEmpty(), "不存在的 Key 应返回 Optional.empty()");
        System.out.println("✓ 查询不存在 Key 返回 empty");
    }

    @Test
    void testSetIfAbsent() {
        String key = TEST_PREFIX + "string:setnx";

        // 第一次应成功
        Boolean first = redisHelper.setIfAbsent(key, "first", Duration.ofMinutes(1));
        assertTrue(first, "SETNX 首次应成功");

        // 第二次应失败
        Boolean second = redisHelper.setIfAbsent(key, "second", Duration.ofMinutes(1));
        assertFalse(second, "SETNX 重复应失败");

        // 值应保持第一次的
        assertEquals("first", redisHelper.get(key).orElse(null));
        System.out.println("✓ SETNX 分布式锁语义正确");
    }

    @Test
    void testDelete() {
        String key = TEST_PREFIX + "string:delete";
        redisHelper.set(key, "to-be-deleted", Duration.ofMinutes(1));

        Boolean deleted = redisHelper.delete(key);
        assertTrue(deleted, "删除应返回 true");

        Optional<String> result = redisHelper.get(key);
        assertTrue(result.isEmpty(), "删除后 Key 应不存在");
        System.out.println("✓ Redis delete 成功");
    }

    @Test
    void testExists() {
        String key = TEST_PREFIX + "string:exists";
        assertFalse(redisHelper.exists(key), "不存在的 Key 应返回 false");

        redisHelper.set(key, "exists", Duration.ofMinutes(1));
        assertTrue(redisHelper.exists(key), "存在的 Key 应返回 true");
        System.out.println("✓ Redis exists 正确");
    }

    @Test
    void testIncrement() {
        String key = TEST_PREFIX + "counter:test";

        // 首次 increment，从 0 开始
        Long val1 = redisHelper.increment(key, Duration.ofMinutes(1));
        assertEquals(1L, val1, "首次 increment 应为 1");

        Long val2 = redisHelper.increment(key);
        assertEquals(2L, val2, "再次 increment 应为 2");

        Long val3 = redisHelper.increment(key);
        assertEquals(3L, val3, "第三次 increment 应为 3");
        System.out.println("✓ Redis INCR 正确: " + val1 + " -> " + val2 + " -> " + val3);
    }

    @Test
    void testTtlExpire() {
        String key = TEST_PREFIX + "ttl:short";
        redisHelper.set(key, "short-lived", Duration.ofSeconds(2));

        // 立即检查 TTL
        Long ttl = redisHelper.getExpire(key);
        assertTrue(ttl > 0, "TTL 应 > 0");
        assertTrue(ttl <= 2, "TTL 应 <= 2 秒");

        // 设置更短的过期时间
        Boolean expired = redisHelper.expire(key, Duration.ofSeconds(1));
        assertTrue(expired, "expire 操作应返回 true");
        System.out.println("✓ Redis TTL/expire 正确: 初始 TTL=" + ttl + "s");
    }

    // ==================== Hash 操作 ====================

    @Test
    void testHashOperations() {
        String key = TEST_PREFIX + "hash:user";

        // hset
        redisHelper.hset(key, "name", "张三");
        redisHelper.hset(key, "age", "28");
        redisHelper.hset(key, "role", "admin");

        // hget
        assertEquals("张三", redisHelper.hget(key, "name"));
        assertEquals("28", redisHelper.hget(key, "age"));
        assertNull(redisHelper.hget(key, "non-existent-field"));

        // hlen
        assertEquals(3L, redisHelper.hlen(key));

        // hgetAll
        Map<String, String> all = redisHelper.hgetAll(key);
        assertEquals(3, all.size());
        assertEquals("张三", all.get("name"));

        // hdel
        redisHelper.hdel(key, "age");
        assertEquals(2L, redisHelper.hlen(key));
        assertNull(redisHelper.hget(key, "age"));

        System.out.println("✓ Redis Hash 操作全部正确");
    }

    // ==================== Set 操作 ====================

    @Test
    void testSetOperations() {
        String key = TEST_PREFIX + "set:tags";

        // sadd
        Long added = redisHelper.sadd(key, "java", "spring", "redis", "postgresql");
        assertEquals(4L, added);

        // 重复添加
        Long addedAgain = redisHelper.sadd(key, "java");
        assertEquals(0L, addedAgain, "重复添加应返回 0");

        // sismember
        assertTrue(redisHelper.sismember(key, "java"));
        assertFalse(redisHelper.sismember(key, "python"));

        // smembers
        Set<String> members = redisHelper.smembers(key);
        assertEquals(4, members.size());
        assertTrue(members.contains("spring"));

        // srem
        redisHelper.srem(key, "postgresql");
        assertEquals(3, redisHelper.smembers(key).size());

        System.out.println("✓ Redis Set 操作全部正确");
    }

    // ==================== ZSet 操作（限流/滑动窗口场景） ====================

    @Test
    void testZSetOperations() {
        String key = TEST_PREFIX + "zset:rate-limit";

        // 模拟时间窗口内的请求记录
        long now = System.currentTimeMillis();
        redisHelper.zadd(key, "req-1", now);
        redisHelper.zadd(key, "req-2", now + 100);
        redisHelper.zadd(key, "req-3", now + 200);

        // zcard
        assertEquals(3L, redisHelper.zcard(key));

        // 移除窗口外的旧记录
        redisHelper.zremrangeByScore(key, 0, now - 1);
        assertEquals(3L, redisHelper.zcard(key), "窗口内的记录不应被移除");

        // 移除窗口内的记录
        redisHelper.zremrangeByScore(key, 0, now + 300);
        assertEquals(0L, redisHelper.zcard(key), "所有记录应被移除");

        System.out.println("✓ Redis ZSet 滑动窗口操作正确");
    }
}

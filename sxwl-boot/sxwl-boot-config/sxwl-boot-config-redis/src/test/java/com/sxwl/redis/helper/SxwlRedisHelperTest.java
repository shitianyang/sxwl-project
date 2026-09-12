package com.sxwl.redis.helper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.*;

import java.time.Duration;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * {@link SxwlRedisHelper} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
class SxwlRedisHelperTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    private SxwlRedisHelper redisHelper;

    @BeforeEach
    void setUp() {
        redisHelper = new SxwlRedisHelper(stringRedisTemplate);
    }

    // ==================== String 操作 ====================

    @Test
    @DisplayName("set 调用 opsForValue().set")
    void set_shouldDelegate() {
        ValueOperations<String, String> ops = mock(ValueOperations.class);
        when(stringRedisTemplate.opsForValue()).thenReturn(ops);

        redisHelper.set("key", "value", Duration.ofSeconds(10));

        verify(ops).set("key", "value", Duration.ofSeconds(10));
    }

    @Test
    @DisplayName("setIfAbsent 调用 opsForValue().setIfAbsent")
    void setIfAbsent_shouldDelegate() {
        ValueOperations<String, String> ops = mock(ValueOperations.class);
        when(stringRedisTemplate.opsForValue()).thenReturn(ops);
        when(ops.setIfAbsent("key", "value", Duration.ofSeconds(10))).thenReturn(true);

        Boolean result = redisHelper.setIfAbsent("key", "value", Duration.ofSeconds(10));

        assertTrue(result);
    }

    @Test
    @DisplayName("get 返回 Optional.of(value)")
    void get_shouldReturnOptional() {
        ValueOperations<String, String> ops = mock(ValueOperations.class);
        when(stringRedisTemplate.opsForValue()).thenReturn(ops);
        when(ops.get("key")).thenReturn("value");

        Optional<String> result = redisHelper.get("key");
        assertTrue(result.isPresent());
        assertEquals("value", result.get());
    }

    @Test
    @DisplayName("get 返回 Optional.empty() 当 value 为 null")
    void get_shouldReturnEmpty_whenValueNull() {
        ValueOperations<String, String> ops = mock(ValueOperations.class);
        when(stringRedisTemplate.opsForValue()).thenReturn(ops);
        when(ops.get("key")).thenReturn(null);

        Optional<String> result = redisHelper.get("key");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("delete 调用 template.delete")
    void delete_shouldDelegate() {
        when(stringRedisTemplate.delete("key")).thenReturn(true);
        assertTrue(redisHelper.delete("key"));
    }

    @Test
    @DisplayName("批量 delete 调用 template.delete")
    void deleteCollection_shouldDelegate() {
        List<String> keys = List.of("k1", "k2");
        when(stringRedisTemplate.delete(keys)).thenReturn(2L);
        assertEquals(2L, redisHelper.delete(keys));
    }

    @Test
    @DisplayName("increment 调用 opsForValue().increment")
    void increment_shouldDelegate() {
        ValueOperations<String, String> ops = mock(ValueOperations.class);
        when(stringRedisTemplate.opsForValue()).thenReturn(ops);
        when(ops.increment("key")).thenReturn(5L);

        assertEquals(5L, redisHelper.increment("key"));
    }

    @Test
    @DisplayName("increment 带 TTL 首次调用时设置过期")
    void incrementWithTtl_shouldSetExpireOnFirstCall() {
        ValueOperations<String, String> ops = mock(ValueOperations.class);
        when(stringRedisTemplate.opsForValue()).thenReturn(ops);
        when(ops.increment("key")).thenReturn(1L);

        redisHelper.increment("key", Duration.ofSeconds(60));

        verify(stringRedisTemplate).expire("key", Duration.ofSeconds(60));
    }

    @Test
    @DisplayName("increment 带 TTL 非首次调用不设置过期")
    void incrementWithTtl_shouldNotSetExpireOnSubsequentCalls() {
        ValueOperations<String, String> ops = mock(ValueOperations.class);
        when(stringRedisTemplate.opsForValue()).thenReturn(ops);
        when(ops.increment("key")).thenReturn(3L);

        redisHelper.increment("key", Duration.ofSeconds(60));

        verify(stringRedisTemplate, never()).expire(anyString(), any());
    }

    @Test
    @DisplayName("exists 调用 template.hasKey")
    void exists_shouldDelegate() {
        when(stringRedisTemplate.hasKey("key")).thenReturn(true);
        assertTrue(redisHelper.exists("key"));
    }

    // ==================== Hash 操作 ====================

    @Test
    @DisplayName("hset 调用 opsForHash().put")
    void hset_shouldDelegate() {
        HashOperations<String, Object, Object> ops = mock(HashOperations.class);
        when(stringRedisTemplate.opsForHash()).thenReturn(ops);

        redisHelper.hset("key", "field", "value");

        verify(ops).put("key", "field", "value");
    }

    @Test
    @DisplayName("hmset 调用 opsForHash().putAll")
    void hmset_shouldDelegate() {
        HashOperations<String, Object, Object> ops = mock(HashOperations.class);
        when(stringRedisTemplate.opsForHash()).thenReturn(ops);

        Map<String, String> map = Map.of("f1", "v1", "f2", "v2");
        redisHelper.hmset("key", map);

        verify(ops).putAll(eq("key"), anyMap());
    }

    @Test
    @DisplayName("hgetAll 应返回转换后的 Map")
    void hgetAll_shouldReturnMap() {
        HashOperations<String, Object, Object> ops = mock(HashOperations.class);
        when(stringRedisTemplate.opsForHash()).thenReturn(ops);
        Map<Object, Object> raw = new LinkedHashMap<>();
        raw.put("f1", "v1");
        raw.put("f2", "v2");
        when(ops.entries("key")).thenReturn(raw);

        Map<String, String> result = redisHelper.hgetAll("key");
        assertEquals(2, result.size());
        assertEquals("v1", result.get("f1"));
        assertEquals("v2", result.get("f2"));
    }

    @Test
    @DisplayName("hget 调用 opsForHash().get")
    void hget_shouldDelegate() {
        HashOperations<String, Object, Object> ops = mock(HashOperations.class);
        when(stringRedisTemplate.opsForHash()).thenReturn(ops);
        when(ops.get("key", "field")).thenReturn("value");

        assertEquals("value", redisHelper.hget("key", "field"));
    }

    @Test
    @DisplayName("hdel 调用 opsForHash().delete")
    void hdel_shouldDelegate() {
        HashOperations<String, Object, Object> ops = mock(HashOperations.class);
        when(stringRedisTemplate.opsForHash()).thenReturn(ops);
        when(ops.delete("key", "f1", "f2")).thenReturn(2L);

        assertEquals(2L, redisHelper.hdel("key", "f1", "f2"));
    }

    // ==================== Set 操作 ====================

    @Test
    @DisplayName("sadd 调用 opsForSet().add")
    void sadd_shouldDelegate() {
        SetOperations<String, String> ops = mock(SetOperations.class);
        when(stringRedisTemplate.opsForSet()).thenReturn(ops);
        when(ops.add("key", "a", "b")).thenReturn(2L);

        assertEquals(2L, redisHelper.sadd("key", "a", "b"));
    }

    @Test
    @DisplayName("smembers 调用 opsForSet().members")
    void smembers_shouldDelegate() {
        SetOperations<String, String> ops = mock(SetOperations.class);
        when(stringRedisTemplate.opsForSet()).thenReturn(ops);
        when(ops.members("key")).thenReturn(Set.of("a", "b"));

        assertEquals(Set.of("a", "b"), redisHelper.smembers("key"));
    }

    @Test
    @DisplayName("sismember 调用 opsForSet().isMember")
    void sismember_shouldDelegate() {
        SetOperations<String, String> ops = mock(SetOperations.class);
        when(stringRedisTemplate.opsForSet()).thenReturn(ops);
        when(ops.isMember("key", "member")).thenReturn(true);

        assertTrue(redisHelper.sismember("key", "member"));
    }

    // ==================== ZSet 操作 ====================

    @Test
    @DisplayName("zadd 调用 opsForZSet().add")
    void zadd_shouldDelegate() {
        ZSetOperations<String, String> ops = mock(ZSetOperations.class);
        when(stringRedisTemplate.opsForZSet()).thenReturn(ops);
        when(ops.add("key", "member", 1.0)).thenReturn(true);

        assertTrue(redisHelper.zadd("key", "member", 1.0));
    }

    @Test
    @DisplayName("zcard 调用 opsForZSet().zCard")
    void zcard_shouldDelegate() {
        ZSetOperations<String, String> ops = mock(ZSetOperations.class);
        when(stringRedisTemplate.opsForZSet()).thenReturn(ops);
        when(ops.zCard("key")).thenReturn(5L);

        assertEquals(5L, redisHelper.zcard("key"));
    }

    // ==================== 通用操作 ====================

    @Test
    @DisplayName("expire 调用 template.expire")
    void expire_shouldDelegate() {
        when(stringRedisTemplate.expire("key", Duration.ofSeconds(10))).thenReturn(true);
        assertTrue(redisHelper.expire("key", Duration.ofSeconds(10)));
    }

    @Test
    @DisplayName("executeScript 应执行 Lua 脚本")
    void executeScript_shouldDelegate() {
        when(stringRedisTemplate.execute(any(org.springframework.data.redis.core.script.DefaultRedisScript.class),
                eq(List.of("key")), eq("arg1"))).thenReturn(true);

        assertTrue(redisHelper.executeScript("return 1", List.of("key"), "arg1"));
    }
}

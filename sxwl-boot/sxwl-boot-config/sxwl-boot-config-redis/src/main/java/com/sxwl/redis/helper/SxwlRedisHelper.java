package com.sxwl.redis.helper;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis 操作封装
 *
 * <p>统一封装 {@link StringRedisTemplate} 的常用操作，简化上层模块的 Redis 调用。
 * 所有 Key 不加自动前缀（前缀由 {@code SxwlRedisKeyUtils} 统一管理）。</p>
 *
 * <h3>设计原则</h3>
 * <ul>
 *   <li>只封装 StringRedisTemplate，统一 String 序列化策略</li>
 *   <li>方法命名直观：set/get/delete/hset/hget/...，与 Redis 命令一一对应</li>
 *   <li>异常不吞掉，直接向上抛（由调用方决定如何处理）</li>
 * </ul>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public class SxwlRedisHelper {

    private final StringRedisTemplate stringRedisTemplate;

    public SxwlRedisHelper(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    // ==================== String 操作 ====================

    /**
     * 设置字符串值，带过期时间
     */
    public void set(String key, String value, Duration ttl) {
        stringRedisTemplate.opsForValue().set(key, value, ttl);
    }

    /**
     * SETNX：仅当 Key 不存在时设置，成功返回 true
     */
    public Boolean setIfAbsent(String key, String value, Duration ttl) {
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value, ttl);
    }

    /**
     * 获取字符串值
     */
    public Optional<String> get(String key) {
        return Optional.ofNullable(stringRedisTemplate.opsForValue().get(key));
    }

    /**
     * 判断 Key 是否存在
     */
    public Boolean exists(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * 删除单个 Key
     */
    public Boolean delete(String key) {
        return stringRedisTemplate.delete(key);
    }

    /**
     * 批量删除 Key
     */
    public Long delete(Collection<String> keys) {
        return stringRedisTemplate.delete(keys);
    }

    /**
     * 自增 1
     */
    public Long increment(String key) {
        return stringRedisTemplate.opsForValue().increment(key);
    }

    /**
     * 自增 1，首次调用时设置 TTL
     */
    public Long increment(String key, Duration ttl) {
        Long value = stringRedisTemplate.opsForValue().increment(key);
        if (value != null && value == 1) {
            stringRedisTemplate.expire(key, ttl);
        }
        return value;
    }

    /**
     * 设置 Key 过期时间
     */
    public Boolean expire(String key, Duration ttl) {
        return stringRedisTemplate.expire(key, ttl);
    }

    /**
     * 获取 Key 剩余 TTL（秒）
     */
    public Long getExpire(String key) {
        return stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    // ==================== Hash 操作 ====================

    /**
     * 设置 Hash 单个字段
     */
    public void hset(String key, String field, String value) {
        stringRedisTemplate.opsForHash().put(key, field, value);
    }

    /**
     * 批量设置 Hash 字段
     */
    public void hmset(String key, Map<String, String> map) {
        stringRedisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 获取 Hash 全部字段
     */
    public Map<String, String> hgetAll(String key) {
        return stringRedisTemplate.<String, String>opsForHash().entries(key)
                .entrySet().stream()
                .collect(LinkedHashMap::new,
                        (m, e) -> m.put(e.getKey().toString(), Objects.toString(e.getValue(), null)),
                        LinkedHashMap::putAll);
    }

    /**
     * 获取 Hash 单个字段
     */
    public String hget(String key, String field) {
        Object value = stringRedisTemplate.opsForHash().get(key, field);
        return value != null ? value.toString() : null;
    }

    /**
     * 删除 Hash 字段
     */
    public Long hdel(String key, String... fields) {
        return stringRedisTemplate.opsForHash().delete(key, (Object[]) fields);
    }

    /**
     * Hash 字段数量
     */
    public Long hlen(String key) {
        return stringRedisTemplate.opsForHash().size(key);
    }

    /**
     * HSCAN 扫描 Hash
     */
    public Cursor<Map.Entry<Object, Object>> hscan(String key, String pattern) {
        return stringRedisTemplate.opsForHash().scan(key,
                org.springframework.data.redis.core.ScanOptions.scanOptions()
                        .match(pattern)
                        .build());
    }

    // ==================== Set 操作 ====================

    /**
     * 向 Set 添加成员
     */
    public Long sadd(String key, String... members) {
        return stringRedisTemplate.opsForSet().add(key, members);
    }

    /**
     * 获取 Set 所有成员
     */
    public Set<String> smembers(String key) {
        return stringRedisTemplate.opsForSet().members(key);
    }

    /**
     * 从 Set 移除成员
     */
    public Long srem(String key, String... members) {
        return stringRedisTemplate.opsForSet().remove(key, (Object[]) members);
    }

    /**
     * 判断成员是否在 Set 中
     */
    public Boolean sismember(String key, String member) {
        return stringRedisTemplate.opsForSet().isMember(key, member);
    }

    // ==================== ZSet 操作（滑动窗口限流用） ====================

    /**
     * 向 ZSet 添加成员
     */
    public Boolean zadd(String key, String member, double score) {
        return stringRedisTemplate.opsForZSet().add(key, member, score);
    }

    /**
     * 移除 ZSet 中 score 范围内的成员
     */
    public Long zremrangeByScore(String key, double min, double max) {
        return stringRedisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }

    /**
     * 获取 ZSet 成员数量
     */
    public Long zcard(String key) {
        return stringRedisTemplate.opsForZSet().zCard(key);
    }

    // ==================== 通用 ====================

    /**
     * 执行 Lua 脚本
     *
     * @param script Lua 脚本内容
     * @param keys   Redis Key 列表
     * @param args   脚本参数
     * @return 脚本执行结果（Boolean）
     */
    public Boolean executeScript(String script, List<String> keys, String... args) {
        DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>(script, Boolean.class);
        return stringRedisTemplate.execute(redisScript, keys, args);
    }

    /**
     * 非阻塞删除（UNLINK，Redis ≥ 4.0）
     */
    public Boolean unlink(String key) {
        return stringRedisTemplate.unlink(key);
    }

    /**
     * 批量非阻塞删除
     */
    public Long unlink(Collection<String> keys) {
        return stringRedisTemplate.unlink(keys);
    }

    /**
     * KEYS 命令（仅 dev 环境，生产禁用）
     */
    public Set<String> keys(String pattern) {
        return stringRedisTemplate.keys(pattern);
    }
}

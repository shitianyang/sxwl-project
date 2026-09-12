package com.sxwl.system.service.impl;

import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.system.model.dto.SysCacheCategoryDTO;
import com.sxwl.system.model.dto.SysCacheKeyDetailDTO;
import com.sxwl.system.service.SysCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 缓存管理 Service 实现
 *
 * <p>直接操作 Redis，通过 StringRedisTemplate 的 SCAN/TYPE/GET 等命令实现。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@Service
public class SysCacheServiceImpl implements SysCacheService {

    private static final Logger log = LoggerFactory.getLogger(SysCacheServiceImpl.class);

    /** 单次删除/扫描的安全上限，避免一次 unlink 过多 key 阻塞 Redis */
    private static final int MAX_CLEAR_BATCH = 1000;

    /**
     * 硬编码的预定义缓存分类（白名单）
     */
    private static final List<SysCacheCategoryDTO> CATEGORIES = List.of(
            new SysCacheCategoryDTO("Token 白名单", "token:jwt:*"),
            new SysCacheCategoryDTO("在线用户", "online:*"),
            new SysCacheCategoryDTO("系统参数", "config:*"),
            new SysCacheCategoryDTO("验证码", "captcha:*"),
            new SysCacheCategoryDTO("登录风控", "login:*"),
            new SysCacheCategoryDTO("防重复提交", "repeat:*")
    );

    private final StringRedisTemplate stringRedisTemplate;

    public SysCacheServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public List<SysCacheCategoryDTO> listCategories() {
        return CATEGORIES;
    }

    @Override
    public List<SysCacheKeyDetailDTO> listKeys(String categoryKeyPrefix) {
        assertAllowedPrefix(categoryKeyPrefix);
        List<SysCacheKeyDetailDTO> result = new ArrayList<>();
        try (Cursor<String> cursor = stringRedisTemplate.scan(
                ScanOptions.scanOptions().match(categoryKeyPrefix).count(200).build())) {
            int count = 0;
            while (cursor.hasNext() && count < 200) {
                String key = cursor.next();
                result.add(buildKeyDetail(key));
                count++;
            }
        } catch (Exception e) {
            log.warn("SCAN 缓存 Key 异常, prefix={}: {}", categoryKeyPrefix, e.getMessage());
        }
        return result;
    }

    @Override
    public SysCacheKeyDetailDTO getKeyDetail(String key) {
        // 拒绝读取白名单分类之外的 key（避免读取/泄露 token:*、login:* 等敏感数据）
        assertAllowedKey(key);
        return buildKeyDetail(key);
    }

    @Override
    public void clearByName(String categoryKeyPrefix) {
        // 仅允许白名单分类前缀，杜绝传入 "*" 清空整个 Redis
        assertAllowedPrefix(categoryKeyPrefix);
        // 使用 SCAN 迭代（禁止使用阻塞式 KEYS 命令），分批收集后统一 unlink
        Set<String> keys = new HashSet<>();
        try (Cursor<String> cursor = stringRedisTemplate.scan(
                ScanOptions.scanOptions().match(categoryKeyPrefix).count(200).build())) {
            while (cursor.hasNext() && keys.size() < MAX_CLEAR_BATCH) {
                keys.add(cursor.next());
            }
        } catch (Exception e) {
            log.warn("SCAN 缓存 Key 异常, prefix={}: {}", categoryKeyPrefix, e.getMessage());
            return;
        }
        if (!keys.isEmpty()) {
            stringRedisTemplate.unlink(keys);
            log.info("清除缓存分类: prefix={}, count={}", categoryKeyPrefix, keys.size());
        }
    }

    @Override
    public void clearByKey(String key) {
        // 仅允许删除白名单分类内的 key（避免删除任意用户的 token:* 等）
        assertAllowedKey(key);
        stringRedisTemplate.unlink(key);
        log.info("清除缓存 Key: {}", key);
    }

    // ==================== 私有方法 ====================

    /**
     * 断言分类前缀属于白名单，否则拒绝
     */
    private void assertAllowedPrefix(String prefix) {
        if (prefix == null || prefix.isBlank()) {
            throw new SxwlBusinessException(10001, "缓存分类前缀不能为空");
        }
        for (SysCacheCategoryDTO category : CATEGORIES) {
            if (category.getKeyPrefix().equals(prefix)) {
                return;
            }
        }
        throw new SxwlBusinessException(10001, "非法的缓存分类前缀: " + prefix);
    }

    /**
     * 断言 key 属于白名单分类（去掉模式尾部的 "*" 后做前缀匹配），否则拒绝
     */
    private void assertAllowedKey(String key) {
        if (key == null || key.isBlank()) {
            throw new SxwlBusinessException(10001, "缓存 Key 不能为空");
        }
        for (SysCacheCategoryDTO category : CATEGORIES) {
            String allowedPrefix = category.getKeyPrefix().replace("*", "");
            if (key.startsWith(allowedPrefix)) {
                return;
            }
        }
        throw new SxwlBusinessException(10001, "非法的缓存 Key（不在允许的分类内）: " + key);
    }

    /**
     * 根据 Key 构建详情（包括类型、Value、TTL）
     */
    private SysCacheKeyDetailDTO buildKeyDetail(String key) {
        SysCacheKeyDetailDTO dto = new SysCacheKeyDetailDTO();
        dto.setKey(key);

        DataType type = stringRedisTemplate.type(key);
        dto.setType(type != null ? type.code() : "unknown");
        dto.setTtl(stringRedisTemplate.getExpire(key, TimeUnit.SECONDS));

        // 根据数据类型读取 Value
        dto.setValue(readValue(key, type));

        return dto;
    }

    /**
     * 按数据类型读取 Value
     */
    private Object readValue(String key, DataType type) {
        if (type == null) return null;

        return switch (type.code()) {
            case "string" -> stringRedisTemplate.opsForValue().get(key);
            case "hash" -> stringRedisTemplate.opsForHash().entries(key);
            case "set" -> stringRedisTemplate.opsForSet().members(key);
            case "zset" -> stringRedisTemplate.opsForZSet().range(key, 0, -1);
            case "list" -> stringRedisTemplate.opsForList().range(key, 0, -1);
            default -> null;
        };
    }
}

package com.sxwl.system.service.impl;

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

    /**
     * 硬编码的预定义缓存分类
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
        return buildKeyDetail(key);
    }

    @Override
    public void clearByName(String categoryKeyPrefix) {
        Set<String> keys = stringRedisTemplate.keys(categoryKeyPrefix);
        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.unlink(keys);
            log.info("清除缓存分类: prefix={}, count={}", categoryKeyPrefix, keys.size());
        }
    }

    @Override
    public void clearByKey(String key) {
        stringRedisTemplate.unlink(key);
        log.info("清除缓存 Key: {}", key);
    }

    // ==================== 私有方法 ====================

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

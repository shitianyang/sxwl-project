package com.sxwl.config.service.impl;

import com.github.pagehelper.PageInfo;
import com.sxwl.config.mapper.SysConfigInfoMapper;
import com.sxwl.config.model.dto.SysConfigDTO;
import com.sxwl.config.model.entity.SysConfigInfo;
import com.sxwl.config.model.params.SysConfigPageParams;
import com.sxwl.config.service.SysConfigInfoService;
import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.common.utils.SxwlDiffUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 系统参数配置 Service 实现
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@Service
public class SysConfigInfoServiceImpl implements SysConfigInfoService {

    private static final Logger log = LoggerFactory.getLogger(SysConfigInfoServiceImpl.class);
    private static final long CACHE_TTL_SECONDS = 3600L;  // Redis 缓存 TTL: 1 小时

    private final SysConfigInfoMapper sysConfigInfoMapper;
    private final StringRedisTemplate stringRedisTemplate;

    public SysConfigInfoServiceImpl(SysConfigInfoMapper sysConfigInfoMapper,
                                     StringRedisTemplate stringRedisTemplate) {
        this.sysConfigInfoMapper = sysConfigInfoMapper;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public SysConfigDTO getConfigById(Long id) {
        SysConfigDTO dto = sysConfigInfoMapper.getConfigById(id);
        if (dto == null) {
            throw new SxwlBusinessException(10004, "参数配置不存在或已被删除");
        }
        return dto;
    }

    @Override
    public SysConfigDTO getConfigByKey(String configKey) {
        return sysConfigInfoMapper.getConfigByKey(configKey);
    }

    @Override
    public PageInfo<SysConfigDTO> getConfigPageByParams(SysConfigPageParams params) {
        List<SysConfigDTO> rows = sysConfigInfoMapper.getConfigPageByParams(params);
        return new PageInfo<>(rows);
    }

    @Override
    public int createConfig(SysConfigDTO dto) {
        // 唯一性校验
        if (sysConfigInfoMapper.checkConfigKeyUnique(dto.getConfigKey(), null) > 0) {
            throw new SxwlBusinessException(10002, "参数键名已存在");
        }

        SysConfigInfo entity = toEntity(dto);
        int result = sysConfigInfoMapper.insertConfig(entity);
        if (result != 1) {
            log.error("新增参数配置失败: configKey={}, result={}", dto.getConfigKey(), result);
            throw new SxwlBusinessException(10001, "新增参数配置失败");
        }

        // ✅ 新增成功后，立即写入 Redis 缓存
        if (dto.getConfigValue() != null) {
            String cacheKey = com.sxwl.common.utils.SxwlRedisKeyUtils.configCacheKey(dto.getConfigKey());
            stringRedisTemplate.opsForValue().set(cacheKey, dto.getConfigValue(), CACHE_TTL_SECONDS, TimeUnit.SECONDS);
            log.info("新增配置并写入缓存: configKey={}, cacheKey={}", dto.getConfigKey(), cacheKey);
        } else {
            // 空值保护（防止缓存穿透）
            String cacheKey = com.sxwl.common.utils.SxwlRedisKeyUtils.configCacheKey(dto.getConfigKey());
            stringRedisTemplate.opsForValue().set(cacheKey, "", 300, TimeUnit.SECONDS);  // 5 分钟短 TTL
        }

        log.info("新增参数配置成功: configKey={}", dto.getConfigKey());
        return result;
    }

    @Override
    public int updateConfig(SysConfigDTO dto) {
        // 查询旧数据（必须在唯一性校验之前，以便获取旧 configKey）
        SysConfigDTO oldDto = sysConfigInfoMapper.getConfigById(dto.getId());
        if (oldDto == null) {
            throw new SxwlBusinessException(10004, "参数配置不存在或已被删除");
        }

        // ✅ 如果 configKey 发生变化，需要清除旧缓存
        String oldConfigKey = oldDto.getConfigKey();
        String newConfigKey = dto.getConfigKey();
        if (oldConfigKey != null && !oldConfigKey.equals(newConfigKey)) {
            String oldCacheKey = com.sxwl.common.utils.SxwlRedisKeyUtils.configCacheKey(oldConfigKey);
            stringRedisTemplate.delete(oldCacheKey);
            log.info("配置键名变更，清除旧缓存: {} -> {}", oldConfigKey, newConfigKey);
        }

        // 唯一性校验（排除自身）
        if (sysConfigInfoMapper.checkConfigKeyUnique(dto.getConfigKey(), dto.getId()) > 0) {
            throw new SxwlBusinessException(10002, "参数键名已存在");
        }

        // 计算字段级变更差异
        SysConfigInfo oldEntity = toEntity(oldDto);
        SysConfigInfo newEntity = toEntity(dto);
        newEntity.setId(dto.getId());
        String diffJson = SxwlDiffUtils.diff(oldEntity, newEntity);
        if (diffJson != null) {
            SxwlDiffUtils.setContextDiff(diffJson);
        }

        SysConfigInfo entity = toEntity(dto);
        entity.setId(dto.getId());

        int result = sysConfigInfoMapper.updateConfig(entity);
        if (result == 0) {
            throw new SxwlBusinessException(10004, "参数配置不存在或已被删除");
        }

        // ✅ 更新成功后，刷新 Redis 缓存
        String cacheKey = com.sxwl.common.utils.SxwlRedisKeyUtils.configCacheKey(newConfigKey);
        if (dto.getConfigValue() != null) {
            stringRedisTemplate.opsForValue().set(cacheKey, dto.getConfigValue(), CACHE_TTL_SECONDS, TimeUnit.SECONDS);
        } else {
            stringRedisTemplate.opsForValue().set(cacheKey, "", 300, TimeUnit.SECONDS);  // 空值保护
        }

        log.info("修改参数配置成功: id={}, configKey={}", dto.getId(), newConfigKey);
        return result;
    }

    @Override
    public int deleteConfigById(Long id) {
        int affected = sysConfigInfoMapper.deleteConfigById(id);
        if (affected == 0) {
            throw new SxwlBusinessException(10004, "参数配置不存在或已被删除");
        }
        log.info("删除参数配置成功: id={}", id);
        return affected;
    }

    // ==================== 私有方法 ====================

    private SysConfigInfo toEntity(SysConfigDTO dto) {
        SysConfigInfo entity = new SysConfigInfo();
        entity.setConfigKey(dto.getConfigKey());
        entity.setConfigName(dto.getConfigName());
        entity.setConfigValue(dto.getConfigValue());
        entity.setConfigType(dto.getConfigType());
        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.getStatus());
        return entity;
    }
}

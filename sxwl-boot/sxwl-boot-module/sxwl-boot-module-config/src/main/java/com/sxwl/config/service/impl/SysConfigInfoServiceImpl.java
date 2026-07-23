package com.sxwl.config.service.impl;

import com.github.pagehelper.PageInfo;
import com.sxwl.config.mapper.SysConfigInfoMapper;
import com.sxwl.config.model.dto.SysConfigDTO;
import com.sxwl.config.model.entity.SysConfigInfo;
import com.sxwl.config.model.params.SysConfigPageParams;
import com.sxwl.config.service.SysConfigInfoService;
import com.sxwl.common.exception.SxwlBusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

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

    private final SysConfigInfoMapper sysConfigInfoMapper;

    public SysConfigInfoServiceImpl(SysConfigInfoMapper sysConfigInfoMapper) {
        this.sysConfigInfoMapper = sysConfigInfoMapper;
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
        log.info("新增参数配置成功: configKey={}", dto.getConfigKey());
        return result;
    }

    @Override
    public int updateConfig(SysConfigDTO dto) {
        // 唯一性校验（排除自身）
        if (sysConfigInfoMapper.checkConfigKeyUnique(dto.getConfigKey(), dto.getId()) > 0) {
            throw new SxwlBusinessException(10002, "参数键名已存在");
        }

        SysConfigInfo entity = toEntity(dto);
        entity.setId(dto.getId());

        int result = sysConfigInfoMapper.updateConfig(entity);
        if (result == 0) {
            throw new SxwlBusinessException(10004, "参数配置不存在或已被删除");
        }
        log.info("修改参数配置成功: id={}", dto.getId());
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

package com.sxwl.system.service.impl;

import com.github.pagehelper.PageInfo;
import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.system.mapper.SysPositionMapper;
import com.sxwl.system.model.dto.SysPositionDTO;
import com.sxwl.system.model.entity.SysPosition;
import com.sxwl.system.model.params.SysPositionPageParams;
import com.sxwl.system.service.SysPositionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统岗位 Service 实现
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
@Service
public class SysPositionServiceImpl implements SysPositionService {

    private static final Logger log = LoggerFactory.getLogger(SysPositionServiceImpl.class);

    /** SysPosition Mapper */
    private final SysPositionMapper sysPositionMapper;

    public SysPositionServiceImpl(SysPositionMapper sysPositionMapper) {
        this.sysPositionMapper = sysPositionMapper;
    }

    /**
     * 根据 ID 查询岗位
     *
     * @param id 岗位 ID
     * @return 岗位 DTO，查不到抛 10004 异常
     */
    @Override
    public SysPositionDTO getPositionById(Long id) {
        SysPositionDTO dto = sysPositionMapper.getPositionById(id);
        if (dto == null) {
            throw new SxwlBusinessException(10004, "岗位不存在或已被删除");
        }
        return dto;
    }

    /**
     * 分页查询岗位列表
     *
     * @param params 分页 + 筛选参数（positionCode、status）
     * @return 分页结果
     */
    @Override
    public PageInfo<SysPositionDTO> getPositionPageByParams(SysPositionPageParams params) {
        List<SysPositionDTO> rows = sysPositionMapper.getPositionPageByParams(params);
        return new PageInfo<>(rows);
    }

    /**
     * 新增岗位
     * <p>包含岗位编码唯一性校验。</p>
     *
     * @param dto 岗位 DTO
     * @return 影响行数
     * @throws SxwlBusinessException 岗位编码重复或新增失败时抛出
     */
    @Override
    public int createPosition(SysPositionDTO dto) {
        // 唯一性校验
        if (sysPositionMapper.checkPositionCodeUnique(dto.getPositionCode(), null) > 0) {
            throw new SxwlBusinessException(10002, "岗位编码已存在");
        }

        SysPosition entity = toEntity(dto);
        int result = sysPositionMapper.insertPosition(entity);
        if (result != 1) {
            log.error("新增岗位失败: positionCode={}, result={}", dto.getPositionCode(), result);
            throw new SxwlBusinessException(10001, "新增岗位失败");
        }
        log.info("新增岗位成功: positionCode={}", dto.getPositionCode());
        return result;
    }

    /**
     * 修改岗位
     * <p>唯一性校验排除自身。</p>
     *
     * @param dto 岗位 DTO
     * @return 影响行数
     * @throws SxwlBusinessException 岗位编码重复或岗位不存在时抛出
     */
    @Override
    public int updatePosition(SysPositionDTO dto) {
        // 唯一性校验（排除自身）
        if (sysPositionMapper.checkPositionCodeUnique(dto.getPositionCode(), dto.getId()) > 0) {
            throw new SxwlBusinessException(10002, "岗位编码已存在");
        }

        SysPosition entity = toEntity(dto);
        entity.setId(dto.getId());

        int result = sysPositionMapper.updatePosition(entity);
        if (result == 0) {
            throw new SxwlBusinessException(10004, "岗位不存在或已被删除");
        }
        log.info("修改岗位成功: id={}", dto.getId());
        return result;
    }

    /**
     * 删除岗位（逻辑删除）
     *
     * @param id 岗位 ID
     * @return 影响行数
     * @throws SxwlBusinessException 岗位不存在时抛出
     */
    @Override
    public int deletePositionById(Long id) {
        int affected = sysPositionMapper.deletePositionById(id);
        if (affected == 0) {
            throw new SxwlBusinessException(10004, "岗位不存在或已被删除");
        }
        log.info("删除岗位成功: id={}", id);
        return affected;
    }

    /**
     * 批量删除岗位（逻辑删除）
     *
     * @param ids 岗位 ID 列表
     * @return 影响行数
     * @throws SxwlBusinessException 列表为空或全部不存在时抛出
     */
    @Override
    public int batchDeletePositionByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new SxwlBusinessException(10001, "删除岗位列表不能为空");
        }
        int affected = sysPositionMapper.batchDeletePositionByIds(ids);
        if (affected == 0) {
            throw new SxwlBusinessException(10004, "岗位不存在或已被删除");
        }
        log.info("批量删除岗位成功: ids={}, count={}", ids, affected);
        return affected;
    }

    // ==================== 私有方法 ====================

    /**
     * DTO 转实体
     *
     * @param dto 岗位 DTO
     * @return 岗位实体
     */
    private SysPosition toEntity(SysPositionDTO dto) {
        SysPosition entity = new SysPosition();
        entity.setPositionCode(dto.getPositionCode());
        entity.setPositionName(dto.getPositionName());
        entity.setSort(dto.getSort());
        entity.setStatus(dto.getStatus());
        entity.setDescription(dto.getDescription());
        return entity;
    }
}

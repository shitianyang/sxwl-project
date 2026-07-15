package com.sxwl.system.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.system.mapper.SysDictDetailMapper;
import com.sxwl.system.mapper.SysDictMapper;
import com.sxwl.system.model.dto.SysDictDTO;
import com.sxwl.system.model.dto.SysDictDetailDTO;
import com.sxwl.system.model.entity.SysDict;
import com.sxwl.system.model.entity.SysDictDetail;
import com.sxwl.system.model.params.SysDictPageParams;
import com.sxwl.system.service.SysDictService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统字典 Service 实现
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
@Service
public class SysDictServiceImpl implements SysDictService {

    private static final Logger log = LoggerFactory.getLogger(SysDictServiceImpl.class);

    /** SysDict Mapper */
    private final SysDictMapper sysDictMapper;
    /** SysDictDetail Mapper */
    private final SysDictDetailMapper sysDictDetailMapper;

    public SysDictServiceImpl(SysDictMapper sysDictMapper,
                              SysDictDetailMapper sysDictDetailMapper) {
        this.sysDictMapper = sysDictMapper;
        this.sysDictDetailMapper = sysDictDetailMapper;
    }

    // ==================== 字典主表 ====================

    /**
     * 根据 ID 查询字典
     *
     * @param id 字典 ID
     * @return 字典 DTO，查不到抛 10004 异常
     */
    @Override
    public SysDictDTO getDictById(Long id) {
        SysDictDTO dto = sysDictMapper.getDictById(id);
        if (dto == null) {
            throw new SxwlBusinessException(10004, "字典不存在或已被删除");
        }
        return dto;
    }

    /**
     * 分页查询字典列表
     *
     * @param params 分页 + 筛选参数（dictCode、dictName、status）
     * @return 分页结果
     */
    @Override
    public PageInfo<SysDictDTO> getDictPageByParams(SysDictPageParams params) {
        PageHelper.startPage(params.getCurrent(), params.getPageSize());
        List<SysDictDTO> rows = sysDictMapper.getDictPageByParams(params);
        return new PageInfo<>(rows);
    }

    /**
     * 新增字典
     * <p>包含字典编码唯一性校验。</p>
     *
     * @param dto 字典 DTO
     * @return 影响行数
     * @throws SxwlBusinessException 字典编码重复或新增失败时抛出
     */
    @Override
    public int createDict(SysDictDTO dto) {
        // 唯一性校验
        if (sysDictMapper.checkDictCodeUnique(dto.getDictCode(), null) > 0) {
            throw new SxwlBusinessException(10002, "字典编码已存在");
        }

        SysDict entity = toEntity(dto);
        int result = sysDictMapper.insertDict(entity);
        if (result != 1) {
            log.error("新增字典失败: dictCode={}, result={}", dto.getDictCode(), result);
            throw new SxwlBusinessException(10001, "新增字典失败");
        }
        log.info("新增字典成功: dictCode={}", dto.getDictCode());
        return result;
    }

    /**
     * 修改字典
     * <p>唯一性校验排除自身。</p>
     *
     * @param dto 字典 DTO
     * @return 影响行数
     * @throws SxwlBusinessException 字典编码重复或字典不存在时抛出
     */
    @Override
    public int updateDict(SysDictDTO dto) {
        // 唯一性校验（排除自身）
        if (sysDictMapper.checkDictCodeUnique(dto.getDictCode(), dto.getId()) > 0) {
            throw new SxwlBusinessException(10002, "字典编码已存在");
        }

        SysDict entity = toEntity(dto);
        entity.setId(dto.getId());

        int result = sysDictMapper.updateDict(entity);
        if (result == 0) {
            throw new SxwlBusinessException(10004, "字典不存在或已被删除");
        }
        log.info("修改字典成功: id={}", dto.getId());
        return result;
    }

    /**
     * 删除字典（逻辑删除）
     *
     * @param id 字典 ID
     * @return 影响行数
     * @throws SxwlBusinessException 字典不存在时抛出
     */
    @Override
    public int deleteDictById(Long id) {
        int affected = sysDictMapper.deleteDictById(id);
        if (affected == 0) {
            throw new SxwlBusinessException(10004, "字典不存在或已被删除");
        }
        log.info("删除字典成功: id={}", id);
        return affected;
    }

    // ==================== 字典明细 ====================

    /**
     * 根据字典 ID 查询所有明细
     *
     * @param dictId 字典 ID
     * @return 明细列表
     */
    @Override
    public List<SysDictDetailDTO> getDetailListByDictId(Long dictId) {
        return sysDictDetailMapper.getDetailListByDictId(dictId);
    }

    /**
     * 新增明细
     * <p>包含明细值唯一性校验。</p>
     *
     * @param dto 明细 DTO
     * @return 影响行数
     * @throws SxwlBusinessException 明细值重复或新增失败时抛出
     */
    @Override
    public int createDetail(SysDictDetailDTO dto) {
        // 唯一性校验
        if (sysDictDetailMapper.checkDetailValueUnique(dto.getDetailValue(), null) > 0) {
            throw new SxwlBusinessException(10002, "字典明细值已存在");
        }

        SysDictDetail entity = toDetailEntity(dto);
        int result = sysDictDetailMapper.insertDetail(entity);
        if (result != 1) {
            log.error("新增字典明细失败: detailValue={}, result={}", dto.getDetailValue(), result);
            throw new SxwlBusinessException(10001, "新增字典明细失败");
        }
        log.info("新增字典明细成功: detailValue={}", dto.getDetailValue());
        return result;
    }

    /**
     * 修改明细
     * <p>唯一性校验排除自身。</p>
     *
     * @param dto 明细 DTO
     * @return 影响行数
     * @throws SxwlBusinessException 明细值重复或明细不存在时抛出
     */
    @Override
    public int updateDetail(SysDictDetailDTO dto) {
        // 唯一性校验（排除自身）
        if (sysDictDetailMapper.checkDetailValueUnique(dto.getDetailValue(), dto.getId()) > 0) {
            throw new SxwlBusinessException(10002, "字典明细值已存在");
        }

        SysDictDetail entity = toDetailEntity(dto);
        entity.setId(dto.getId());

        int result = sysDictDetailMapper.updateDetail(entity);
        if (result == 0) {
            throw new SxwlBusinessException(10004, "字典明细不存在或已被删除");
        }
        log.info("修改字典明细成功: id={}", dto.getId());
        return result;
    }

    /**
     * 删除明细（逻辑删除）
     *
     * @param id 明细 ID
     * @return 影响行数
     * @throws SxwlBusinessException 明细不存在时抛出
     */
    @Override
    public int deleteDetailById(Long id) {
        int affected = sysDictDetailMapper.deleteDetailById(id);
        if (affected == 0) {
            throw new SxwlBusinessException(10004, "字典明细不存在或已被删除");
        }
        log.info("删除字典明细成功: id={}", id);
        return affected;
    }

    // ==================== 私有方法 ====================

    /**
     * DTO 转实体
     *
     * @param dto 字典 DTO
     * @return 字典实体
     */
    private SysDict toEntity(SysDictDTO dto) {
        SysDict entity = new SysDict();
        entity.setDictCode(dto.getDictCode());
        entity.setDictName(dto.getDictName());
        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.getStatus());
        return entity;
    }

    /**
     * 明细 DTO 转实体
     *
     * @param dto 明细 DTO
     * @return 明细实体
     */
    private SysDictDetail toDetailEntity(SysDictDetailDTO dto) {
        SysDictDetail entity = new SysDictDetail();
        entity.setDictId(dto.getDictId());
        entity.setDetailValue(dto.getDetailValue());
        entity.setDetailLabel(dto.getDetailLabel());
        entity.setDescription(dto.getDescription());
        entity.setSort(dto.getSort());
        entity.setStatus(dto.getStatus());
        entity.setIsDefault(dto.getIsDefault());
        return entity;
    }
}

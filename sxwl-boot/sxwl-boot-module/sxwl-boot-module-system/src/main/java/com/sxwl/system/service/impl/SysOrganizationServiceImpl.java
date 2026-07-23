package com.sxwl.system.service.impl;

import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.common.utils.SxwlDiffUtils;
import com.sxwl.common.utils.SxwlTreeUtils;
import com.sxwl.system.mapper.SysOrganizationMapper;
import com.sxwl.system.model.dto.SysOrganizationDTO;
import com.sxwl.system.model.entity.SysOrganization;
import com.sxwl.system.service.SysOrganizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 系统组织 Service 实现
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
@Service
public class SysOrganizationServiceImpl implements SysOrganizationService {

    private static final Logger log = LoggerFactory.getLogger(SysOrganizationServiceImpl.class);

    /** SysOrganization Mapper */
    private final SysOrganizationMapper sysOrganizationMapper;

    public SysOrganizationServiceImpl(SysOrganizationMapper sysOrganizationMapper) {
        this.sysOrganizationMapper = sysOrganizationMapper;
    }

    /**
     * 根据 ID 查询组织
     *
     * @param id 组织 ID
     * @return 组织 DTO，查不到抛 10004 异常
     */
    @Override
    public SysOrganizationDTO getOrganizationById(Long id) {
        SysOrganizationDTO dto = sysOrganizationMapper.getOrganizationById(id);
        if (dto == null) {
            throw new SxwlBusinessException(10004, "组织不存在或已被删除");
        }
        return dto;
    }

    /**
     * 查询组织树（不分页）
     *
     * @return 树形组织列表
     */
    @Override
    public List<SysOrganizationDTO> getOrganizationTree() {
        List<SysOrganizationDTO> all = sysOrganizationMapper.selectAllOrganizations();
        return SxwlTreeUtils.buildTree(all);
    }

    /**
     * 查询所有组织（平铺列表）
     *
     * @return 平铺组织列表
     */
    @Override
    public List<SysOrganizationDTO> getAllOrganizationList() {
        return sysOrganizationMapper.selectAllOrganizations();
    }

    /**
     * 新增组织
     * <p>包含组织编码唯一性校验。</p>
     *
     * @param dto 组织 DTO
     * @return 影响行数
     * @throws SxwlBusinessException 组织编码重复或新增失败时抛出
     */
    @Override
    public int createOrganization(SysOrganizationDTO dto) {
        // 唯一性校验
        if (sysOrganizationMapper.checkOrgCodeUnique(dto.getOrgCode(), null) > 0) {
            throw new SxwlBusinessException(10002, "组织编码已存在");
        }

        SysOrganization entity = toEntity(dto);
        // 设置 ancestors
        if (dto.getParentId() != null && dto.getParentId() > 0) {
            SysOrganizationDTO parent = sysOrganizationMapper.getOrganizationById(dto.getParentId());
            if (parent == null) {
                throw new SxwlBusinessException(10004, "父组织不存在");
            }
            entity.setAncestors(parent.getAncestors() + "," + dto.getParentId());
        } else {
            entity.setAncestors("0");
            entity.setParentId(0L);
        }

        int result = sysOrganizationMapper.insertOrganization(entity);
        if (result != 1) {
            log.error("新增组织失败: orgName={}, result={}", dto.getOrgName(), result);
            throw new SxwlBusinessException(10001, "新增组织失败");
        }
        log.info("新增组织成功: orgName={}", dto.getOrgName());
        return result;
    }

    /**
     * 修改组织
     * <p>唯一性校验排除自身，parentId 变了则自动更新 ancestors。</p>
     *
     * @param dto 组织 DTO
     * @return 影响行数
     * @throws SxwlBusinessException 组织编码重复或组织不存在时抛出
     */
    @Override
    public int updateOrganization(SysOrganizationDTO dto) {
        // 唯一性校验（排除自身）
        if (sysOrganizationMapper.checkOrgCodeUnique(dto.getOrgCode(), dto.getId()) > 0) {
            throw new SxwlBusinessException(10002, "组织编码已存在");
        }

        SysOrganization entity = toEntity(dto);
        entity.setId(dto.getId());

        // 如果 parentId 变了，更新 ancestors
        SysOrganizationDTO old = sysOrganizationMapper.getOrganizationById(dto.getId());
        if (old != null && !Objects.equals(old.getParentId(), dto.getParentId())) {
            if (dto.getParentId() != null && dto.getParentId() > 0) {
                SysOrganizationDTO parent = sysOrganizationMapper.getOrganizationById(dto.getParentId());
                if (parent == null) {
                    throw new SxwlBusinessException(10004, "父组织不存在");
                }
                entity.setAncestors(parent.getAncestors() + "," + dto.getParentId());
            } else {
                entity.setAncestors("0");
                entity.setParentId(0L);
            }
        }

        // 计算字段级变更差异
        if (old != null) {
            SysOrganization oldEntity = toEntity(old);
            String diffJson = SxwlDiffUtils.diff(oldEntity, entity);
            if (diffJson != null) {
                SxwlDiffUtils.setContextDiff(diffJson);
            }
        }

        int result = sysOrganizationMapper.updateOrganization(entity);
        if (result == 0) {
            throw new SxwlBusinessException(10004, "组织不存在或已被删除");
        }
        log.info("修改组织成功: id={}", dto.getId());
        return result;
    }

    /**
     * 删除组织（逻辑删除）
     * <p>存在子组织则不允许删除。</p>
     *
     * @param id 组织 ID
     * @return 影响行数
     * @throws SxwlBusinessException 存在子组织或组织不存在时抛出
     */
    @Override
    public int deleteOrganizationById(Long id) {
        // 检查是否有子组织
        int childCount = sysOrganizationMapper.countChildrenByParentId(id);
        if (childCount > 0) {
            throw new SxwlBusinessException(10001, "存在子组织，不允许删除");
        }

        int affected = sysOrganizationMapper.deleteOrganizationById(id);
        if (affected == 0) {
            throw new SxwlBusinessException(10004, "组织不存在或已被删除");
        }
        log.info("删除组织成功: id={}", id);
        return affected;
    }

    // ==================== 私有方法 ====================

    /**
     * DTO 转实体
     *
     * @param dto 组织 DTO
     * @return 组织实体
     */
    private SysOrganization toEntity(SysOrganizationDTO dto) {
        SysOrganization entity = new SysOrganization();
        entity.setOrgCode(dto.getOrgCode());
        entity.setOrgName(dto.getOrgName());
        entity.setParentId(dto.getParentId());
        entity.setAncestors(dto.getAncestors());
        entity.setOrgLevel(dto.getOrgLevel());
        entity.setOrgType(dto.getOrgType());
        entity.setLeaderId(dto.getLeaderId());
        entity.setPhone(dto.getPhone());
        entity.setSort(dto.getSort());
        entity.setStatus(dto.getStatus());
        entity.setDescription(dto.getDescription());
        return entity;
    }
}

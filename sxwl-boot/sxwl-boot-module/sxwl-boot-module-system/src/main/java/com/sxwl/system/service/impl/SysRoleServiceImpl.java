package com.sxwl.system.service.impl;

import com.github.pagehelper.PageInfo;
import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.system.mapper.SysRoleMapper;
import com.sxwl.system.model.dto.SysRoleDTO;
import com.sxwl.system.model.entity.SysRole;
import com.sxwl.system.model.params.SysRolePageParams;
import com.sxwl.system.service.SysRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sxwl.common.utils.SxwlSnowFlakeUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统角色 Service 实现
 *
 * @author shitianyang
 * @date 2026/7/11
 * @since 0.1.0
 */
@Service
public class SysRoleServiceImpl implements SysRoleService {

    private static final Logger log = LoggerFactory.getLogger(SysRoleServiceImpl.class);

    /** SysRole Mapper */
    private final SysRoleMapper sysRoleMapper;

    public SysRoleServiceImpl(SysRoleMapper sysRoleMapper) {
        this.sysRoleMapper = sysRoleMapper;
    }

    // ==================== CRUD ====================

    /**
     * 根据 ID 查询角色
     *
     * @param id 角色 ID
     * @return 角色 DTO，查不到抛 10004 异常
     */
    @Override
    public SysRoleDTO getRoleById(Long id) {
        SysRoleDTO dto = sysRoleMapper.getRoleById(id);
        if (dto == null) {
            throw new SxwlBusinessException(10004, "角色不存在或已被删除");
        }
        return dto;
    }

    /**
     * 分页查询角色列表
     *
     * @param params 分页 + 筛选参数（roleCode、roleName、status）
     * @return 分页结果
     */
    @Override
    public PageInfo<SysRoleDTO> getRolePageByParams(SysRolePageParams params) {
        List<SysRoleDTO> rows = sysRoleMapper.getRolePageByParams(params);
        return new PageInfo<>(rows);
    }

    /**
     * 新增角色
     * <p>包含角色编码唯一性校验。</p>
     *
     * @param dto 角色 DTO
     * @return 影响行数
     * @throws SxwlBusinessException 角色编码重复或新增失败时抛出
     */
    @Override
    public int createRole(SysRoleDTO dto) {
        // 唯一性校验
        if (sysRoleMapper.checkRoleCodeUnique(dto.getRoleCode(), null) > 0) {
            throw new SxwlBusinessException(10002, "角色编码已存在");
        }

        SysRole entity = toEntity(dto);
        int result = sysRoleMapper.insertRole(entity);
        if (result != 1) {
            log.error("新增角色失败: roleCode={}, result={}", dto.getRoleCode(), result);
            throw new SxwlBusinessException(10001, "新增角色失败");
        }
        log.info("新增角色成功: roleCode={}", dto.getRoleCode());
        return result;
    }

    /**
     * 修改角色
     * <p>唯一性校验排除自身。</p>
     *
     * @param dto 角色 DTO
     * @return 影响行数
     * @throws SxwlBusinessException 角色编码重复或角色不存在时抛出
     */
    @Override
    public int updateRole(SysRoleDTO dto) {
        // 唯一性校验（排除自身）
        if (sysRoleMapper.checkRoleCodeUnique(dto.getRoleCode(), dto.getId()) > 0) {
            throw new SxwlBusinessException(10002, "角色编码已存在");
        }

        SysRole entity = toEntity(dto);
        entity.setId(dto.getId());

        int result = sysRoleMapper.updateRole(entity);
        if (result == 0) {
            throw new SxwlBusinessException(10004, "角色不存在或已被删除");
        }
        log.info("修改角色成功: id={}", dto.getId());
        return result;
    }

    /**
     * 删除角色（逻辑删除）
     *
     * @param id 角色 ID
     * @return 影响行数
     * @throws SxwlBusinessException 角色不存在时抛出
     */
    @Override
    public int deleteRoleById(Long id) {
        int affected = sysRoleMapper.deleteRoleById(id);
        if (affected == 0) {
            throw new SxwlBusinessException(10004, "角色不存在或已被删除");
        }
        log.info("删除角色成功: id={}", id);
        return affected;
    }

    // ==================== 菜单分配 ====================

    /**
     * 保存角色的菜单分配（先删后插）
     *
     * @param roleId  角色 ID
     * @param menuIds 菜单 ID 列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRoleMenus(Long roleId, List<Long> menuIds) {
        // 先删
        sysRoleMapper.deleteRoleMenusByRoleId(roleId);

        // 后插（为空则不插入）
        if (menuIds != null && !menuIds.isEmpty()) {
            List<Long> ids = menuIds.stream().map(m -> SxwlSnowFlakeUtils.nextId()).collect(Collectors.toList());
            sysRoleMapper.batchInsertRoleMenus(roleId, menuIds, ids, 0L, 0L, new Date());
        }
        log.info("保存角色菜单分配成功: roleId={}, menuCount={}", roleId, menuIds != null ? menuIds.size() : 0);
    }

    /**
     * 查询角色已分配的菜单 ID 列表
     *
     * @param roleId 角色 ID
     * @return 菜单 ID 列表
     */
    @Override
    public List<Long> getMenuIdListByRoleId(Long roleId) {
        return sysRoleMapper.getMenuIdListByRoleId(roleId);
    }

    // ==================== 数据权限 ====================

    /**
     * 保存角色的数据权限（先删后插）
     *
     * @param roleId 角色 ID
     * @param orgIds 组织 ID 列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRoleDataScope(Long roleId, List<Long> orgIds) {
        // 先删
        sysRoleMapper.deleteRoleDataScopeByRoleId(roleId);

        // 后插（为空则不插入）
        if (orgIds != null && !orgIds.isEmpty()) {
            List<Long> ids = orgIds.stream().map(m -> SxwlSnowFlakeUtils.nextId()).collect(Collectors.toList());
            sysRoleMapper.batchInsertRoleDataScope(roleId, orgIds, ids, 0L, 0L, new Date());
        }
        log.info("保存角色数据权限成功: roleId={}, orgCount={}", roleId, orgIds != null ? orgIds.size() : 0);
    }

    /**
     * 查询角色已授权的组织 ID 列表（数据权限）
     *
     * @param roleId 角色 ID
     * @return 组织 ID 列表
     */
    @Override
    public List<Long> getDataScopeOrgIdListByRoleId(Long roleId) {
        return sysRoleMapper.getDataScopeOrgIdListByRoleId(roleId);
    }

    // ==================== 私有方法 ====================

    /**
     * DTO 转实体
     *
     * @param dto 角色 DTO
     * @return 角色实体
     */
    private SysRole toEntity(SysRoleDTO dto) {
        SysRole entity = new SysRole();
        entity.setRoleCode(dto.getRoleCode());
        entity.setRoleName(dto.getRoleName());
        entity.setDataScope(dto.getDataScope());
        entity.setSort(dto.getSort());
        entity.setStatus(dto.getStatus());
        entity.setDescription(dto.getDescription());
        return entity;
    }
}

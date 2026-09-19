package com.sxwl.system.service.impl;

import com.github.pagehelper.PageInfo;
import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.common.constants.SxwlSystemConstants;
import com.sxwl.common.utils.SxwlDiffUtils;
import com.sxwl.common.utils.SxwlSnowFlakeUtils;
import com.sxwl.security.key.SxwlSM2KeyManager;
import com.sxwl.security.model.SxwlLoginUser;
import com.sxwl.security.utils.SxwlSecurityUtils;
import com.sxwl.system.mapper.SysUserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.sxwl.system.model.dto.SysUserDTO;
import com.sxwl.system.model.entity.SysUser;
import com.sxwl.system.model.params.SysUserPageParams;
import com.sxwl.system.service.SxwlAuthCacheService;
import com.sxwl.system.service.SysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 系统用户 Service 实现
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@Service
public class SysUserServiceImpl implements SysUserService {

    private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);

    /** 系统用户 Mapper */
    private final SysUserMapper sysUserMapper;

    /** 密码编码器（SM3 哈希） */
    private final PasswordEncoder passwordEncoder;

    /** SM2 密钥管理器（支持密钥轮换和宽限期降级解密） */
    private final SxwlSM2KeyManager keyManager;

    /** 登录权限快照失效服务，组织/角色变更后让在线用户无感拿到新权限 */
    private final SxwlAuthCacheService sxwlAuthCacheService;

    public SysUserServiceImpl(SysUserMapper sysUserMapper,
                              PasswordEncoder passwordEncoder,
                              SxwlSM2KeyManager keyManager,
                              SxwlAuthCacheService sxwlAuthCacheService) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.keyManager = keyManager;
        this.sxwlAuthCacheService = sxwlAuthCacheService;
    }

    /**
     * 根据 ID 查询用户
     *
     * @param id 用户 ID
     * @return 用户 DTO，查不到抛 10004 异常
     */
    @Override
    public SysUserDTO getUserById(Long id) {
        SysUserDTO dto = sysUserMapper.getUserById(id);
        if (dto == null) {
            throw new SxwlBusinessException(10004, "用户不存在或已被删除");
        }
        // 编辑回显不返回密码，前端不应展示密码字段
        dto.setPassword(null);
        // 回显当前已分配的角色/组织/岗位，供前端选择器初始化
        dto.setRoleIds(sysUserMapper.getRoleIdsByUserId(id));
        dto.setOrgIds(sysUserMapper.getOrgIdsByUserId(id));
        dto.setPositionId(sysUserMapper.getPositionIdByUserId(id));
        return dto;
    }

    /**
     * 分页查询用户列表
     *
     * @param params 分页 + 筛选参数（username、status）
     * @return 分页结果
     */
    @Override
    public PageInfo<SysUserDTO> getUserPageByParams(SysUserPageParams params) {
        List<SysUserDTO> rows = sysUserMapper.getUserPageByParams(params);
        return new PageInfo<>(rows);
    }

    /**
     * 新增用户
     * <p>包含唯一性校验（用户名、手机号）、密码加密。</p>
     *
     * @param dto 用户 DTO
     * @return 影响行数
     * @throws SxwlBusinessException 用户名/手机号重复或新增失败时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int createUser(SysUserDTO dto) {
        // 仅当分配超级管理员角色时才加行锁串行化，避免所有用户创建被串行阻塞（L7）
        Long superAdminRoleId = sysUserMapper.selectRoleIdByCode(SxwlSystemConstants.ADMIN_ROLE_CODE);
        boolean isSuperAdminRole = superAdminRoleId != null
                && dto.getRoleIds() != null
                && dto.getRoleIds().contains(superAdminRoleId);
        boolean isAdminUsername = SxwlSystemConstants.ADMIN_USERNAME.equals(dto.getUsername());
        if (isSuperAdminRole) {
            superAdminRoleId = sysUserMapper.lockRoleIdByCode(SxwlSystemConstants.ADMIN_ROLE_CODE);
            if (sysUserMapper.countUsersByRoleId(superAdminRoleId) > 0) {
                throw new SxwlBusinessException(10003, "禁止添加超级管理员账号");
            }
        }
        if (isAdminUsername || isSuperAdminRole) {
            throw new SxwlBusinessException(10003,
                    "禁止添加超级管理员账号");
        }
        // 1. 唯一性校验
        if (sysUserMapper.checkUsernameUnique(dto.getUsername(), null) > 0) {
            throw new SxwlBusinessException(10002, "用户名已存在");
        }
        if (sysUserMapper.checkPhoneUnique(dto.getPhone(), null) > 0) {
            throw new SxwlBusinessException(10002, "手机号已被占用");
        }

        // 2. 构建实体（审计字段由 SxwlAutoFillInterceptor 自动填充）
        SysUser entity = toEntity(dto);
        entity.setPassword(encodePassword(dto.getPassword()));
        // 选择了所属组织时,将其作为主组织落地到用户表 create_org（数据权限锚点，
        // AutoFill 仅在 createOrg 为 null 时填充操作者组织，此处显式覆盖）
        if (dto.getOrgIds() != null && !dto.getOrgIds().isEmpty()) {
            entity.setCreateOrg(dto.getOrgIds().get(0));
        }

        int result = sysUserMapper.insertUser(entity);
        if (result != 1) {
            log.error("新增用户失败: username={}, result={}", dto.getUsername(), result);
            throw new SxwlBusinessException(10001, "新增用户失败");
        }

        // 3. 保存用户-角色/组织/岗位关联
        saveUserAssociations(entity.getId(), dto.getRoleIds(), dto.getOrgIds(), dto.getPositionId());
        log.info("新增用户成功: username={}", dto.getUsername());
        return result;
    }

    /**
     * 修改用户
     * <p>唯一性校验排除自身，密码可选——传值才加密更新。</p>
     *
     * @param dto 用户 DTO
     * @return 影响行数
     * @throws SxwlBusinessException 用户名/手机号重复或用户不存在时抛出
     */
    @Override
    public int updateUser(SysUserDTO dto) {
        SysUserDTO oldDto = sysUserMapper.getUserById(dto.getId());
        if (oldDto == null) {
            throw new SxwlBusinessException(10004, "用户不存在或已被删除");
        }
        if (isProtectedAdminUser(oldDto)
                || SxwlSystemConstants.ADMIN_USERNAME.equals(dto.getUsername())) {
            throw new SxwlBusinessException(10003, "超级管理员账号为系统保留账号，不允许修改");
        }
        // 禁止通过修改用户提权：不得为普通用户分配超级管理员角色
        Long superAdminRoleId = sysUserMapper.selectRoleIdByCode(SxwlSystemConstants.ADMIN_ROLE_CODE);
        if (superAdminRoleId != null && dto.getRoleIds() != null && dto.getRoleIds().contains(superAdminRoleId)) {
            throw new SxwlBusinessException(10003, "禁止分配超级管理员角色");
        }
        // 1. 唯一性校验（排除自身）
        if (sysUserMapper.checkUsernameUnique(dto.getUsername(), dto.getId()) > 0) {
            throw new SxwlBusinessException(10002, "用户名已存在");
        }
        if (sysUserMapper.checkPhoneUnique(dto.getPhone(), dto.getId()) > 0) {
            throw new SxwlBusinessException(10002, "手机号已被占用");
        }

        // 2. 查询旧数据并计算字段级变更差异
        SysUser oldEntity = toEntity(oldDto);
        SysUser newEntity = toEntity(dto);
        newEntity.setId(dto.getId());
        String diffJson = SxwlDiffUtils.diff(oldEntity, newEntity);
        if (diffJson != null) {
            SxwlDiffUtils.setContextDiff(diffJson);
        }

        // 3. 构建实体（审计字段由 SxwlAutoFillInterceptor 自动填充）
        SysUser entity = toEntity(dto);

        // 密码可选——传了才修改
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            entity.setPassword(encodePassword(dto.getPassword()));
        }

        // 选择了所属组织时同步主组织锚点 create_org；未选择（清空）则保持原值，
        // 避免数据权限锚点丢失
        if (dto.getOrgIds() != null && !dto.getOrgIds().isEmpty()) {
            entity.setCreateOrg(dto.getOrgIds().get(0));
        }

        int result = sysUserMapper.updateUser(entity);
        if (result == 0) {
            throw new SxwlBusinessException(10004, "用户不存在或已被删除");
        }
        // 同步角色/组织/岗位关联（仅当请求携带分配信息时处理，避免误清）
        boolean assignmentMode = dto.getRoleIds() != null || dto.getOrgIds() != null;
        if (assignmentMode) {
            saveUserAssociations(dto.getId(), dto.getRoleIds(), dto.getOrgIds(), dto.getPositionId());
        }
        // 组织/角色/数据范围锚点可能变更，清除该用户的登录权限快照，
        // 在线用户下一次请求经静默刷新自动重建最新权限，无需重新登录
        sxwlAuthCacheService.evictUserAuthCache(dto.getId());
        log.info("修改用户成功: id={}", dto.getId());
        return result;
    }

    /**
     * 删除用户（逻辑删除）
     *
     * @param id 用户 ID
     * @return 影响行数
     * @throws SxwlBusinessException 用户不存在时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteUserById(Long id) {
        SysUserDTO user = sysUserMapper.getUserById(id);
        if (user != null && isProtectedAdminUser(user)) {
            throw new SxwlBusinessException(10003, "超级管理员账号不允许删除");
        }
        // 同步逻辑删除用户-角色关联，避免孤儿数据（L5）
        sysUserMapper.deleteUserRoleByUserId(id);
        sysUserMapper.deleteUserOrganizationByUserId(id);
        sysUserMapper.deleteUserPositionByUserId(id);
        int affected = sysUserMapper.deleteUserById(id);
        if (affected == 0) {
            throw new SxwlBusinessException(10004, "用户不存在或已被删除");
        }
        log.info("删除用户成功: id={}", id);
        return affected;
    }

    /**
     * 批量删除用户（逻辑删除）
     *
     * @param ids 用户 ID 列表
     * @return 影响行数
     * @throws SxwlBusinessException 列表为空或全部不存在时抛出
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDeleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new SxwlBusinessException(10001, "删除用户列表不能为空");
        }
        for (Long id : ids) {
            SysUserDTO user = id != null ? sysUserMapper.getUserById(id) : null;
            if (user != null && isProtectedAdminUser(user)) {
                throw new SxwlBusinessException(10003, "超级管理员账号不允许删除");
            }
        }
        // 同步逻辑删除用户-角色关联，避免孤儿数据（L5）
        sysUserMapper.deleteUserRoleByUserIds(ids);
        sysUserMapper.deleteUserOrganizationByUserIds(ids);
        sysUserMapper.deleteUserPositionByUserIds(ids);
        int affected = sysUserMapper.batchDeleteByIds(ids);
        if (affected == 0) {
            throw new SxwlBusinessException(10004, "用户不存在或已被删除");
        }
        log.info("批量删除用户成功: ids={}, count={}", ids, affected);
        return affected;
    }

    // ==================== 私有方法 ====================

    /**
     * 全量替换用户的角色/组织/岗位关联。
     * <p>组织列表首个为主组织（is_main=1）；positionId 为 null 表示清除岗位。</p>
     *
     * @param userId     用户 ID
     * @param roleIds    角色 ID 列表
     * @param orgIds     组织 ID 列表
     * @param positionId 岗位 ID（可为 null）
     */
    private void saveUserAssociations(Long userId, List<Long> roleIds, List<Long> orgIds, Long positionId) {
        Long operatorId = currentUserId();
        Long operatorOrgId = currentOrgId();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        // 角色：全量替换
        sysUserMapper.deleteUserRoleByUserId(userId);
        if (roleIds != null && !roleIds.isEmpty()) {
            List<Long> ids = roleIds.stream()
                    .map(r -> SxwlSnowFlakeUtils.nextId())
                    .collect(java.util.stream.Collectors.toList());
            sysUserMapper.batchInsertUserRole(ids, userId, roleIds, operatorId, operatorOrgId, now);
        }

        // 组织：全量替换，首个为主组织
        sysUserMapper.deleteUserOrganizationByUserId(userId);
        if (orgIds != null && !orgIds.isEmpty()) {
            List<Long> ids = orgIds.stream()
                    .map(o -> SxwlSnowFlakeUtils.nextId())
                    .collect(java.util.stream.Collectors.toList());
            sysUserMapper.batchInsertUserOrganization(ids, userId, orgIds, operatorId, operatorOrgId, now);
        }

        // 岗位：全量替换（null 表示清除）
        sysUserMapper.deleteUserPositionByUserId(userId);
        if (positionId != null) {
            sysUserMapper.insertUserPosition(SxwlSnowFlakeUtils.nextId(), userId, positionId, operatorId, operatorOrgId, now);
        }
    }

    /**
     * 取当前登录用户 ID，未登录时回退 0。
     */
    private Long currentUserId() {
        Long userId = SxwlSecurityUtils.getCurrentUserId();
        return userId != null ? userId : 0L;
    }

    /**
     * 取当前登录用户的组织 ID，未登录或无组织信息时回退 0。
     */
    private Long currentOrgId() {
        SxwlLoginUser loginUser = SxwlSecurityUtils.getCurrentUser().orElse(null);
        return loginUser != null && loginUser.getOrgId() != null ? loginUser.getOrgId() : 0L;
    }

    /**
     * SM2 解密 + SM3 编码密码
     *
     * @param encryptedPassword 前端加密后的密码（Base64）
     * @return SM3 哈希后的密文
     */
    private String encodePassword(String encryptedPassword) {
        String plainPassword = keyManager.decrypt(encryptedPassword);
        return passwordEncoder.encode(plainPassword);
    }

    /**
     * DTO 转实体
     *
     * @param dto 用户 DTO
     * @return 用户实体
     */
    private SysUser toEntity(SysUserDTO dto) {
        SysUser entity = new SysUser();
        entity.setId(dto.getId());
        entity.setUsername(dto.getUsername());
        entity.setRealName(dto.getRealName());
        entity.setPhone(dto.getPhone());
        entity.setEmail(dto.getEmail());
        entity.setStatus(dto.getStatus());
        return entity;
    }

    private boolean isProtectedAdminUser(SysUserDTO user) {
        return Boolean.TRUE.equals(user.getSuperAdmin())
                || SxwlSystemConstants.ADMIN_USERNAME.equals(user.getUsername());
    }

}

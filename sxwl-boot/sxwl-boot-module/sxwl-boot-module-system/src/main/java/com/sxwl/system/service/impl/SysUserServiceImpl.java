package com.sxwl.system.service.impl;

import com.github.pagehelper.PageInfo;
import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.common.utils.SM2Utils;
import com.sxwl.security.config.SxwlSecurityProperties;
import com.sxwl.system.mapper.SysUserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.sxwl.system.model.dto.SysUserDTO;
import com.sxwl.system.model.entity.SysUser;
import com.sxwl.system.model.params.SysUserPageParams;
import com.sxwl.system.service.SysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

    /** 安全配置属性 */
    private final SxwlSecurityProperties securityProperties;

    public SysUserServiceImpl(SysUserMapper sysUserMapper,
                              PasswordEncoder passwordEncoder,
                              SxwlSecurityProperties securityProperties) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.securityProperties = securityProperties;
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
    public int createUser(SysUserDTO dto) {
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

        int result = sysUserMapper.insertUser(entity);
        if (result != 1) {
            log.error("新增用户失败: username={}, result={}", dto.getUsername(), result);
            throw new SxwlBusinessException(10001, "新增用户失败");
        }
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
        // 1. 唯一性校验（排除自身）
        if (sysUserMapper.checkUsernameUnique(dto.getUsername(), dto.getId()) > 0) {
            throw new SxwlBusinessException(10002, "用户名已存在");
        }
        if (sysUserMapper.checkPhoneUnique(dto.getPhone(), dto.getId()) > 0) {
            throw new SxwlBusinessException(10002, "手机号已被占用");
        }

        // 2. 构建实体（审计字段由 SxwlAutoFillInterceptor 自动填充）
        SysUser entity = toEntity(dto);

        // 密码可选——传了才修改
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            entity.setPassword(encodePassword(dto.getPassword()));
        }

        int result = sysUserMapper.updateUser(entity);
        if (result == 0) {
            throw new SxwlBusinessException(10004, "用户不存在或已被删除");
        }
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
    public int deleteUserById(Long id) {
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
    public int batchDeleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new SxwlBusinessException(10001, "删除用户列表不能为空");
        }
        int affected = sysUserMapper.batchDeleteByIds(ids);
        if (affected == 0) {
            throw new SxwlBusinessException(10004, "用户不存在或已被删除");
        }
        log.info("批量删除用户成功: ids={}, count={}", ids, affected);
        return affected;
    }

    // ==================== 私有方法 ====================

    /**
     * SM2 解密 + SM3 编码密码
     *
     * @param encryptedPassword 前端加密后的密码（Base64）
     * @return SM3 哈希后的密文
     */
    private String encodePassword(String encryptedPassword) {
        String privateKey = securityProperties.getSm2PrivateKey();
        String plainPassword;
        if (privateKey != null && !privateKey.isBlank()) {
            plainPassword = SM2Utils.decryptFromBase64(encryptedPassword, privateKey);
        } else {
            plainPassword = encryptedPassword;
        }
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

}

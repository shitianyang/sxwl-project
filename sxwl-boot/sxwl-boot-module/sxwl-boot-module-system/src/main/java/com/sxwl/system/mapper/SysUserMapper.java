package com.sxwl.system.mapper;

import com.sxwl.system.model.dto.SysUserDTO;
import com.sxwl.system.model.entity.SysUser;
import com.sxwl.system.model.params.SysUserPageParams;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统用户 Mapper（CRUD 用，独立于 auth 模块的认证专用 Mapper）
 *
 * <p>所有 SQL 定义在 {@code mapper/SysUserMapper.xml} 中。</p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@Mapper
public interface SysUserMapper {

    /**
     * 根据 ID 查询用户（含密码，用于编辑回显）
     *
     * @param id 用户 ID
     * @return 用户 DTO（含密码字段）
     */
    SysUserDTO getUserById(@Param("id") Long id);

    /**
     * 分页查询用户列表
     *
     * @param params 分页查询参数（含 username、status 筛选条件）
     * @return 用户 DTO 列表
     */
    List<SysUserDTO> getUserPageByParams(SysUserPageParams params);

    /**
     * 校验用户名是否唯一（排除指定 ID）
     *
     * @param username  用户名
     * @param excludeId 需要排除的用户 ID（修改时传自身 ID，新增传 null）
     * @return >0 表示已存在
     */
    int checkUsernameUnique(@Param("username") String username,
                            @Param("excludeId") Long excludeId);

    /**
     * 校验手机号是否唯一（排除指定 ID）
     *
     * @param phone     手机号
     * @param excludeId 需要排除的用户 ID（修改时传自身 ID，新增传 null）
     * @return >0 表示已存在
     */
    int checkPhoneUnique(@Param("phone") String phone,
                          @Param("excludeId") Long excludeId);

    /**
     * 新增用户
     *
     * @param entity 用户实体（继承 {@code SxwlBasicField}，自动填充审计字段）
     * @return 影响行数
     */
    int insertUser(SysUser entity);

    /**
     * 修改用户
     *
     * @param entity 用户实体（自动填充 updateBy / updateTime）
     * @return 影响行数
     */
    int updateUser(SysUser entity);

    /**
     * 逻辑删除用户
     *
     * @param id 用户 ID
     * @return 影响行数
     */
    int deleteUserById(@Param("id") Long id);

    /**
     * 批量逻辑删除用户
     *
     * @param ids 用户 ID 列表
     * @return 影响行数
     */
    int batchDeleteByIds(@Param("ids") List<Long> ids);
}

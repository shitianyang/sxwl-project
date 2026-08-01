package com.sxwl.auth.mapper;

import com.sxwl.auth.model.entity.PlaUserInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * C 端用户 Mapper（仅微信登录认证用，不做完整 CRUD）
 *
 * <p>对应底座预留 C 端用户表 pla_user_info，审计字段由自动填充拦截器处理。</p>
 *
 * @author shitianyang
 * @date 2026/7/7
 * @since 0.1.0
 */
@Mapper
public interface PlaUserInfoMapper {

    /**
     * 根据微信 OpenID 查询用户
     */
    @Select("""
            SELECT id, wx_open_id, nickname, avatar, register_source, login_type, status
            FROM pla_user_info
            WHERE wx_open_id = #{openId} AND delete_flag = 0
            """)
    PlaUserInfo selectByOpenId(@Param("openId") String openId);

    /**
     * 新增微信用户（id/create_time/delete_flag 由自动填充拦截器处理）
     */
    @Insert("""
            INSERT INTO pla_user_info
                (id, wx_open_id, nickname, avatar, register_source, login_type, status,
                 create_by, create_org, create_time, delete_flag)
            VALUES
                (#{id}, #{wxOpenId}, #{nickname}, #{avatar}, #{registerSource}, #{loginType}, #{status},
                 #{createBy}, #{createOrg}, #{createTime}, #{deleteFlag})
            """)
    int insertUser(PlaUserInfo entity);

    /**
     * 更新最近登录方式与登录时间
     */
    @Update("""
            UPDATE pla_user_info
            SET login_type = 'wechat', last_login_time = now()
            WHERE id = #{id}
            """)
    int updateLoginInfo(@Param("id") Long id);

    /**
     * 更新昵称与头像（登录时同步微信最新资料）
     */
    @Update("""
            UPDATE pla_user_info
            SET nickname = #{nickname}, avatar = #{avatar}, update_time = now()
            WHERE id = #{id}
            """)
    int updateProfile(@Param("id") Long id,
                      @Param("nickname") String nickname,
                      @Param("avatar") String avatar);
}

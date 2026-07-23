package com.sxwl.rustfs.mapper;

import com.sxwl.rustfs.model.entity.SysFileSessionInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 系统文件上传会话 Mapper
 *
 * <p>所有 SQL 定义在 {@code mapper/SysFileSessionInfoMapper.xml} 中。</p>
 *
 * @author shitianyang
 * @since 0.1.0
 */
@Mapper
public interface SysFileSessionInfoMapper {

    /**
     * 新增上传会话
     *
     * @param entity 会话实体
     * @return 影响行数
     */
    int insertUpload(SysFileSessionInfo entity);

    /**
     * 更新会话状态
     *
     * @param id     会话 ID
     * @param status 新状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 根据 MD5 查询未完成的会话
     *
     * @param fileMd5 文件 MD5
     * @return 会话实体
     */
    SysFileSessionInfo getByMd5(@Param("fileMd5") String fileMd5);

    /**
     * 根据 ID 查询会话
     *
     * @param id 会话 ID
     * @return 会话实体
     */
    SysFileSessionInfo getById(@Param("id") Long id);
}

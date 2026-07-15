package com.sxwl.rustfs.mapper;

import com.sxwl.common.annotation.SxwlDataScope;
import com.sxwl.rustfs.model.dto.SysFileDTO;
import com.sxwl.rustfs.model.entity.SysFileInfo;
import com.sxwl.rustfs.model.params.SysFilePageParams;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 系统文件 Mapper
 *
 * <p>所有 SQL 定义在 {@code mapper/SysFileInfoMapper.xml} 中。</p>
 *
 * @author shitianyang
 * @since 0.1.0
 */
@Mapper
public interface SysFileInfoMapper {

    /**
     * 新增文件
     *
     * @param entity 文件实体
     * @return 影响行数
     */
    int insertFile(SysFileInfo entity);

    /**
     * 分页查询文件列表
     *
     * @param params 分页查询参数
     * @return 文件 DTO 列表
     */
    @SxwlDataScope
    List<SysFileDTO> selectFilePageByParams(SysFilePageParams params);

    /**
     * 根据 ID 查询文件
     *
     * @param id 文件 ID
     * @return 文件实体
     */
    SysFileInfo getFileById(Long id);

    @SxwlDataScope
    SysFileInfo getVisibleFileById(Long id);

    /**
     * 根据 MD5 查询已完成的文件
     *
     * @param md5 文件 MD5
     * @return 文件实体
     */
    SysFileInfo getFileByMd5(String md5);

    /**
     * 软删除文件
     *
     * @param id 文件 ID
     * @return 影响行数
     */
    int deleteFileById(Long id);

}

package com.sxwl.rustfs.mapper;

import com.sxwl.rustfs.model.entity.SysFileChunkInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统文件分片明细 Mapper
 *
 * <p>所有 SQL 定义在 {@code mapper/SysFileChunkInfoMapper.xml} 中。</p>
 *
 * @author shitianyang
 * @since 0.1.0
 */
@Mapper
public interface SysFileChunkInfoMapper {

    /**
     * 新增单个分片记录
     *
     * @param entity 分片实体
     * @return 影响行数
     */
    int insertChunk(SysFileChunkInfo entity);

    /**
     * 批量新增分片记录
     *
     * @param chunks 分片列表
     * @return 影响行数
     */
    int batchInsert(List<SysFileChunkInfo> chunks);

    /**
     * 更新分片状态为已上传
     *
     * @param uploadId   会话 ID
     * @param chunkIndex 分片序号
     * @param objectKey  临时对象键
     * @return 影响行数
     */
    int updateChunkStatus(@Param("uploadId") Long uploadId,
                          @Param("chunkIndex") Integer chunkIndex,
                          @Param("objectKey") String objectKey);

    /**
     * 查询已上传的分片序号列表
     *
     * @param uploadId 会话 ID
     * @return 已上传的分片序号列表
     */
    List<Integer> getUploadedChunks(@Param("uploadId") Long uploadId);

    /**
     * 查询会话的所有分片（按序号排序）
     *
     * @param uploadId 会话 ID
     * @return 分片列表
     */
    List<SysFileChunkInfo> getChunksByUploadId(@Param("uploadId") Long uploadId);

    /**
     * 统计已上传的分片数
     *
     * @param uploadId 会话 ID
     * @return 已上传数
     */
    int countUploadedChunks(@Param("uploadId") Long uploadId);

    /**
     * 判断指定分片是否已上传完成（断点续传幂等，L21）
     *
     * @param uploadId   会话 ID
     * @param chunkIndex 分片序号
     * @return 已上传计数（0 或 1）
     */
    int countChunkUploaded(@Param("uploadId") Long uploadId, @Param("chunkIndex") Integer chunkIndex);

    /**
     * 删除指定会话的全部分片记录（会话过期清理时使用，物理删除）
     *
     * @param uploadId 会话 ID
     * @return 影响行数
     */
    int deleteByUploadId(@Param("uploadId") Long uploadId);
}

package com.sxwl.notice.mapper;

import com.sxwl.notice.model.dto.SysNoticeDTO;
import com.sxwl.notice.model.entity.SysNoticeInfo;
import com.sxwl.notice.model.params.SysNoticePageParams;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知公告 Mapper
 *
 * <p>所有 SQL 定义在 {@code mapper/SysNoticeInfoMapper.xml} 中。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@Mapper
public interface SysNoticeInfoMapper {

    SysNoticeDTO getNoticeById(@Param("id") Long id);

    List<SysNoticeDTO> getNoticePageByParams(SysNoticePageParams params);

    int insertNotice(SysNoticeInfo entity);

    int updateNotice(SysNoticeInfo entity);

    int deleteNoticeById(@Param("id") Long id);

    /**
     * 发布公告（变更状态+发布时间）
     */
    int publishNotice(@Param("id") Long id, @Param("publishTime") java.time.LocalDateTime publishTime);

    /**
     * 撤回公告
     */
    int revokeNotice(@Param("id") Long id);
}

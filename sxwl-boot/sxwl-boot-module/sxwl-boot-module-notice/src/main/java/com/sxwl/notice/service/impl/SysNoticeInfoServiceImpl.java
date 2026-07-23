package com.sxwl.notice.service.impl;

import com.github.pagehelper.PageInfo;
import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.common.utils.SxwlDiffUtils;
import com.sxwl.notice.mapper.SysNoticeInfoMapper;
import com.sxwl.notice.mapper.SysNoticeReadMapper;
import com.sxwl.notice.model.dto.SysNoticeDTO;
import com.sxwl.notice.model.dto.SysNoticeUnreadItem;
import com.sxwl.notice.model.entity.SysNoticeInfo;
import com.sxwl.notice.model.params.SysNoticePageParams;
import com.sxwl.notice.service.SysNoticeInfoService;
import com.sxwl.sse.manager.SxwlSseEmitterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知公告 Service 实现
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@Service
public class SysNoticeInfoServiceImpl implements SysNoticeInfoService {

    private static final Logger log = LoggerFactory.getLogger(SysNoticeInfoServiceImpl.class);

    private final SysNoticeInfoMapper sysNoticeInfoMapper;
    private final SysNoticeReadMapper sysNoticeReadMapper;
    private final SxwlSseEmitterManager sseEmitterManager;

    public SysNoticeInfoServiceImpl(SysNoticeInfoMapper sysNoticeInfoMapper,
                                    SysNoticeReadMapper sysNoticeReadMapper,
                                    SxwlSseEmitterManager sseEmitterManager) {
        this.sysNoticeInfoMapper = sysNoticeInfoMapper;
        this.sysNoticeReadMapper = sysNoticeReadMapper;
        this.sseEmitterManager = sseEmitterManager;
    }

    @Override
    public SysNoticeDTO getNoticeById(Long id) {
        SysNoticeDTO dto = sysNoticeInfoMapper.getNoticeById(id);
        if (dto == null) {
            throw new SxwlBusinessException(10004, "通知公告不存在或已被删除");
        }
        return dto;
    }

    @Override
    public PageInfo<SysNoticeDTO> getNoticePageByParams(SysNoticePageParams params) {
        List<SysNoticeDTO> rows = sysNoticeInfoMapper.getNoticePageByParams(params);
        return new PageInfo<>(rows);
    }

    @Override
    public int createNotice(SysNoticeDTO dto) {
        SysNoticeInfo entity = toEntity(dto);
        int result = sysNoticeInfoMapper.insertNotice(entity);
        if (result != 1) {
            log.error("新增通知公告失败: title={}, result={}", dto.getTitle(), result);
            throw new SxwlBusinessException(10001, "新增通知公告失败");
        }
        log.info("新增通知公告成功: title={}", dto.getTitle());
        return result;
    }

    @Override
    public int updateNotice(SysNoticeDTO dto) {
        // 已发布/已撤回不可编辑
        SysNoticeDTO existing = getNoticeById(dto.getId());
        if (existing.getStatus() != null && existing.getStatus() != 0) {
            throw new SxwlBusinessException(10003, "已发布或已撤回的公告不可编辑");
        }

        SysNoticeInfo entity = toEntity(dto);
        entity.setId(dto.getId());

        // 计算字段级变更差异
        SysNoticeInfo oldEntity = toEntity(existing);
        String diffJson = SxwlDiffUtils.diff(oldEntity, entity);
        if (diffJson != null) {
            SxwlDiffUtils.setContextDiff(diffJson);
        }

        int result = sysNoticeInfoMapper.updateNotice(entity);
        if (result == 0) {
            throw new SxwlBusinessException(10004, "通知公告不存在或已被删除");
        }
        log.info("修改通知公告成功: id={}", dto.getId());
        return result;
    }

    @Override
    public int deleteNoticeById(Long id) {
        int affected = sysNoticeInfoMapper.deleteNoticeById(id);
        if (affected == 0) {
            throw new SxwlBusinessException(10004, "通知公告不存在或已被删除");
        }
        log.info("删除通知公告成功: id={}", id);
        return affected;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishNotice(Long id) {
        int affected = sysNoticeInfoMapper.publishNotice(id, LocalDateTime.now());
        if (affected == 0) {
            throw new SxwlBusinessException(10004, "通知公告不存在或已发布");
        }
        log.info("发布通知公告成功: id={}", id);

        // SSE 推送：广播新公告给所有在线用户
        try {
            List<SysNoticeUnreadItem> recentList = sysNoticeReadMapper.listRecentNotices(null, 1);
            if (!recentList.isEmpty()) {
                sseEmitterManager.sendToAll("new-notice", recentList.get(0));
            }
        } catch (Exception e) {
            log.warn("SSE 推送新公告失败: id={}", id, e);
        }
    }

    @Override
    public void revokeNotice(Long id) {
        int affected = sysNoticeInfoMapper.revokeNotice(id);
        if (affected == 0) {
            throw new SxwlBusinessException(10004, "通知公告不存在或未发布");
        }
        log.info("撤回通知公告成功: id={}", id);
    }

    // ========== 用户未读/已读接口 ==========

    @Override
    public Long getUnreadCount(Long userId) {
        Long count = sysNoticeReadMapper.countUnread(userId);
        return count != null ? count : 0L;
    }

    @Override
    public List<SysNoticeUnreadItem> getRecentUnreadList(Long userId) {
        return sysNoticeReadMapper.listRecentNotices(userId, 10);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long noticeId, Long userId) {
        Long id = System.currentTimeMillis();
        sysNoticeReadMapper.insertRead(id, noticeId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllAsRead(Long userId) {
        sysNoticeReadMapper.insertReadAll(userId);
    }

    private SysNoticeInfo toEntity(SysNoticeDTO dto) {
        SysNoticeInfo entity = new SysNoticeInfo();
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setNoticeType(dto.getNoticeType());
        entity.setLevel(dto.getLevel());
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : 0); // 默认草稿
        entity.setPublishTime(dto.getPublishTime());
        entity.setExpireTime(dto.getExpireTime());
        return entity;
    }
}

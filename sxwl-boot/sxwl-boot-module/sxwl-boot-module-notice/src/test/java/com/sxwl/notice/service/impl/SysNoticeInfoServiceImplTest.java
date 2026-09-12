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
import com.sxwl.sse.manager.SxwlSseEmitterManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * {@link SysNoticeInfoServiceImpl} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SysNoticeInfoServiceImpl 测试")
class SysNoticeInfoServiceImplTest {

    @Mock
    private SysNoticeInfoMapper sysNoticeInfoMapper;

    @Mock
    private SysNoticeReadMapper sysNoticeReadMapper;

    @Mock
    private SxwlSseEmitterManager sseEmitterManager;

    private SysNoticeInfoServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new SysNoticeInfoServiceImpl(sysNoticeInfoMapper, sysNoticeReadMapper, sseEmitterManager);
    }

    @Test
    @DisplayName("getNoticeById 存在时应返回 DTO")
    void getNoticeById_found_shouldReturn() {
        SysNoticeDTO dto = new SysNoticeDTO();
        dto.setId(1L);
        when(sysNoticeInfoMapper.getNoticeById(1L)).thenReturn(dto);

        SysNoticeDTO result = service.getNoticeById(1L);
        assertSame(dto, result);
    }

    @Test
    @DisplayName("getNoticeById 不存在时应抛出异常")
    void getNoticeById_notFound_shouldThrow() {
        when(sysNoticeInfoMapper.getNoticeById(999L)).thenReturn(null);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> service.getNoticeById(999L));
        assertEquals(10004, ex.getCode());
    }

    @Test
    @DisplayName("getNoticePageByParams 应返回分页结果")
    void getNoticePageByParams_shouldReturnPage() {
        SysNoticeDTO dto = new SysNoticeDTO();
        SysNoticePageParams params = new SysNoticePageParams();
        when(sysNoticeInfoMapper.getNoticePageByParams(params)).thenReturn(List.of(dto));

        PageInfo<SysNoticeDTO> result = service.getNoticePageByParams(params);
        assertEquals(1, result.getList().size());
    }

    @Test
    @DisplayName("createNotice 应成功创建")
    void createNotice_shouldSucceed() {
        SysNoticeDTO dto = new SysNoticeDTO();
        dto.setTitle("测试标题");
        dto.setContent("测试内容");
        dto.setNoticeType("notice");
        dto.setLevel("info");

        when(sysNoticeInfoMapper.insertNotice(any(SysNoticeInfo.class))).thenReturn(1);

        int result = service.createNotice(dto);
        assertEquals(1, result);
        verify(sysNoticeInfoMapper).insertNotice(any(SysNoticeInfo.class));
    }

    @Test
    @DisplayName("createNotice 插入失败时应抛出异常")
    void createNotice_insertFailed_shouldThrow() {
        SysNoticeDTO dto = new SysNoticeDTO();
        dto.setTitle("测试标题");

        when(sysNoticeInfoMapper.insertNotice(any(SysNoticeInfo.class))).thenReturn(0);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> service.createNotice(dto));
        assertEquals(10001, ex.getCode());
    }

    @Test
    @DisplayName("updateNotice 已发布公告不可编辑")
    void updateNotice_published_shouldThrow() {
        SysNoticeDTO dto = new SysNoticeDTO();
        dto.setId(1L);

        SysNoticeDTO existing = new SysNoticeDTO();
        existing.setStatus(1);
        when(sysNoticeInfoMapper.getNoticeById(1L)).thenReturn(existing);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> service.updateNotice(dto));
        assertEquals(10003, ex.getCode());
    }

    @Test
    @DisplayName("updateNotice 草稿状态应成功更新")
    void updateNotice_draft_shouldSucceed() {
        SysNoticeDTO dto = new SysNoticeDTO();
        dto.setId(1L);
        dto.setTitle("新标题");
        dto.setContent("新内容");
        dto.setNoticeType("notice");
        dto.setLevel("info");

        SysNoticeDTO existing = new SysNoticeDTO();
        existing.setStatus(0);
        existing.setTitle("旧标题");
        existing.setContent("旧内容");
        existing.setNoticeType("notice");
        existing.setLevel("info");

        when(sysNoticeInfoMapper.getNoticeById(1L)).thenReturn(existing);
        when(sysNoticeInfoMapper.updateNotice(any(SysNoticeInfo.class))).thenReturn(1);

        try (MockedStatic<SxwlDiffUtils> diffUtils = mockStatic(SxwlDiffUtils.class)) {
            diffUtils.when(() -> SxwlDiffUtils.diff(any(SysNoticeInfo.class), any(SysNoticeInfo.class)))
                    .thenReturn("[{\"field\":\"title\",\"old\":\"旧标题\",\"new\":\"新标题\"}]");

            int result = service.updateNotice(dto);
            assertEquals(1, result);
            diffUtils.verify(() -> SxwlDiffUtils.setContextDiff(anyString()));
        }
    }

    @Test
    @DisplayName("updateNotice 更新影响行数为 0 时应抛出异常")
    void updateNotice_zeroAffected_shouldThrow() {
        SysNoticeDTO dto = new SysNoticeDTO();
        dto.setId(999L);

        SysNoticeDTO existing = new SysNoticeDTO();
        existing.setStatus(0);
        when(sysNoticeInfoMapper.getNoticeById(999L)).thenReturn(existing);
        when(sysNoticeInfoMapper.updateNotice(any(SysNoticeInfo.class))).thenReturn(0);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> service.updateNotice(dto));
        assertEquals(10004, ex.getCode());
    }

    @Test
    @DisplayName("deleteNoticeById 应成功删除")
    void deleteNoticeById_shouldSucceed() {
        when(sysNoticeInfoMapper.deleteNoticeById(1L)).thenReturn(1);

        int result = service.deleteNoticeById(1L);
        assertEquals(1, result);
    }

    @Test
    @DisplayName("deleteNoticeById 不存在时应抛出异常")
    void deleteNoticeById_notFound_shouldThrow() {
        when(sysNoticeInfoMapper.deleteNoticeById(999L)).thenReturn(0);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> service.deleteNoticeById(999L));
        assertEquals(10004, ex.getCode());
    }

    @Test
    @DisplayName("publishNotice 应成功发布并推送 SSE")
    void publishNotice_shouldSucceed() {
        when(sysNoticeInfoMapper.publishNotice(eq(1L), any())).thenReturn(1);

        SysNoticeUnreadItem item = new SysNoticeUnreadItem();
        item.setId(1L);
        when(sysNoticeReadMapper.listRecentNotices(null, 1)).thenReturn(List.of(item));

        service.publishNotice(1L);

        verify(sysNoticeReadMapper).listRecentNotices(null, 1);
        verify(sseEmitterManager).sendToAll("new-notice", item);
    }

    @Test
    @DisplayName("publishNotice 发布失败时应抛出异常")
    void publishNotice_failed_shouldThrow() {
        when(sysNoticeInfoMapper.publishNotice(eq(1L), any())).thenReturn(0);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> service.publishNotice(1L));
        assertEquals(10004, ex.getCode());
    }

    @Test
    @DisplayName("revokeNotice 应成功撤回")
    void revokeNotice_shouldSucceed() {
        when(sysNoticeInfoMapper.revokeNotice(1L)).thenReturn(1);

        service.revokeNotice(1L);
        verify(sysNoticeInfoMapper).revokeNotice(1L);
    }

    @Test
    @DisplayName("revokeNotice 撤回失败时应抛出异常")
    void revokeNotice_failed_shouldThrow() {
        when(sysNoticeInfoMapper.revokeNotice(999L)).thenReturn(0);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> service.revokeNotice(999L));
        assertEquals(10004, ex.getCode());
    }

    @Test
    @DisplayName("getUnreadCount 应返回未读数")
    void getUnreadCount_shouldReturn() {
        when(sysNoticeReadMapper.countUnread(1L)).thenReturn(5L);

        Long count = service.getUnreadCount(1L);
        assertEquals(5L, count);
    }

    @Test
    @DisplayName("getUnreadCount 返回 null 时应返回 0")
    void getUnreadCount_null_shouldReturnZero() {
        when(sysNoticeReadMapper.countUnread(1L)).thenReturn(null);

        Long count = service.getUnreadCount(1L);
        assertEquals(0L, count);
    }

    @Test
    @DisplayName("getRecentUnreadList 应返回列表")
    void getRecentUnreadList_shouldReturn() {
        when(sysNoticeReadMapper.listRecentNotices(1L, 10)).thenReturn(List.of(new SysNoticeUnreadItem()));

        List<SysNoticeUnreadItem> result = service.getRecentUnreadList(1L);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("markAsRead 应标记已读")
    void markAsRead_shouldSucceed() {
        service.markAsRead(1L, 1L);
        verify(sysNoticeReadMapper).insertRead(anyLong(), eq(1L), eq(1L));
    }

    @Test
    @DisplayName("markAllAsRead 应标记全部已读")
    void markAllAsRead_shouldSucceed() {
        service.markAllAsRead(1L);
        verify(sysNoticeReadMapper).insertReadAll(1L);
    }
}

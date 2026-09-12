package com.sxwl.notice.controller;

import com.github.pagehelper.PageInfo;
import com.sxwl.notice.model.dto.SysNoticeDTO;
import com.sxwl.notice.model.dto.SysNoticeUnreadItem;
import com.sxwl.notice.model.params.SysNoticePageParams;
import com.sxwl.notice.service.SysNoticeInfoService;
import com.sxwl.security.utils.SxwlSecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * {@link SysNoticeController} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SysNoticeController 测试")
class SysNoticeControllerTest {

    @Mock
    private SysNoticeInfoService sysNoticeInfoService;

    private SysNoticeController controller;

    @BeforeEach
    void setUp() {
        controller = new SysNoticeController(sysNoticeInfoService);
    }

    @Test
    @DisplayName("getNoticeById 应返回公告")
    void getNoticeById_shouldReturn() {
        SysNoticeDTO dto = new SysNoticeDTO();
        when(sysNoticeInfoService.getNoticeById(1L)).thenReturn(dto);

        assertSame(dto, controller.getNoticeById(1L));
    }

    @Test
    @DisplayName("getNoticePageByParams 应返回分页结果")
    void getNoticePageByParams_shouldReturn() {
        SysNoticePageParams params = new SysNoticePageParams();
        PageInfo<SysNoticeDTO> page = new PageInfo<>(List.of(new SysNoticeDTO()));
        when(sysNoticeInfoService.getNoticePageByParams(params)).thenReturn(page);

        assertSame(page, controller.getNoticePageByParams(params));
    }

    @Test
    @DisplayName("createNotice 应调用 service")
    void createNotice_shouldCallService() {
        SysNoticeDTO dto = new SysNoticeDTO();
        controller.createNotice(dto);
        verify(sysNoticeInfoService).createNotice(dto);
    }

    @Test
    @DisplayName("updateNotice 应调用 service")
    void updateNotice_shouldCallService() {
        SysNoticeDTO dto = new SysNoticeDTO();
        controller.updateNotice(dto);
        verify(sysNoticeInfoService).updateNotice(dto);
    }

    @Test
    @DisplayName("deleteNoticeById 应调用 service")
    void deleteNoticeById_shouldCallService() {
        controller.deleteNoticeById(1L);
        verify(sysNoticeInfoService).deleteNoticeById(1L);
    }

    @Test
    @DisplayName("publishNotice 应调用 service")
    void publishNotice_shouldCallService() {
        controller.publishNotice(1L);
        verify(sysNoticeInfoService).publishNotice(1L);
    }

    @Test
    @DisplayName("revokeNotice 应调用 service")
    void revokeNotice_shouldCallService() {
        controller.revokeNotice(2L);
        verify(sysNoticeInfoService).revokeNotice(2L);
    }

    @Test
    @DisplayName("getUnreadCount 已登录应返回未读数")
    void getUnreadCount_loggedIn_shouldReturn() {
        try (MockedStatic<SxwlSecurityUtils> securityUtils = mockStatic(SxwlSecurityUtils.class)) {
            securityUtils.when(SxwlSecurityUtils::getCurrentUserId).thenReturn(1L);
            when(sysNoticeInfoService.getUnreadCount(1L)).thenReturn(3L);

            Long result = controller.getUnreadCount();
            assertEquals(3L, result);
        }
    }

    @Test
    @DisplayName("getUnreadCount 未登录应返回 0")
    void getUnreadCount_notLoggedIn_shouldReturnZero() {
        try (MockedStatic<SxwlSecurityUtils> securityUtils = mockStatic(SxwlSecurityUtils.class)) {
            securityUtils.when(SxwlSecurityUtils::getCurrentUserId).thenReturn(null);

            Long result = controller.getUnreadCount();
            assertEquals(0L, result);
        }
    }

    @Test
    @DisplayName("getUnreadList 已登录应返回列表")
    void getUnreadList_loggedIn_shouldReturn() {
        try (MockedStatic<SxwlSecurityUtils> securityUtils = mockStatic(SxwlSecurityUtils.class)) {
            securityUtils.when(SxwlSecurityUtils::getCurrentUserId).thenReturn(1L);
            when(sysNoticeInfoService.getRecentUnreadList(1L)).thenReturn(List.of(new SysNoticeUnreadItem()));

            List<SysNoticeUnreadItem> result = controller.getUnreadList();
            assertEquals(1, result.size());
        }
    }

    @Test
    @DisplayName("getUnreadList 未登录应返回空列表")
    void getUnreadList_notLoggedIn_shouldReturnEmpty() {
        try (MockedStatic<SxwlSecurityUtils> securityUtils = mockStatic(SxwlSecurityUtils.class)) {
            securityUtils.when(SxwlSecurityUtils::getCurrentUserId).thenReturn(null);

            List<SysNoticeUnreadItem> result = controller.getUnreadList();
            assertTrue(result.isEmpty());
        }
    }

    @Test
    @DisplayName("markAsRead 已登录应标记已读")
    void markAsRead_loggedIn_shouldCallService() {
        try (MockedStatic<SxwlSecurityUtils> securityUtils = mockStatic(SxwlSecurityUtils.class)) {
            securityUtils.when(SxwlSecurityUtils::getCurrentUserId).thenReturn(1L);

            controller.markAsRead(5L);
            verify(sysNoticeInfoService).markAsRead(5L, 1L);
        }
    }

    @Test
    @DisplayName("markAsRead 未登录应跳过")
    void markAsRead_notLoggedIn_shouldSkip() {
        try (MockedStatic<SxwlSecurityUtils> securityUtils = mockStatic(SxwlSecurityUtils.class)) {
            securityUtils.when(SxwlSecurityUtils::getCurrentUserId).thenReturn(null);

            controller.markAsRead(5L);
            verify(sysNoticeInfoService, never()).markAsRead(anyLong(), anyLong());
        }
    }

    @Test
    @DisplayName("markAllAsRead 已登录应标记全部已读")
    void markAllAsRead_loggedIn_shouldCallService() {
        try (MockedStatic<SxwlSecurityUtils> securityUtils = mockStatic(SxwlSecurityUtils.class)) {
            securityUtils.when(SxwlSecurityUtils::getCurrentUserId).thenReturn(1L);

            controller.markAllAsRead();
            verify(sysNoticeInfoService).markAllAsRead(1L);
        }
    }

    @Test
    @DisplayName("markAllAsRead 未登录应跳过")
    void markAllAsRead_notLoggedIn_shouldSkip() {
        try (MockedStatic<SxwlSecurityUtils> securityUtils = mockStatic(SxwlSecurityUtils.class)) {
            securityUtils.when(SxwlSecurityUtils::getCurrentUserId).thenReturn(null);

            controller.markAllAsRead();
            verify(sysNoticeInfoService, never()).markAllAsRead(anyLong());
        }
    }
}

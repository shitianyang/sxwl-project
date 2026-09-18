package com.sxwl.backup.controller;

import com.github.pagehelper.PageInfo;
import com.sxwl.backup.dto.SysBackupDTO;
import com.sxwl.backup.service.SysBackupService;
import com.sxwl.security.utils.SxwlSecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * {@link SysBackupController} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SysBackupController 测试")
class SysBackupControllerTest {

    @Mock
    private SysBackupService sysBackupService;

    private SysBackupController controller;

    @BeforeEach
    void setUp() {
        controller = new SysBackupController(sysBackupService);
    }

    @Test
    @DisplayName("backup 应调用 service.backup")
    void backup_shouldCallService() {
        try (MockedStatic<SxwlSecurityUtils> securityUtils = mockStatic(SxwlSecurityUtils.class)) {
            securityUtils.when(SxwlSecurityUtils::getCurrentUser)
                    .thenReturn(Optional.empty());

            controller.backup();

            verify(sysBackupService).backup(null, null);
        }
    }

    @Test
    @DisplayName("backup 已登录用户应传递 userId")
    void backup_loggedIn_shouldPassUserId() {
        try (MockedStatic<SxwlSecurityUtils> securityUtils = mockStatic(SxwlSecurityUtils.class)) {
            securityUtils.when(SxwlSecurityUtils::getCurrentUser)
                    .thenReturn(Optional.of(new com.sxwl.security.model.SxwlLoginUser() {{
                        setUserId(1L);
                        setCreateOrg(100L);
                    }}));

            controller.backup();

            // 备份需同时携带 userId 与所属组织，供异步线程写入审计字段
            verify(sysBackupService).backup(1L, 100L);
        }
    }

    @Test
    @DisplayName("list 应返回分页结果")
    void list_shouldReturnPageInfo() {
        SysBackupDTO dto = new SysBackupDTO();
        dto.setId(1L);
        dto.setFileName("backup.sql.gz");
        PageInfo<SysBackupDTO> expected = new PageInfo<>(List.of(dto));
        when(sysBackupService.list(1, 20)).thenReturn(expected);

        PageInfo<SysBackupDTO> result = controller.list(1, 20);

        assertSame(expected, result);
        verify(sysBackupService).list(1, 20);
    }

    @Test
    @DisplayName("list 使用默认分页参数")
    void list_shouldUseDefaultPagination() {
        PageInfo<SysBackupDTO> expected = new PageInfo<>(List.of());
        when(sysBackupService.list(1, 20)).thenReturn(expected);

        controller.list(1, 20);

        verify(sysBackupService).list(1, 20);
    }

    @Test
    @DisplayName("restore 应调用 service.restore")
    void restore_shouldCallService() {
        controller.restore(100L);
        verify(sysBackupService).restore(100L);
    }

    @Test
    @DisplayName("delete 应调用 service.delete")
    void delete_shouldCallService() {
        controller.delete(200L);
        verify(sysBackupService).delete(200L);
    }
}

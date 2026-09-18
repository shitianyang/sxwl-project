package com.sxwl.backup.service.impl;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import com.sxwl.backup.dto.SysBackupDTO;
import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.rustfs.client.SxwlRustfsTemplate;
import com.sxwl.rustfs.config.SxwlRustfsProperties;
import com.sxwl.rustfs.mapper.SysFileInfoMapper;
import com.sxwl.rustfs.model.dto.SysFileDTO;
import com.sxwl.rustfs.model.entity.SysFileInfo;
import com.sxwl.rustfs.model.params.SysFilePageParams;
import com.sxwl.websocket.manager.SxwlWebSocketSessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * {@link SysBackupServiceImpl} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SysBackupServiceImpl 测试")
class SysBackupServiceImplTest {

    @Mock
    private SysFileInfoMapper sysFileInfoMapper;

    @Mock
    private SxwlRustfsTemplate rustfsTemplate;

    @Mock
    private SxwlRustfsProperties rustfsProperties;

    @Mock
    private SxwlWebSocketSessionManager wsSessionManager;

    private SysBackupServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new SysBackupServiceImpl(sysFileInfoMapper, rustfsTemplate,
                rustfsProperties, wsSessionManager);
        ReflectionTestUtils.setField(service, "datasourceUrl", "jdbc:postgresql://localhost:5432/mydb");
        ReflectionTestUtils.setField(service, "datasourceUsername", "postgres");
        ReflectionTestUtils.setField(service, "datasourcePassword", "postgres");
        lenient().when(rustfsProperties.getDefaultBucket()).thenReturn("sxwl-files");
    }

    @Test
    @DisplayName("list 应返回分页备份列表")
    void list_shouldReturnPagedBackups() {
        SysFileDTO file1 = new SysFileDTO();
        file1.setId(1L);
        file1.setFileName("backup1.sql.gz");
        file1.setFileSize(2048L);
        file1.setPresignedUrl("http://minio/backup/1");
        file1.setCreateTime("2026-07-05 12:00:00");

        SysFileDTO file2 = new SysFileDTO();
        file2.setId(2L);
        file2.setFileName("backup2.sql.gz");
        file2.setFileSize(4096L);
        file2.setPresignedUrl("http://minio/backup/2");
        file2.setCreateTime("2026-07-05 13:00:00");

        try (MockedStatic<PageMethod> pageMethodMock = mockStatic(PageMethod.class)) {
            when(sysFileInfoMapper.selectFilePageByParams(any(SysFilePageParams.class)))
                    .thenReturn(List.of(file1, file2));

            PageInfo<SysBackupDTO> result = service.list(1, 20);

            assertNotNull(result);
            List<SysBackupDTO> list = result.getList();
            assertEquals(2, list.size());

            SysBackupDTO dto1 = list.get(0);
            assertEquals(1L, dto1.getId());
            assertEquals("backup1.sql.gz", dto1.getFileName());
            assertEquals(2048L, dto1.getFileSize());
            assertEquals("2.00 KB", dto1.getFileSizeDisplay());
            assertEquals("http://minio/backup/1", dto1.getFileUrl());
            assertEquals(1, dto1.getStatus());
            assertEquals("2026-07-05 12:00:00", dto1.getCreateTime());

            SysBackupDTO dto2 = list.get(1);
            assertEquals(2L, dto2.getId());
            assertEquals("backup2.sql.gz", dto2.getFileName());
            assertEquals(4096L, dto2.getFileSize());
            assertEquals("4.00 KB", dto2.getFileSizeDisplay());

            pageMethodMock.verify(() -> PageMethod.startPage(1, 20));
        }
    }

    @Test
    @DisplayName("restore 备份记录不存在时应抛出异常")
    void restore_notFound_shouldThrow() {
        when(sysFileInfoMapper.getVisibleFileById(999L)).thenReturn(null);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> service.restore(999L));
        assertEquals(10004, ex.getCode());
    }

    @Test
    @DisplayName("restore 业务类型不是 db_backup 时应抛出异常")
    void restore_wrongBusinessType_shouldThrow() {
        SysFileInfo fileInfo = new SysFileInfo();
        fileInfo.setBusinessType("avatar");

        when(sysFileInfoMapper.getVisibleFileById(1L)).thenReturn(fileInfo);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> service.restore(1L));
        assertEquals(10001, ex.getCode());
    }

    @Test
    @DisplayName("restore 对象键为空时应抛出异常")
    void restore_emptyObjectKey_shouldThrow() {
        SysFileInfo fileInfo = new SysFileInfo();
        fileInfo.setBusinessType("db_backup");
        fileInfo.setObjectKey(null);

        when(sysFileInfoMapper.getVisibleFileById(1L)).thenReturn(fileInfo);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> service.restore(1L));
        assertEquals(10001, ex.getCode());
    }

    @Test
    @DisplayName("restore 应提示功能未开放")
    void restore_shouldThrowUnavailable() {
        SysFileInfo fileInfo = new SysFileInfo();
        fileInfo.setBusinessType("db_backup");
        fileInfo.setObjectKey("backup/20260705/file.sql.gz");
        fileInfo.setFileName("test.sql.gz");

        when(sysFileInfoMapper.getVisibleFileById(1L)).thenReturn(fileInfo);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> service.restore(1L));
        assertEquals(10001, ex.getCode());
    }

    @Test
    @DisplayName("delete 备份记录不存在时应抛出异常")
    void delete_notFound_shouldThrow() {
        when(sysFileInfoMapper.getVisibleFileById(999L)).thenReturn(null);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> service.delete(999L));
        assertEquals(10004, ex.getCode());
    }

    @Test
    @DisplayName("delete 应删除 S3 对象并软删除数据库记录")
    void delete_shouldDeleteS3AndDb() {
        SysFileInfo fileInfo = new SysFileInfo();
        fileInfo.setBucketName("sxwl-files");
        fileInfo.setObjectKey("backup/20260705/file.sql.gz");

        when(sysFileInfoMapper.getVisibleFileById(1L)).thenReturn(fileInfo);
        when(sysFileInfoMapper.deleteFileById(1L)).thenReturn(1);

        service.delete(1L);

        verify(rustfsTemplate).delete("sxwl-files", "backup/20260705/file.sql.gz");
        verify(sysFileInfoMapper).deleteFileById(1L);
    }

    @Test
    @DisplayName("delete S3 删除失败时应忽略并继续执行")
    void delete_s3DeleteFailed_shouldIgnore() {
        SysFileInfo fileInfo = new SysFileInfo();
        fileInfo.setBucketName(null);
        fileInfo.setObjectKey("backup/20260705/file.sql.gz");

        when(sysFileInfoMapper.getVisibleFileById(1L)).thenReturn(fileInfo);
        doThrow(new RuntimeException("S3 error")).when(rustfsTemplate)
                .delete(eq("sxwl-files"), eq("backup/20260705/file.sql.gz"));
        when(sysFileInfoMapper.deleteFileById(1L)).thenReturn(1);

        assertDoesNotThrow(() -> service.delete(1L));

        verify(rustfsTemplate).delete("sxwl-files", "backup/20260705/file.sql.gz");
        verify(sysFileInfoMapper).deleteFileById(1L);
    }

    @Test
    @DisplayName("delete 数据库删除影响行数为 0 时应抛出异常")
    void delete_dbDeleteZero_shouldThrow() {
        SysFileInfo fileInfo = new SysFileInfo();
        fileInfo.setObjectKey("backup/20260705/file.sql.gz");

        when(sysFileInfoMapper.getVisibleFileById(1L)).thenReturn(fileInfo);
        when(sysFileInfoMapper.deleteFileById(1L)).thenReturn(0);

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> service.delete(1L));
        assertEquals(10004, ex.getCode());
    }

    @Test
    @DisplayName("backup 应执行完整备份流程")
    void backup_shouldExecuteFullFlow() throws Exception {
        Long userId = 1L;

        Process mockProcess = mock(Process.class);
        when(mockProcess.getInputStream()).thenReturn(new ByteArrayInputStream("SQL data".getBytes()));
        when(mockProcess.waitFor()).thenReturn(0);

        try (MockedConstruction<ProcessBuilder> ignored = mockConstruction(
                ProcessBuilder.class,
                (mock, context) -> {
                    when(mock.start()).thenReturn(mockProcess);
                })) {

            service.backup(userId, 100L);

            ArgumentCaptor<SysFileInfo> captor = ArgumentCaptor.forClass(SysFileInfo.class);
            verify(sysFileInfoMapper).insertFile(captor.capture());

            SysFileInfo saved = captor.getValue();
            assertEquals("db_backup", saved.getBusinessType());
            assertEquals("application/gzip", saved.getFileType());
            assertEquals(1, saved.getStatus());
            // 组织 ID 必须由调用方同步传入并落入文件记录，否则数据权限无法过滤备份件
            assertEquals(100L, saved.getCreateOrg());
            assertNotNull(saved.getFileName());
            assertTrue(saved.getFileName().startsWith("backup_mydb_"));
            assertNotNull(saved.getObjectKey());
            assertNotNull(saved.getDescription());
        }
    }

    @Test
    @DisplayName("backup 数据库 URL 无法解析时应抛出异常")
    void backup_invalidDbUrl_shouldThrow() {
        ReflectionTestUtils.setField(service, "datasourceUrl", "");
        Long userId = 1L;

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> service.backup(userId, null));
        assertEquals(10001, ex.getCode());
    }
}

package com.sxwl.rustfs.service.impl;

import com.github.pagehelper.PageInfo;
import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.common.utils.SxwlSnowFlakeUtils;
import com.sxwl.rustfs.client.SxwlRustfsTemplate;
import com.sxwl.rustfs.config.SxwlRustfsProperties;
import com.sxwl.rustfs.mapper.SysFileChunkInfoMapper;
import com.sxwl.rustfs.mapper.SysFileInfoMapper;
import com.sxwl.rustfs.mapper.SysFileSessionInfoMapper;
import com.sxwl.rustfs.model.dto.*;
import com.sxwl.rustfs.model.entity.SysFileChunkInfo;
import com.sxwl.rustfs.model.entity.SysFileInfo;
import com.sxwl.rustfs.model.entity.SysFileSessionInfo;
import com.sxwl.rustfs.model.params.SysFilePageParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * {@link SysFileServiceImpl} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SysFileServiceImpl 测试")
class SysFileServiceImplTest {

    @Mock
    private SysFileInfoMapper sysFileInfoMapper;

    @Mock
    private SysFileSessionInfoMapper sysFileSessionInfoMapper;

    @Mock
    private SysFileChunkInfoMapper sysFileChunkInfoMapper;

    @Mock
    private SxwlRustfsTemplate rustfsTemplate;

    @Mock
    private SxwlRustfsProperties properties;

    private SysFileServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new SysFileServiceImpl(sysFileInfoMapper, sysFileSessionInfoMapper,
                sysFileChunkInfoMapper, rustfsTemplate, properties);
        lenient().when(properties.getDefaultBucket()).thenReturn("sxwl-files");
        lenient().when(properties.getTempPathPrefix()).thenReturn("tmp/");
        lenient().when(properties.getPresignedUrlExpire()).thenReturn(3600L);
    }

    // ===== initUpload =====

    @Test
    @DisplayName("initUpload 有未完成会话时应返回已有 ID")
    void initUpload_existingSession_shouldReturnExistingId() {
        UploadInitDTO dto = new UploadInitDTO();
        dto.setFileMd5("abc123");

        SysFileSessionInfo existing = new SysFileSessionInfo();
        existing.setId(5L);
        when(sysFileSessionInfoMapper.getByMd5("abc123")).thenReturn(existing);

        Long result = service.initUpload(dto);
        assertEquals(5L, result);
        verify(sysFileSessionInfoMapper, never()).insertUpload(any());
        verify(sysFileChunkInfoMapper, never()).batchInsert(any());
    }

    @Test
    @DisplayName("initUpload 新会话应创建并返回新 ID")
    void initUpload_newSession_shouldCreateAndReturnId() {
        UploadInitDTO dto = new UploadInitDTO();
        dto.setFileMd5("abc123");
        dto.setOriginalName("test.png");
        dto.setFileSize(1024L);
        dto.setContentType("image/png");
        dto.setTotalChunks(4);
        dto.setChunkSize(256);

        when(sysFileSessionInfoMapper.getByMd5("abc123")).thenReturn(null);

        final SysFileSessionInfo[] capturedSession = new SysFileSessionInfo[1];
        doAnswer(invocation -> {
            SysFileSessionInfo s = invocation.getArgument(0);
            s.setId(1L);
            capturedSession[0] = s;
            return 1;
        }).when(sysFileSessionInfoMapper).insertUpload(any(SysFileSessionInfo.class));

        try (MockedStatic<SxwlSnowFlakeUtils> snowFlake = mockStatic(SxwlSnowFlakeUtils.class)) {
            snowFlake.when(SxwlSnowFlakeUtils::nextId).thenReturn(100L, 200L, 300L, 400L);

            ArgumentCaptor<List<SysFileChunkInfo>> chunksCaptor = ArgumentCaptor.forClass(List.class);
            when(sysFileChunkInfoMapper.batchInsert(chunksCaptor.capture())).thenReturn(4);

            Long result = service.initUpload(dto);

            assertNotNull(capturedSession[0]);
            assertEquals("abc123", capturedSession[0].getFileMd5());
            assertEquals(4, capturedSession[0].getTotalChunks());
            assertEquals(1L, capturedSession[0].getId());
            assertEquals(0, capturedSession[0].getStatus().intValue());

            List<SysFileChunkInfo> chunks = chunksCaptor.getValue();
            assertEquals(4, chunks.size());
            assertEquals(0, chunks.get(0).getChunkIndex().intValue());
            assertEquals(3, chunks.get(3).getChunkIndex().intValue());
            assertEquals(100L, chunks.get(0).getId().longValue());

            assertEquals(1L, result);
        }
    }

    // ===== uploadChunk =====

    @Test
    @DisplayName("uploadChunk 会话不存在时应抛出异常")
    void uploadChunk_sessionNotFound_shouldThrow() {
        when(sysFileSessionInfoMapper.getById(999L)).thenReturn(null);

        MultipartFile file = mock(MultipartFile.class);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.uploadChunk(999L, 0, null, file));
        assertTrue(ex.getMessage().contains("不存在"));
    }

    @Test
    @DisplayName("uploadChunk 会话已结束时应抛出异常")
    void uploadChunk_sessionCompleted_shouldThrow() {
        SysFileSessionInfo session = new SysFileSessionInfo();
        session.setStatus(1);
        when(sysFileSessionInfoMapper.getById(1L)).thenReturn(session);

        MultipartFile file = mock(MultipartFile.class);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.uploadChunk(1L, 0, null, file));
        assertTrue(ex.getMessage().contains("已结束"));
    }

    @Test
    @DisplayName("uploadChunk 分片序号超出范围时应抛出异常")
    void uploadChunk_invalidIndex_shouldThrow() {
        SysFileSessionInfo session = new SysFileSessionInfo();
        session.setStatus(0);
        session.setTotalChunks(4);
        when(sysFileSessionInfoMapper.getById(1L)).thenReturn(session);

        MultipartFile file = mock(MultipartFile.class);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.uploadChunk(1L, 5, null, file));
        assertTrue(ex.getMessage().contains("超出范围"));
    }

    @Test
    @DisplayName("uploadChunk 成功应上传到 S3 并更新状态")
    void uploadChunk_success_shouldUploadAndUpdate() throws Exception {
        SysFileSessionInfo session = new SysFileSessionInfo();
        session.setStatus(0);
        session.setTotalChunks(4);
        when(sysFileSessionInfoMapper.getById(1L)).thenReturn(session);

        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("data".getBytes()));
        when(file.getSize()).thenReturn(4L);
        when(file.getContentType()).thenReturn("application/octet-stream");

        UploadChunkDTO result = service.uploadChunk(1L, 2, "chunk-md5", file);

        assertEquals(1L, result.getUploadId());
        assertEquals(2, result.getChunkIndex().intValue());

        verify(rustfsTemplate).upload(eq("sxwl-files"), eq("tmp/1/2"), any(InputStream.class), eq(4L), eq("application/octet-stream"));
        verify(sysFileChunkInfoMapper).updateChunkStatus(1L, 2, "tmp/1/2");
    }

    @Test
    @DisplayName("uploadChunk S3 上传失败时应抛出 SxwlBusinessException")
    void uploadChunk_s3Failed_shouldThrow() throws Exception {
        SysFileSessionInfo session = new SysFileSessionInfo();
        session.setStatus(0);
        session.setTotalChunks(4);
        when(sysFileSessionInfoMapper.getById(1L)).thenReturn(session);

        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenThrow(new RuntimeException("S3 error"));

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> service.uploadChunk(1L, 0, null, file));
        assertEquals(10001, ex.getCode());
    }

    // ===== getUploadedChunks =====

    @Test
    @DisplayName("getUploadedChunks 会话不存在时应返回 null")
    void getUploadedChunks_sessionNotFound_shouldReturnNull() {
        when(sysFileSessionInfoMapper.getByMd5("abc")).thenReturn(null);

        assertNull(service.getUploadedChunks("abc"));
    }

    @Test
    @DisplayName("getUploadedChunks 存在会话时应返回续传信息")
    void getUploadedChunks_found_shouldReturn() {
        SysFileSessionInfo session = new SysFileSessionInfo();
        session.setId(1L);
        when(sysFileSessionInfoMapper.getByMd5("abc")).thenReturn(session);
        when(sysFileChunkInfoMapper.getUploadedChunks(1L)).thenReturn(List.of(0, 1, 2));

        ChunkCheckDTO result = service.getUploadedChunks("abc");
        assertEquals(1L, result.getUploadId());
        assertEquals(List.of(0, 1, 2), result.getUploadedChunks());
    }

    // ===== completeUpload =====

    @Test
    @DisplayName("completeUpload 会话不存在时应抛出异常")
    void completeUpload_sessionNotFound_shouldThrow() {
        UploadCompleteDTO dto = new UploadCompleteDTO();
        dto.setUploadId(999L);
        when(sysFileSessionInfoMapper.getById(999L)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.completeUpload(dto));
        assertTrue(ex.getMessage().contains("不存在"));
    }

    @Test
    @DisplayName("completeUpload 会话已结束时应抛出异常")
    void completeUpload_sessionCompleted_shouldThrow() {
        SysFileSessionInfo session = new SysFileSessionInfo();
        session.setStatus(1);
        when(sysFileSessionInfoMapper.getById(1L)).thenReturn(session);

        UploadCompleteDTO dto = new UploadCompleteDTO();
        dto.setUploadId(1L);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.completeUpload(dto));
        assertTrue(ex.getMessage().contains("已结束"));
    }

    @Test
    @DisplayName("completeUpload 分片未全部上传时应抛出异常")
    void completeUpload_notAllChunks_shouldThrow() {
        SysFileSessionInfo session = new SysFileSessionInfo();
        session.setStatus(0);
        session.setTotalChunks(4);
        when(sysFileSessionInfoMapper.getById(1L)).thenReturn(session);
        when(sysFileChunkInfoMapper.countUploadedChunks(1L)).thenReturn(2);

        UploadCompleteDTO dto = new UploadCompleteDTO();
        dto.setUploadId(1L);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.completeUpload(dto));
        assertTrue(ex.getMessage().contains("未全部上传"));
    }

    @Test
    @DisplayName("completeUpload 成功应合并分片并返回文件信息")
    void completeUpload_success_shouldComposeAndReturn() {
        SysFileSessionInfo session = new SysFileSessionInfo();
        session.setId(1L);
        session.setFileMd5("abc123");
        session.setStatus(0);
        session.setTotalChunks(2);
        session.setOriginalName("test.png");
        session.setFileSize(1024L);
        session.setContentType("image/png");

        SysFileChunkInfo chunk1 = new SysFileChunkInfo();
        chunk1.setObjectKey("tmp/1/0");
        SysFileChunkInfo chunk2 = new SysFileChunkInfo();
        chunk2.setObjectKey("tmp/1/1");

        when(sysFileSessionInfoMapper.getById(1L)).thenReturn(session);
        when(sysFileChunkInfoMapper.countUploadedChunks(1L)).thenReturn(2);
        when(sysFileChunkInfoMapper.getChunksByUploadId(1L)).thenReturn(List.of(chunk1, chunk2));
        when(sysFileInfoMapper.insertFile(any(SysFileInfo.class))).thenReturn(1);
        when(sysFileSessionInfoMapper.updateStatus(1L, 1)).thenReturn(1);

        UploadCompleteDTO dto = new UploadCompleteDTO();
        dto.setUploadId(1L);
        dto.setFileMd5("abc123");

        SysFileDTO result = service.completeUpload(dto);

        assertNotNull(result);
        assertEquals("test.png", result.getFileName());
        assertEquals(1024L, result.getFileSize());
        assertEquals("image/png", result.getFileType());

        verify(rustfsTemplate).composeObject(eq("sxwl-files"), eq(List.of("tmp/1/0", "tmp/1/1")), anyString());
        verify(rustfsTemplate).deleteByPrefix("sxwl-files", "tmp/1/");
        verify(sysFileSessionInfoMapper).updateStatus(1L, 1);
    }

    // ===== simpleUpload =====

    @Test
    @DisplayName("simpleUpload 成功应上传到 S3 并返回文件信息")
    void simpleUpload_success_shouldUploadAndReturn() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.png");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("data".getBytes()));
        when(file.getSize()).thenReturn(4L);
        when(file.getContentType()).thenReturn("image/png");

        when(sysFileInfoMapper.insertFile(any(SysFileInfo.class))).thenReturn(1);

        SysFileDTO result = service.simpleUpload(file);

        assertNotNull(result);
        assertEquals("test.png", result.getFileName());
        assertEquals(4L, result.getFileSize());
        assertEquals("image/png", result.getFileType());
        assertEquals("png", result.getFileSuffix());

        verify(rustfsTemplate).upload(eq("sxwl-files"), anyString(), any(InputStream.class), eq(4L), eq("image/png"));
    }

    @Test
    @DisplayName("simpleUpload 原始文件名为空时应使用 unknown")
    void simpleUpload_emptyName_shouldUseUnknown() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("data".getBytes()));
        when(file.getSize()).thenReturn(4L);
        when(file.getContentType()).thenReturn("application/octet-stream");
        when(sysFileInfoMapper.insertFile(any(SysFileInfo.class))).thenReturn(1);

        SysFileDTO result = service.simpleUpload(file);

        assertEquals("unknown", result.getFileName());
        assertNull(result.getFileSuffix());
    }

    @Test
    @DisplayName("simpleUpload S3 上传失败时应抛出异常")
    void simpleUpload_s3Failed_shouldThrow() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.png");
        when(file.getInputStream()).thenThrow(new RuntimeException("S3 error"));

        SxwlBusinessException ex = assertThrows(SxwlBusinessException.class,
                () -> service.simpleUpload(file));
        assertEquals(10001, ex.getCode());
    }

    // ===== downloadFile =====

    @Test
    @DisplayName("downloadFile 文件不存在时应抛出异常")
    void downloadFile_notFound_shouldThrow() {
        when(sysFileInfoMapper.getVisibleFileById(999L)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.downloadFile(999L));
        assertTrue(ex.getMessage().contains("不存在"));
    }

    @Test
    @DisplayName("downloadFile 存在时应返回文件流")
    void downloadFile_found_shouldReturn() {
        SysFileInfo fileInfo = new SysFileInfo();
        fileInfo.setFileName("test.png");
        fileInfo.setFileType("image/png");
        fileInfo.setObjectKey("2026/07/24/uuid.png");
        fileInfo.setBucketName("sxwl-files");

        when(sysFileInfoMapper.getVisibleFileById(1L)).thenReturn(fileInfo);

        InputStream mockStream = new ByteArrayInputStream("data".getBytes());
        when(rustfsTemplate.download("sxwl-files", "2026/07/24/uuid.png")).thenReturn(mockStream);

        ResponseEntity<Resource> response = service.downloadFile(1L);

        assertEquals(MediaType.parseMediaType("image/png"), response.getHeaders().getContentType());
        assertTrue(response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION).contains("test.png"));
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("downloadFile bucketName 为 null 时应使用默认 bucket")
    void downloadFile_nullBucket_shouldUseDefault() {
        SysFileInfo fileInfo = new SysFileInfo();
        fileInfo.setFileName("test.png");
        fileInfo.setFileType("image/png");
        fileInfo.setObjectKey("2026/07/24/uuid.png");
        // bucketName is null

        when(sysFileInfoMapper.getVisibleFileById(1L)).thenReturn(fileInfo);
        when(rustfsTemplate.download("sxwl-files", "2026/07/24/uuid.png")).thenReturn(new ByteArrayInputStream("data".getBytes()));

        ResponseEntity<Resource> response = service.downloadFile(1L);

        assertEquals(MediaType.parseMediaType("image/png"), response.getHeaders().getContentType());
        assertNotNull(response.getBody());
    }

    // ===== getPresignedUrl =====

    @Test
    @DisplayName("getPresignedUrl 文件不存在时应抛出异常")
    void getPresignedUrl_notFound_shouldThrow() {
        when(sysFileInfoMapper.getVisibleFileById(999L)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.getPresignedUrl(999L));
        assertTrue(ex.getMessage().contains("不存在"));
    }

    @Test
    @DisplayName("getPresignedUrl 存在时应返回 URL")
    void getPresignedUrl_found_shouldReturn() {
        SysFileInfo fileInfo = new SysFileInfo();
        fileInfo.setObjectKey("2026/07/24/uuid.png");
        fileInfo.setBucketName("sxwl-files");

        when(sysFileInfoMapper.getVisibleFileById(1L)).thenReturn(fileInfo);
        when(rustfsTemplate.generatePresignedUrl("sxwl-files", "2026/07/24/uuid.png", java.time.Duration.ofSeconds(3600)))
                .thenReturn("http://presigned-url");

        String result = service.getPresignedUrl(1L);
        assertEquals("http://presigned-url", result);
    }

    // ===== checkMd5 =====

    @Test
    @DisplayName("checkMd5 存在时应返回 DTO")
    void checkMd5_found_shouldReturnDto() {
        SysFileInfo fileInfo = new SysFileInfo();
        fileInfo.setId(1L);
        fileInfo.setFileName("test.png");
        fileInfo.setFileSize(1024L);
        fileInfo.setFileType("image/png");
        fileInfo.setFileSuffix("png");
        fileInfo.setMd5("abc123");
        fileInfo.setCreateTime(LocalDateTime.of(2026, 7, 24, 12, 0, 0));

        when(sysFileInfoMapper.getFileByMd5("abc123")).thenReturn(fileInfo);

        SysFileDTO result = service.checkMd5("abc123");
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test.png", result.getFileName());
        assertEquals("abc123", result.getMd5());
    }

    @Test
    @DisplayName("checkMd5 不存在时应返回 null")
    void checkMd5_notFound_shouldReturnNull() {
        when(sysFileInfoMapper.getFileByMd5("not-exist")).thenReturn(null);

        assertNull(service.checkMd5("not-exist"));
    }

    // ===== deleteFile =====

    @Test
    @DisplayName("deleteFile 文件不存在时应抛出异常")
    void deleteFile_notFound_shouldThrow() {
        when(sysFileInfoMapper.getVisibleFileById(999L)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.deleteFile(999L));
        assertTrue(ex.getMessage().contains("不存在"));
    }

    @Test
    @DisplayName("deleteFile 存在时应软删除")
    void deleteFile_found_shouldSoftDelete() {
        SysFileInfo fileInfo = new SysFileInfo();
        fileInfo.setObjectKey("2026/07/24/uuid.png");
        when(sysFileInfoMapper.getVisibleFileById(1L)).thenReturn(fileInfo);
        when(sysFileInfoMapper.deleteFileById(1L)).thenReturn(1);

        service.deleteFile(1L);
        verify(sysFileInfoMapper).deleteFileById(1L);
    }

    // ===== getFilePageByParams =====

    @Test
    @DisplayName("getFilePageByParams 应返回分页结果")
    void getFilePageByParams_shouldReturnPage() {
        SysFileDTO dto = new SysFileDTO();
        SysFilePageParams params = new SysFilePageParams();
        when(sysFileInfoMapper.selectFilePageByParams(params)).thenReturn(List.of(dto));

        PageInfo<SysFileDTO> result = service.getFilePageByParams(params);
        assertEquals(1, result.getList().size());
        assertSame(dto, result.getList().get(0));
    }

    @Test
    @DisplayName("getFilePageByParams 无数据时应返回空分页")
    void getFilePageByParams_empty_shouldReturnEmptyPage() {
        SysFilePageParams params = new SysFilePageParams();
        when(sysFileInfoMapper.selectFilePageByParams(params)).thenReturn(List.of());

        PageInfo<SysFileDTO> result = service.getFilePageByParams(params);
        assertTrue(result.getList().isEmpty());
    }
}

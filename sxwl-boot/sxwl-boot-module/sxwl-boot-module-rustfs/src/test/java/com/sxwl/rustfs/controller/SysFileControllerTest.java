package com.sxwl.rustfs.controller;

import com.github.pagehelper.PageInfo;
import com.sxwl.rustfs.model.dto.*;
import com.sxwl.rustfs.model.params.SysFilePageParams;
import com.sxwl.rustfs.service.SysFileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * {@link SysFileController} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SysFileController 测试")
class SysFileControllerTest {

    @Mock
    private SysFileService sysFileService;

    private SysFileController controller;

    @BeforeEach
    void setUp() {
        controller = new SysFileController(sysFileService);
    }

    @Test
    @DisplayName("simpleUpload 应调用 service")
    void simpleUpload_shouldCallService() {
        MultipartFile file = mock(MultipartFile.class);
        SysFileDTO dto = new SysFileDTO();
        when(sysFileService.simpleUpload(file)).thenReturn(dto);

        SysFileDTO result = controller.simpleUpload(file);
        assertSame(dto, result);
        verify(sysFileService).simpleUpload(file);
    }

    @Test
    @DisplayName("initUpload 应返回会话 ID")
    void initUpload_shouldReturnSessionId() {
        UploadInitDTO dto = new UploadInitDTO();
        when(sysFileService.initUpload(dto)).thenReturn(1L);

        Long result = controller.initUpload(dto);
        assertEquals(1L, result);
    }

    @Test
    @DisplayName("uploadChunk 应返回分片上传结果")
    void uploadChunk_shouldReturn() {
        MultipartFile file = mock(MultipartFile.class);
        UploadChunkDTO chunkDto = new UploadChunkDTO(1L, 0);
        when(sysFileService.uploadChunk(1L, 0, "md5", file)).thenReturn(chunkDto);

        UploadChunkDTO result = controller.uploadChunk(1L, 0, "md5", file);
        assertSame(chunkDto, result);
    }

    @Test
    @DisplayName("uploadChunk 不传 chunkMd5 时应调用 service")
    void uploadChunk_withoutMd5_shouldCallService() {
        MultipartFile file = mock(MultipartFile.class);
        UploadChunkDTO chunkDto = new UploadChunkDTO(1L, 1);
        when(sysFileService.uploadChunk(1L, 1, null, file)).thenReturn(chunkDto);

        UploadChunkDTO result = controller.uploadChunk(1L, 1, null, file);
        assertSame(chunkDto, result);
    }

    @Test
    @DisplayName("getUploadedChunks 应返回已上传分片")
    void getUploadedChunks_shouldReturn() {
        ChunkCheckDTO dto = new ChunkCheckDTO(1L, List.of(0, 1));
        when(sysFileService.getUploadedChunks("abc")).thenReturn(dto);

        ChunkCheckDTO result = controller.getUploadedChunks("abc");
        assertSame(dto, result);
    }

    @Test
    @DisplayName("completeUpload 应返回文件信息")
    void completeUpload_shouldReturn() {
        UploadCompleteDTO dto = new UploadCompleteDTO();
        SysFileDTO fileDto = new SysFileDTO();
        when(sysFileService.completeUpload(dto)).thenReturn(fileDto);

        SysFileDTO result = controller.completeUpload(dto);
        assertSame(fileDto, result);
    }

    @Test
    @DisplayName("checkMd5 应返回文件信息")
    void checkMd5_shouldReturn() {
        SysFileDTO dto = new SysFileDTO();
        when(sysFileService.checkMd5("abc")).thenReturn(dto);

        SysFileDTO result = controller.checkMd5("abc");
        assertSame(dto, result);
    }

    @Test
    @DisplayName("checkMd5 不存在应返回 null")
    void checkMd5_notFound_shouldReturnNull() {
        when(sysFileService.checkMd5("not-exist")).thenReturn(null);

        assertNull(controller.checkMd5("not-exist"));
    }

    @Test
    @DisplayName("downloadFile 应返回文件流")
    void downloadFile_shouldReturn() {
        ResponseEntity<Resource> response = ResponseEntity.ok().build();
        when(sysFileService.downloadFile(1L)).thenReturn(response);

        ResponseEntity<Resource> result = controller.downloadFile(1L);
        assertSame(response, result);
    }

    @Test
    @DisplayName("getPresignedUrl 应返回 URL")
    void getPresignedUrl_shouldReturn() {
        when(sysFileService.getPresignedUrl(1L)).thenReturn("http://presigned-url");

        String result = controller.getPresignedUrl(1L);
        assertEquals("http://presigned-url", result);
    }

    @Test
    @DisplayName("getFilePageByParams 应返回分页结果")
    void getFilePageByParams_shouldReturn() {
        SysFilePageParams params = new SysFilePageParams();
        PageInfo<SysFileDTO> page = new PageInfo<>(List.of(new SysFileDTO()));
        when(sysFileService.getFilePageByParams(params)).thenReturn(page);

        PageInfo<SysFileDTO> result = controller.getFilePageByParams(params);
        assertSame(page, result);
    }

    @Test
    @DisplayName("deleteFile 应调用 service")
    void deleteFile_shouldCallService() {
        controller.deleteFile(1L);
        verify(sysFileService).deleteFile(1L);
    }
}

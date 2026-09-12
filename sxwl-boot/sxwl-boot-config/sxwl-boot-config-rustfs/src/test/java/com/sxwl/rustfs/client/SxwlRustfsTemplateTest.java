package com.sxwl.rustfs.client;

import com.sxwl.rustfs.config.SxwlRustfsProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * {@link SxwlRustfsTemplate} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SxwlRustfsTemplate 测试")
class SxwlRustfsTemplateTest {

    @Mock
    private S3Client s3Client;

    private SxwlRustfsTemplate template;
    private SxwlRustfsProperties properties;

    @BeforeEach
    void setUp() {
        properties = new SxwlRustfsProperties();
        template = new SxwlRustfsTemplate(s3Client, properties);
    }

    @Test
    @DisplayName("upload 应调用 S3Client.putObject")
    void upload_shouldCallS3Put() {
        InputStream inputStream = new ByteArrayInputStream("test-data".getBytes());
        template.upload("bucket", "key", inputStream, 9, "text/plain");
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("download 应返回 InputStream")
    @SuppressWarnings("unchecked")
    void download_shouldReturnStream() {
        ResponseInputStream<GetObjectResponse> responseStream = mock(ResponseInputStream.class);
        when(s3Client.getObject(any(GetObjectRequest.class)))
                .thenReturn(responseStream);
        InputStream result = template.download("bucket", "key");
        assertNotNull(result);
    }

    @Test
    @DisplayName("delete 应调用 S3Client.deleteObject")
    void delete_shouldCallS3Delete() {
        template.delete("bucket", "key");
        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    @DisplayName("doesObjectExist 应返回 true 当对象存在")
    void doesObjectExist_shouldReturnTrue_whenExists() {
        when(s3Client.headObject(any(HeadObjectRequest.class))).thenReturn(null);
        assertTrue(template.doesObjectExist("bucket", "key"));
    }

    @Test
    @DisplayName("doesObjectExist 应返回 false 当对象不存在")
    void doesObjectExist_shouldReturnFalse_whenNotExists() {
        when(s3Client.headObject(any(HeadObjectRequest.class)))
                .thenThrow(NoSuchKeyException.class);
        assertFalse(template.doesObjectExist("bucket", "key"));
    }
}

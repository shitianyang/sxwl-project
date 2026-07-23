package com.sxwl.rustfs.client;

import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.rustfs.config.SxwlRustfsProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * S3 操作封装
 *
 * <p>统一封装 {@link S3Client} 的常用操作，简化上层模块调用。</p>
 *
 * @author shitianyang
 * @since 0.1.0
 */
public class SxwlRustfsTemplate {

    private static final Logger log = LoggerFactory.getLogger(SxwlRustfsTemplate.class);

    private final S3Client s3Client;
    private final SxwlRustfsProperties properties;

    public SxwlRustfsTemplate(S3Client s3Client, SxwlRustfsProperties properties) {
        this.s3Client = s3Client;
        this.properties = properties;
    }

    /**
     * 上传对象
     *
     * @param bucket        桶名
     * @param key           对象键
     * @param inputStream   文件流
     * @param contentLength 内容长度
     * @param contentType   MIME 类型
     */
    public void upload(String bucket, String key, InputStream inputStream, long contentLength, String contentType) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();
        s3Client.putObject(request, RequestBody.fromInputStream(inputStream, contentLength));
        log.debug("S3 upload success: bucket={}, key={}", bucket, key);
    }

    /**
     * 下载对象
     *
     * @param bucket 桶名
     * @param key    对象键
     * @return 文件流
     */
    public InputStream download(String bucket, String key) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        return s3Client.getObject(request);
    }

    /**
     * 删除对象
     *
     * @param bucket 桶名
     * @param key    对象键
     */
    public void delete(String bucket, String key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        s3Client.deleteObject(request);
        log.debug("S3 delete success: bucket={}, key={}", bucket, key);
    }

    /**
     * 按前缀批量删除
     *
     * @param bucket 桶名
     * @param prefix 对象键前缀
     */
    public void deleteByPrefix(String bucket, String prefix) {
        ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(bucket)
                .prefix(prefix)
                .build();
        ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);
        if (listResponse.contents().isEmpty()) {
            return;
        }

        List<ObjectIdentifier> keys = listResponse.contents().stream()
                .map(obj -> ObjectIdentifier.builder().key(obj.key()).build())
                .collect(Collectors.toList());

        DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
                .bucket(bucket)
                .delete(Delete.builder().objects(keys).build())
                .build();
        s3Client.deleteObjects(deleteRequest);
        log.debug("S3 deleteByPrefix success: bucket={}, prefix={}, count={}", bucket, prefix, keys.size());
    }

    /**
     * 生成预签名 URL
     *
     * @param bucket   桶名
     * @param key      对象键
     * @param duration 有效期
     * @return 预签名 URL
     */
    public String generatePresignedUrl(String bucket, String key, Duration duration) {
        return s3Client.utilities().getUrl(
                GetUrlRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build()
        ).toExternalForm();
    }

    /**
     * 列出对象
     *
     * @param bucket 桶名
     * @param prefix 对象键前缀
     * @return 对象列表
     */
    public List<S3Object> listObjects(String bucket, String prefix) {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucket)
                .prefix(prefix)
                .build();
        return s3Client.listObjectsV2(request).contents();
    }

    /**
     * 合并对象（分片合并）
     *
     * <p>将多个源对象按顺序合并为一个目标对象。</p>
     *
     * @param bucket     桶名
     * @param sourceKeys 源对象键列表（按顺序合并）
     * @param destKey    目标对象键
     */
    public void composeObject(String bucket, List<String> sourceKeys, String destKey) {
        List<CompletedPart> completedParts = new ArrayList<>();
        String uploadId = null;

        try {
            // 创建 MultipartUpload
            CreateMultipartUploadRequest createRequest = CreateMultipartUploadRequest.builder()
                    .bucket(bucket)
                    .key(destKey)
                    .build();
            CreateMultipartUploadResponse createResponse = s3Client.createMultipartUpload(createRequest);
            uploadId = createResponse.uploadId();

            // 逐个上传分片（从源对象读取并上传到目标对象的分片）
            for (int i = 0; i < sourceKeys.size(); i++) {
                GetObjectRequest getRequest = GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(sourceKeys.get(i))
                        .build();
                InputStream sourceStream = s3Client.getObject(getRequest);

                UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                        .bucket(bucket)
                        .key(destKey)
                        .uploadId(uploadId)
                        .partNumber(i + 1)
                        .build();

                // 读取源对象全部内容
                byte[] bytes = sourceStream.readAllBytes();
                UploadPartResponse uploadPartResponse = s3Client.uploadPart(
                        uploadPartRequest,
                        RequestBody.fromBytes(bytes));

                completedParts.add(CompletedPart.builder()
                        .partNumber(i + 1)
                        .eTag(uploadPartResponse.eTag())
                        .build());
            }

            // 完成 MultipartUpload
            CompleteMultipartUploadRequest completeRequest = CompleteMultipartUploadRequest.builder()
                    .bucket(bucket)
                    .key(destKey)
                    .uploadId(uploadId)
                    .multipartUpload(CompletedMultipartUpload.builder()
                            .parts(completedParts)
                            .build())
                    .build();
            s3Client.completeMultipartUpload(completeRequest);
            log.debug("S3 composeObject success: bucket={}, destKey={}, parts={}", bucket, destKey, sourceKeys.size());

        } catch (Exception e) {
            // 取消 MultipartUpload
            if (uploadId != null) {
                try {
                    AbortMultipartUploadRequest abortRequest = AbortMultipartUploadRequest.builder()
                            .bucket(bucket)
                            .key(destKey)
                            .uploadId(uploadId)
                            .build();
                    s3Client.abortMultipartUpload(abortRequest);
                } catch (Exception ex) {
                    log.warn("Abort multipart upload failed: bucket={}, key={}, uploadId={}", bucket, destKey, uploadId, ex);
                }
            }
            throw new SxwlBusinessException(500, "S3 composeObject 合并失败", e);
        }
    }

    /**
     * 检查对象是否存在
     *
     * @param bucket 桶名
     * @param key    对象键
     * @return 是否存在
     */
    public boolean doesObjectExist(String bucket, String key) {
        try {
            HeadObjectRequest request = HeadObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            s3Client.headObject(request);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }
}

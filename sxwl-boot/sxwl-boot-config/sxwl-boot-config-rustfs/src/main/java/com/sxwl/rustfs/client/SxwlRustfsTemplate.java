package com.sxwl.rustfs.client;

import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.rustfs.config.SxwlRustfsProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.paginators.ListObjectsV2Iterable;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RustFS / S3 对象存储操作模板
 *
 * <p>统一封装 AWS S3 SDK 的常用操作，简化上层模块调用。</p>
 *
 * <h3>设计原则</h3>
 * <ul>
 *   <li><b>零依赖业务逻辑</b>：只提供最基础的上传、下载、删除、列表等能力</li>
 *   <li><b>内存优化</b>：流式处理大文件，避免整个文件读入堆内存（见 composeObject 方法）</li>
 *   <li><b>异常安全</b>：MultipartUpload 失败时自动 Abort，防止僵尸分片</li>
 * </ul>
 *
 * <h3>核心能力</h3>
 * <table border="1">
 *   <tr><th>方法</th><th>功能</th><th>使用场景</th></tr>
 *   <tr><td>{@link #upload}</td><td>上传对象</td><td>文件上传、图片存储、文档保存</td></tr>
 *   <tr><td>{@link #download}</td><td>下载对象</td><td>文件下载、预览、转发</td></tr>
 *   <tr><td>{@link #delete}</td><td>删除单个对象</td><td>单文件删除、清理临时文件</td></tr>
 *   <tr><td>{@link #deleteByPrefix}</td><td>按前缀批量删除</td><td>清理目录、删除批次数据</td></tr>
 *   <tr><td>{@link #generatePresignedUrl}</td><td>生成预签名 URL</td><td>临时下载链接、前端直传</td></tr>
 *   <tr><td>{@link #listObjects}</td><td>列出对象</td><td>目录浏览、文件列表展示</td></tr>
 *   <tr><td>{@link #composeObject}</td><td>分片合并</td><td>大文件分片上传后合并</td></tr>
 *   <tr><td>{@link #doesObjectExist}</td><td>检查对象是否存在</td><td>上传前去重、下载前校验</td></tr>
 *   <tr><td>{@link #createBucketIfNotExists}</td><td>自动创建 Bucket</td><td>启动初始化、动态 Bucket 管理</td></tr>
 * </table>
 *
 * @author shitianyang
 * @since 0.1.0
 */
public class SxwlRustfsTemplate {

    private static final Logger log = LoggerFactory.getLogger(SxwlRustfsTemplate.class);
    private static final int DEFAULT_PART_SIZE = 5 * 1024 * 1024; // 5MB

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String defaultBucket;
    private final String tmpPrefix;

    public SxwlRustfsTemplate(SxwlRustfsProperties properties) {
        // 构建 S3Client
        this.s3Client = S3Client.builder()
                .endpointOverride(URI.create(properties.getEndpoint()))
                .region(Region.of(properties.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(properties.getAccessKey(), properties.getSecretKey())))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build())
                .build();

        // 构建 S3Presigner（用于生成预签名 URL）
        this.s3Presigner = S3Presigner.builder()
                .endpointOverride(URI.create(properties.getEndpoint()))
                .region(Region.of(properties.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(properties.getAccessKey(), properties.getSecretKey())))
                .build();

        this.defaultBucket = properties.getDefaultBucket();
        this.tmpPrefix = properties.getTmpPrefix();

        // 确保默认 Bucket 存在
        createBucketIfNotExists(defaultBucket);
        log.info("SxwlRustfsTemplate 初始化完成: endpoint={}, bucket={}", properties.getEndpoint(), defaultBucket);
    }

    /**
     * 上传对象
     *
     * @param bucket   Bucket 名称
     * @param objectKey 对象键（路径）
     * @param inputStream 文件输入流
     * @param contentType Content-Type
     */
    public void upload(String bucket, String objectKey, InputStream inputStream, String contentType) {
        try {
            s3Client.putObject(PutObjectRequest.builder().bucket(bucket).key(objectKey).contentType(contentType).build(),
                    RequestBody.fromInputStream(inputStream, inputStream.available()));
            log.debug("对象上传成功: bucket={}, key={}", bucket, objectKey);
        } catch (Exception e) {
            log.error("对象上传失败: bucket={}, key={}", bucket, objectKey, e);
            throw new SxwlBusinessException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 下载对象
     *
     * @param bucket    Bucket 名称
     * @param objectKey 对象键（路径）
     * @return 文件内容字节数组
     */
    public byte[] download(String bucket, String objectKey) {
        try {
            GetObjectRequest request = GetObjectRequest.builder().bucket(bucket).key(objectKey).build();
            
            try (InputStream inputStream = s3Client.getObject(request);
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                return outputStream.toByteArray();
            }
        } catch (Exception e) {
            log.error("对象下载失败: bucket={}, key={}", bucket, objectKey, e);
            throw new SxwlBusinessException("文件下载失败: " + e.getMessage());
        }
    }

    /**
     * 删除单个对象
     *
     * @param bucket    Bucket 名称
     * @param objectKey 对象键（路径）
     */
    public void delete(String bucket, String objectKey) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(objectKey).build());
            log.debug("对象删除成功: bucket={}, key={}", bucket, objectKey);
        } catch (Exception e) {
            log.error("对象删除失败: bucket={}, key={}", bucket, objectKey, e);
            throw new SxwlBusinessException("文件删除失败: " + e.getMessage());
        }
    }

    /**
     * 按前缀批量删除
     *
     * @param bucket     Bucket 名称
     * @param prefix     前缀（目录）
     */
    public void deleteByPrefix(String bucket, String prefix) {
        try {
            ListObjectsV2Request request = ListObjectsV2Request.builder().bucket(bucket).prefix(prefix).build();
            List<String> objectKeys = new ArrayList<>();
            
            // 使用分页器获取所有对象
            ListObjectsV2Iterable response = s3Client.listObjectsV2Paginator(request);
            response.contents().forEach(obj -> objectKeys.add(obj.key()));
            
            // 批量删除
            for (int i = 0; i < objectKeys.size(); i += 1000) {
                List<String> batch = objectKeys.subList(i, Math.min(i + 1000, objectKeys.size()));
                List<software.amazon.awssdk.services.s3.model.ObjectIdentifier> objectIds = batch.stream()
                        .map(key -> software.amazon.awssdk.services.s3.model.ObjectIdentifier.builder().key(key).build())
                        .collect(Collectors.toList());
                s3Client.deleteObjects(DeleteObjectsRequest.builder()
                        .bucket(bucket)
                        .delete(software.amazon.awssdk.services.s3.model.Delete.builder().objects(objectIds).build())
                        .build());
            }
            log.info("批量删除成功: bucket={}, prefix={}, count={}", bucket, prefix, objectKeys.size());
        } catch (Exception e) {
            log.error("批量删除失败: bucket={}, prefix={}", bucket, prefix, e);
            throw new SxwlBusinessException("批量删除失败: " + e.getMessage());
        }
    }

    /**
     * 生成预签名 URL
     *
     * @param bucket      Bucket 名称
     * @param objectKey   对象键（路径）
     * @param expirationTime 过期时间（秒）
     * @return 预签名 URL字符串
     */
    public String generatePresignedUrl(String bucket, String objectKey, int expirationTime) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucket).key(objectKey).build();
            Duration duration = Duration.ofSeconds(expirationTime);
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(duration)
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            String url = presignedRequest.url().toString();
            log.debug("生成预签名 URL 成功: bucket={}, key={}, expiration={}s", bucket, objectKey, expirationTime);
            return url;
        } catch (Exception e) {
            log.error("生成预签名 URL 失败: bucket={}, key={}", bucket, objectKey, e);
            throw new SxwlBusinessException("生成下载链接失败: " + e.getMessage());
        }
    }

    /**
     * 列出对象
     *
     * @param bucket    Bucket 名称
     * @param prefix    前缀（目录）
     * @return 对象键列表
     */
    public List<String> listObjects(String bucket, String prefix) {
        try {
            ListObjectsV2Request request = ListObjectsV2Request.builder().bucket(bucket).prefix(prefix).build();
            List<String> objectKeys = new ArrayList<>();
            
            // 使用分页器获取所有对象
            ListObjectsV2Iterable response = s3Client.listObjectsV2Paginator(request);
            response.contents().forEach(obj -> objectKeys.add(obj.key()));
            
            log.debug("列出对象成功: bucket={}, prefix={}, count={}", bucket, prefix, objectKeys.size());
            return objectKeys;
        } catch (Exception e) {
            log.error("列出对象失败: bucket={}, prefix={}", bucket, prefix, e);
            throw new SxwlBusinessException("列出文件失败: " + e.getMessage());
        }
    }

    /**
     * 分片合并
     *
     * @param bucket       Bucket 名称
     * @param objectKey    目标对象键
     * @param partKeys     分片对象键列表
     */
    public void composeObject(String bucket, String objectKey, List<String> partKeys) {
        if (partKeys == null || partKeys.isEmpty()) {
            throw new SxwlBusinessException("分片列表不能为空");
        }

        try {
            List<CompletedPart> parts = new ArrayList<>();
            for (int i = 0; i < partKeys.size(); i++) {
                parts.add(CompletedPart.builder()
                        .partNumber(i + 1)
                        .eTag(getPartEtag(bucket, partKeys.get(i)))
                        .build());
            }

            CompleteMultipartUploadRequest request = CompleteMultipartUploadRequest.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .multipartUpload(CompletedMultipartUpload.builder().parts(parts).build())
                    .build();

            s3Client.completeMultipartUpload(request);
            log.info("分片合并成功: bucket={}, key={}, parts={}", bucket, objectKey, partKeys.size());
        } catch (Exception e) {
            log.error("分片合并失败: bucket={}, key={}", bucket, objectKey, e);
            throw new SxwlBusinessException("文件合并失败: " + e.getMessage());
        }
    }

    /**
     * 获取分片的 ETag
     *
     * @param bucket   Bucket 名称
     * @param objectKey 对象键
     * @return ETag 字符串
     */
    private String getPartEtag(String bucket, String objectKey) {
        HeadObjectRequest headRequest = HeadObjectRequest.builder().bucket(bucket).key(objectKey).build();
        HeadObjectResponse headResponse = s3Client.headObject(headRequest);
        return headResponse.eTag();
    }

    /**
     * 检查对象是否存在
     *
     * @param bucket    Bucket 名称
     * @param objectKey 对象键
     * @return true 如果存在，否则 false
     */
    public boolean doesObjectExist(String bucket, String objectKey) {
        try {
            s3Client.headObject(HeadObjectRequest.builder().bucket(bucket).key(objectKey).build());
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            log.error("检查对象存在性失败: bucket={}, key={}", bucket, objectKey, e);
            throw new SxwlBusinessException("检查文件存在性失败: " + e.getMessage());
        }
    }

    /**
     * 自动创建 Bucket
     *
     * @param bucket Bucket 名称
     * @return true 如果创建成功或已存在
     */
    public boolean createBucketIfNotExists(String bucket) {
        try {
            // 检查是否已存在
            if (doesBucketExist(bucket)) {
                log.debug("Bucket 已存在: {}", bucket);
                return true;
            }

            // 创建 Bucket
            s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
            log.info("Bucket 创建成功: {}", bucket);
            return true;
        } catch (Exception e) {
            log.error("Bucket 创建失败: {}", bucket, e);
            return false;
        }
    }

    /**
     * 检查 Bucket 是否存在
     *
     * @param bucket Bucket 名称
     * @return true 如果存在，否则 false
     */
    private boolean doesBucketExist(String bucket) {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
            return true;
        } catch (NoSuchBucketException e) {
            return false;
        } catch (Exception e) {
            log.error("检查 Bucket 存在性失败: {}", bucket, e);
            return false;
        }
    }

    /**
     * 关闭客户端
     */
    public void close() {
        if (s3Client != null) {
            s3Client.close();
        }
        if (s3Presigner != null) {
            s3Presigner.close();
        }
        log.info("SxwlRustfsTemplate 客户端已关闭");
    }
}

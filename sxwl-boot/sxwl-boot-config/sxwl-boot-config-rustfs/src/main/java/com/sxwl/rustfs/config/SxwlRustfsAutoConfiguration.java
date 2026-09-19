package com.sxwl.rustfs.config;

import com.sxwl.rustfs.client.SxwlRustfsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.InitializingBean;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

/**
 * RustFS 自动装配配置
 *
 * <p>提供开箱即用的对象存储基础设施，仅需要配置 {@code endpoint}、{@code access-key}、{@code secret-key}。</p>
 *
 * <h3>设计原则</h3>
 * <ul>
 *   <li><b>最小化配置</b>：仅暴露必需的连接参数，其他默认值硬编码在 Java 类中</li>
 *   <li><b>零业务逻辑</b>：只提供 {@code S3Client} 和 {@code SxwlRustfsTemplate} Bean，不处理任何业务</li>
 *   <li><b>启动安全检查</b>：初始化时自动创建默认 Bucket，失败时 warn 不阻塞</li>
 * </ul>
 *
 * <h3>默认值</h3>
 * <table border="1">
 *   <tr><th>功能</th><th>默认值</th><th>说明</th></tr>
 *   <tr><td>区域</td><td>{@code us-east-1}</td><td>S3 区域</td></tr>
 *   <tr><td>默认桶名</td><td>{@code sxwl-files}</td><td>自动创建的默认 Bucket</td></tr>
 *   <tr><td>预签名 URL 有效期</td><td>{@code 3600 秒 (1小时)}</td><td>{@link SxwlRustfsTemplate#generatePresignedUrl}</td></tr>
 *   <tr><td>分片临时前缀</td><td>{@code tmp/}</td><td>临时对象存储路径前缀</td></tr>
 * </table>
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * @RestController
 * @RequestMapping("/sys/file")
 * public class SysFileController {
 *     
 *     @Autowired private SxwlRustfsTemplate rustfsTemplate;
 *     
 *     @PostMapping("/upload")
 *     public Result upload(MultipartFile file) {
 *         // 1. 上传文件
 *         try (InputStream is = file.getInputStream()) {
 *             String key = "uploads/" + file.getOriginalFilename();
 *             rustfsTemplate.upload("sxwl-files", key, is, file.getSize(), file.getContentType());
 *         }
 *         
 *         // 2. 生成预签名 URL（7 天有效）
 *         String url = rustfsTemplate.generatePresignedUrl("sxwl-files", key, Duration.ofDays(7));
 *         return Result.success(url);
 *     }
 *     
 *     @GetMapping("/download/{key}")
 *     public Result download(@PathVariable String key) {
 *         // 生成预签名 URL（5 分钟有效）
 *         String url = rustfsTemplate.generatePresignedUrl("sxwl-files", key, Duration.ofMinutes(5));
 *         return Result.success(url);
 *     }
 * }
 * }</pre>
 *
 * <h3>安全提示</h3>
 * <ul>
 *   <li><b>预签名 URL 安全性</b>：预签名 URL 包含签名信息，过期后自动失效，不会被重复利用</li>
 *   <li><b>密钥保护</b>：access-key 和 secret-key 不要提交到 Git，使用环境变量或密钥管理工具</li>
 *   <li><b>Bucket 权限</b>：建议为每个业务场景创建独立的 Bucket，避免权限混淆</li>
 * </ul>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@AutoConfiguration
@EnableConfigurationProperties(SxwlRustfsProperties.class)
public class SxwlRustfsAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(SxwlRustfsAutoConfiguration.class);

    /**
     * 默认桶名（硬编码）
     */
    private static final String DEFAULT_BUCKET = "sxwl-files";

    /**
     * 预签名 URL 默认有效期（1 小时）
     */
    private static final long DEFAULT_PRESIGNED_URL_EXPIRE = 3600;

    /**
     * 分片临时对象前缀（硬编码）
     */
    private static final String TEMP_PATH_PREFIX = "tmp/";

    /**
     * S3 客户端（线程安全，全局单例）
     *
     * <p>配置 {@code forcePathStyle} 兼容 MinIO/RustFS 等 S3 兼容服务。</p>
     */
    @Bean
    @ConditionalOnMissingBean
    public S3Client s3Client(SxwlRustfsProperties properties) {
        return S3Client.builder()
                .region(Region.of("us-east-1"))
                .endpointOverride(URI.create(properties.getEndpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(properties.getAccessKey(), properties.getSecretKey())))
                .forcePathStyle(true)
                .build();
    }

    /**
     * RustFS 操作模板
     *
     * <p>封装所有 S3 操作，提供简洁的 API 供业务模块调用。</p>
     *
     * <h3>核心能力</h3>
     * <ul>
     *   <li><b>文件操作</b>：上传、下载、删除、按前缀批量删除</li>
     *   <li><b>列表查询</b>：按前缀列出所有对象</li>
     *   <li><b>预签名 URL</b>：生成临时访问链接（可配置有效期）</li>
     *   <li><b>分片合并</b>：将多个源对象按顺序合并为目标对象</li>
     *   <li><b>Bucket 管理</b>：自动创建 Bucket（如果不存在）</li>
     * </ul>
     */
    @Bean
    @ConditionalOnMissingBean
    public SxwlRustfsTemplate sxwlRustfsTemplate(SxwlRustfsProperties properties) {
        return new SxwlRustfsTemplate(properties);
    }

    /**
     * 确保默认 Bucket 存在（启动后执行，RustFS 未就绪时仅 warn 不阻塞）
     *
     * <p>该方法在 {@code sxwlRustfsTemplate} Bean 初始化后自动触发。</p>
     */
    @Bean
    @ConditionalOnMissingBean
    public DefaultBucketInitializer defaultBucketInitializer(SxwlRustfsProperties properties) {
        return new DefaultBucketInitializer(properties, DEFAULT_BUCKET);
    }

    /**
     * 默认 Bucket 初始化的辅助类
     *
     * <p>实现 {@link InitializingBean}，在 Spring Bean 初始化后执行 Bucket 检查创建。</p>
     */
    public static class DefaultBucketInitializer implements InitializingBean {
        private final SxwlRustfsProperties properties;
        private final String bucketName;

        public DefaultBucketInitializer(SxwlRustfsProperties properties, String bucketName) {
            this.properties = properties;
            this.bucketName = bucketName;
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            SxwlRustfsTemplate template = new SxwlRustfsTemplate(properties);
            try {
                template.createBucketIfNotExists(bucketName);
                log.info("默认 Bucket 初始化成功: {}", bucketName);
            } catch (Exception e) {
                log.warn("默认 Bucket 初始化失败（RustFS 可能未启动）: bucket={}, error={}",
                        bucketName, e.getMessage());
            }
        }
    }
}

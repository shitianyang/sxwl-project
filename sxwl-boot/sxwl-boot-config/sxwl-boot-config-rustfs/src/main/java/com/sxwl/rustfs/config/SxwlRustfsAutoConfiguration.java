package com.sxwl.rustfs.config;

import com.sxwl.rustfs.client.SxwlRustfsTemplate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

/**
 * RustFS 自动装配
 *
 * <p>注册 {@link S3Client} 和 {@link SxwlRustfsTemplate} 到 Spring 容器。</p>
 *
 * @author shitianyang
 * @since 0.1.0
 */
@AutoConfiguration
@EnableConfigurationProperties(SxwlRustfsProperties.class)
public class SxwlRustfsAutoConfiguration {

    /**
     * S3 客户端（线程安全，全局单例）
     */
    @Bean
    @ConditionalOnMissingBean
    public S3Client s3Client(SxwlRustfsProperties properties) {
        return S3Client.builder()
                .region(Region.of(properties.getRegion()))
                .endpointOverride(URI.create(properties.getEndpoint()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(properties.getAccessKey(), properties.getSecretKey())))
                .forcePathStyle(true)
                .build();
    }

    /**
     * RustFS 操作模板
     */
    @Bean
    @ConditionalOnMissingBean
    public SxwlRustfsTemplate sxwlRustfsTemplate(S3Client s3Client, SxwlRustfsProperties properties) {
        return new SxwlRustfsTemplate(s3Client, properties);
    }
}

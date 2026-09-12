package com.sxwl.rustfs.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SxwlRustfsProperties} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("SxwlRustfsProperties 测试")
class SxwlRustfsPropertiesTest {

    @Test
    @DisplayName("默认值应正确")
    void defaultValues_shouldBeCorrect() {
        SxwlRustfsProperties props = new SxwlRustfsProperties();
        assertEquals("us-east-1", props.getRegion());
        assertEquals("sxwl-files", props.getDefaultBucket());
        assertEquals(3600L, props.getPresignedUrlExpire());
        assertEquals("tmp/", props.getTempPathPrefix());
    }

    @Test
    @DisplayName("setter/getter 应正常读写")
    void testGetterSetter() {
        SxwlRustfsProperties props = new SxwlRustfsProperties();
        props.setEndpoint("http://localhost:9000");
        props.setRegion("cn-north-1");
        props.setAccessKey("AKID");
        props.setSecretKey("secret");
        props.setDefaultBucket("my-bucket");
        props.setPresignedUrlExpire(7200L);
        props.setTempPathPrefix("temp/");

        assertEquals("http://localhost:9000", props.getEndpoint());
        assertEquals("cn-north-1", props.getRegion());
        assertEquals("AKID", props.getAccessKey());
        assertEquals("secret", props.getSecretKey());
        assertEquals("my-bucket", props.getDefaultBucket());
        assertEquals(7200L, props.getPresignedUrlExpire());
        assertEquals("temp/", props.getTempPathPrefix());
    }
}

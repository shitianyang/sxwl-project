package com.sxwl.common.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SM3 摘要工具类单元测试
 *
 * @author shitianyang
 * @since 0.1.0
 */
@DisplayName("SM3Utils 工具类测试")
class SM3UtilsTest {

    @Test
    @DisplayName("SM3 摘要：对同一输入两次摘要结果一致")
    void testDigestConsistency() {
        String content = "Hello SM3 测试";
        String digest1 = SM3Utils.digestHex(content);
        String digest2 = SM3Utils.digestHex(content);
        assertEquals(digest1, digest2, "同一输入多次摘要应返回相同结果");
    }

    @Test
    @DisplayName("SM3 摘要：不同输入摘要结果不同")
    void testDigestDifferent() {
        String digest1 = SM3Utils.digestHex("data1");
        String digest2 = SM3Utils.digestHex("data2");
        assertNotEquals(digest1, digest2, "不同输入摘要结果应不同");
    }

    @Test
    @DisplayName("SM3 摘要：返回 64 位十六进制字符串")
    void testDigestLength() {
        String digest = SM3Utils.digestHex("任意内容");
        assertEquals(64, digest.length(), "SM3 摘要应为 64 位十六进制");
    }

    @Test
    @DisplayName("SM3 摘要：字节数组方式与字符串方式结果一致")
    void testDigestBytes() {
        String content = "字节数组摘要测试";
        byte[] data = content.getBytes(StandardCharsets.UTF_8);
        String digestViaBytes = SM3Utils.digestHex(data);
        String digestViaString = SM3Utils.digestHex(content);
        assertEquals(digestViaString, digestViaBytes, "字节数组方式与字符串方式应一致");
    }

    @Test
    @DisplayName("SM3 校验：正确摘要返回 true")
    void testVerifySuccess() {
        String content = "校验测试内容";
        String digest = SM3Utils.digestHex(content);
        assertTrue(SM3Utils.verify(content, digest), "正确摘要应校验通过");
    }

    @Test
    @DisplayName("SM3 校验：错误摘要返回 false")
    void testVerifyFail() {
        assertFalse(SM3Utils.verify("content", "0000000000000000000000000000000000000000000000000000000000000000"),
                "错误摘要应校验失败");
    }

    @Test
    @DisplayName("SM3 摘要：空字符串边界测试")
    void testDigestEmptyString() {
        String digest = SM3Utils.digestHex("");
        assertNotNull(digest);
        assertEquals(64, digest.length(), "空字符串摘要也应为 64 位");
    }
}

package com.sxwl.common.utils;

import com.sxwl.common.exception.SxwlBusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SM4 对称加密工具类单元测试
 *
 * @author shitianyang
 * @since 0.1.0
 */
@DisplayName("SM4Utils 工具类测试")
class SM4UtilsTest {

    @Test
    @DisplayName("SM4 密钥生成：返回 16 字节")
    void testGenerateKey() {
        byte[] key = SM4Utils.generateKey();
        assertNotNull(key);
        assertEquals(16, key.length, "SM4 密钥应为 16 字节");
    }

    @Test
    @DisplayName("SM4 密钥生成（十六进制）：返回 32 位十六进制字符串")
    void testGenerateKeyHex() {
        String keyHex = SM4Utils.generateKeyHex();
        assertNotNull(keyHex);
        assertEquals(32, keyHex.length(), "十六进制密钥应为 32 位");
    }

    @Test
    @DisplayName("SM4 IV 生成：返回 16 字节")
    void testGenerateIv() {
        byte[] iv = SM4Utils.generateIv();
        assertNotNull(iv);
        assertEquals(16, iv.length, "IV 应为 16 字节");
    }

    @Test
    @DisplayName("SM4-ECB 加解密：加密后解密与原文一致")
    void testEcbEncryptDecrypt() {
        byte[] key = SM4Utils.generateKey();
        String plainText = "SM4 ECB加解密测试数据！@#$%";

        String cipherBase64 = SM4Utils.encryptEcbToBase64(plainText, SM4Utils.generateKeyHex());
        // 使用相同的密钥
        String keyHex = SM4Utils.generateKeyHex();
        cipherBase64 = SM4Utils.encryptEcbToBase64(plainText, keyHex);
        assertNotNull(cipherBase64);

        String decryptedText = SM4Utils.decryptEcbFromBase64(cipherBase64, keyHex);
        assertEquals(plainText, decryptedText, "ECB 解密后应与原文一致");
    }

    @Test
    @DisplayName("SM4-CBC 加解密：加密后解密与原文一致")
    void testCbcEncryptDecrypt() {
        String keyHex = SM4Utils.generateKeyHex();
        String ivHex = SM4Utils.generateIvHex();
        String plainText = "SM4 CBC加解密测试数据！@#$%";

        String cipherBase64 = SM4Utils.encryptCbcToBase64(plainText, keyHex, ivHex);
        assertNotNull(cipherBase64);
        assertNotEquals(plainText, cipherBase64, "密文不应等于明文");

        String decryptedText = SM4Utils.decryptCbcFromBase64(cipherBase64, keyHex, ivHex);
        assertEquals(plainText, decryptedText, "CBC 解密后应与原文一致");
    }

    @Test
    @DisplayName("SM4-ECB 加解密：空字符串边界测试")
    void testEcbEmptyString() {
        String keyHex = SM4Utils.generateKeyHex();
        String plainText = "";

        String cipherBase64 = SM4Utils.encryptEcbToBase64(plainText, keyHex);
        String decryptedText = SM4Utils.decryptEcbFromBase64(cipherBase64, keyHex);
        assertEquals(plainText, decryptedText, "ECB 空字符串加解密应一致");
    }

    @Test
    @DisplayName("SM4-ECB：无效密钥长度应抛出异常")
    void testEcbInvalidKeyLength() {
        assertThrows(SxwlBusinessException.class,
                () -> SM4Utils.encryptEcbToBase64("hello", "aabb"),
                "无效密钥长度应抛出异常");
    }

    @Test
    @DisplayName("SM4 同一明文在不同 IV 下 CBC 加密结果不同")
    void testCbcDifferentIv() {
        String keyHex = SM4Utils.generateKeyHex();
        String iv1 = SM4Utils.generateIvHex();
        String iv2 = SM4Utils.generateIvHex();
        String plainText = "相同原文";

        String cipher1 = SM4Utils.encryptCbcToBase64(plainText, keyHex, iv1);
        String cipher2 = SM4Utils.encryptCbcToBase64(plainText, keyHex, iv2);
        assertNotEquals(cipher1, cipher2, "不同 IV 下 CBC 加密结果应不同");
    }
}

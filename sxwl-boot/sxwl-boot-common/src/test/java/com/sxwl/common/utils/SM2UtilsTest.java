package com.sxwl.common.utils;

import com.sxwl.common.exception.SxwlBusinessException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SM2 非对称加密工具类单元测试
 *
 * @author shitianyang
 * @since 0.1.0
 */
@DisplayName("SM2Utils 工具类测试")
class SM2UtilsTest {

    @BeforeAll
    static void setUp() {
        // BouncyCastleRegistrar 在 SM2Utils 静态块中自动注册，确保已初始化
        SM2Utils.generateKeyPair();
    }

    @Test
    @DisplayName("生成密钥对：返回非空且公钥私钥不为 null")
    void testGenerateKeyPair() {
        SM2Utils.SM2KeyPair keyPair = SM2Utils.generateKeyPairHex();
        assertNotNull(keyPair);
        assertNotNull(keyPair.getPublicKeyHex(), "公钥不能为空");
        assertNotNull(keyPair.getPrivateKeyHex(), "私钥不能为空");
        assertTrue(keyPair.getPublicKeyHex().length() > 0, "公钥长度大于0");
        assertTrue(keyPair.getPrivateKeyHex().length() > 0, "私钥长度大于0");
    }

    @Test
    @DisplayName("SM2 加密-解密：加密后密文不为空，解密后与原文一致")
    void testEncryptDecrypt() {
        SM2Utils.SM2KeyPair keyPair = SM2Utils.generateKeyPairHex();
        String plainText = "Hello SM2 国密加密测试数据！@#$%";

        // 加密
        String cipherBase64 = SM2Utils.encryptToBase64(plainText, keyPair.getPublicKeyHex());
        assertNotNull(cipherBase64, "密文不能为空");
        assertNotEquals(plainText, cipherBase64, "密文不应等于明文");

        // 解密
        String decryptedText = SM2Utils.decryptFromBase64(cipherBase64, keyPair.getPrivateKeyHex());
        assertEquals(plainText, decryptedText, "解密后应与原文一致");
    }

    @Test
    @DisplayName("SM2 加密空字符串应抛出异常（SM2 不支持加密空数据）")
    void testEncryptEmptyString() {
        SM2Utils.SM2KeyPair keyPair = SM2Utils.generateKeyPairHex();
        assertThrows(com.sxwl.common.exception.SxwlBusinessException.class,
                () -> SM2Utils.encryptToBase64("", keyPair.getPublicKeyHex()));
    }

    @Test
    @DisplayName("SM2 签名-验签：签名通过验证")
    void testSignVerify() {
        SM2Utils.SM2KeyPair keyPair = SM2Utils.generateKeyPairHex();
        String content = "待签名数据内容";

        // 签名
        String signHex = SM2Utils.signToHex(content, keyPair.getPrivateKeyHex());
        assertNotNull(signHex, "签名不能为空");

        // 验签
        boolean verified = SM2Utils.verifyHexSign(content, signHex, keyPair.getPublicKeyHex());
        assertTrue(verified, "验签应通过");
    }

    @Test
    @DisplayName("SM2 签名-验签：篡改数据后验签应失败")
    void testSignVerifyTampered() {
        SM2Utils.SM2KeyPair keyPair = SM2Utils.generateKeyPairHex();
        String content = "原始数据";
        String signHex = SM2Utils.signToHex(content, keyPair.getPrivateKeyHex());

        // 篡改数据
        boolean verified = SM2Utils.verifyHexSign("篡改数据", signHex, keyPair.getPublicKeyHex());
        assertFalse(verified, "篡改后验签应失败");
    }

    @Test
    @DisplayName("SM2 密钥对导出：toString 不暴露私钥原文")
    void testKeyPairToString() {
        SM2Utils.SM2KeyPair keyPair = SM2Utils.generateKeyPairHex();
        String toString = keyPair.toString();
        assertTrue(toString.contains("****"), "toString 应隐藏私钥（包含 ****）");
        assertFalse(toString.contains(keyPair.getPrivateKeyHex()), "toString 不应包含完整的私钥原文");
    }

    @Test
    @DisplayName("从私钥推导公钥：推导出的公钥与 KeyPair 中的公钥一致")
    void testDerivePublicKey() {
        SM2Utils.SM2KeyPair keyPair = SM2Utils.generateKeyPairHex();
        String derivedPublicKey = SM2Utils.deriveRawPublicKeyHex(keyPair.getPrivateKeyHex());
        String rawPublicKey = SM2Utils.toRawPublicKeyHex(keyPair.getPublicKeyHex());
        assertEquals(rawPublicKey, derivedPublicKey, "从私钥推导的裸公钥应与 KeyPair 中的裸公钥一致");
    }

    @Test
    @DisplayName("SM2 加密：无效公钥应抛出异常")
    void testEncryptWithInvalidPublicKey() {
        assertThrows(SxwlBusinessException.class,
                () -> SM2Utils.encryptToBase64("hello", "invalid_hex"),
                "无效公钥应抛出 SxwlBusinessException");
    }

    @Test
    @DisplayName("SM2 解密：无效私钥应抛出异常")
    void testDecryptWithInvalidPrivateKey() {
        byte[] cipherData = "some_cipher_data".getBytes(StandardCharsets.UTF_8);
        assertThrows(SxwlBusinessException.class,
                () -> SM2Utils.decrypt(cipherData, "invalid_hex"),
                "无效私钥应抛出 SxwlBusinessException");
    }
}

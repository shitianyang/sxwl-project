package com.sxwl.common.utils;

import com.sxwl.common.exception.SxwlBusinessException;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;

/**
 * JWT Secret 管理器
 * <p>
 * 负责 JWT 签名密钥的生成、SM4 加解密、SM2 加解密。
 * JWT 本身的生成/解析/校验由 {@link SxwlJwtUtils} 负责。
 * </p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public final class SxwlJwtSecretManager {

    /**
     * secret 生成时使用的随机字节长度
     */
    private static final int SECRET_RANDOM_BYTE_LENGTH = 32;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static final HexFormat HEX = HexFormat.of();

    static {
        BouncyCastleRegistrar.ensureRegistered();
    }

    private SxwlJwtSecretManager() {
        throw new UnsupportedOperationException("SxwlJwtSecretManager 工具类，不允许实例化");
    }

    /**
     * 使用 SM4 随机种子 + SM3 摘要生成 JWT secret
     * <p>
     * 结果为 64 位十六进制字符串（32 字节），可直接用于 HS256 签名。
     * </p>
     *
     * @return 十六进制 secret
     */
    public static String generateSecret() {
        byte[] randomBytes = new byte[SECRET_RANDOM_BYTE_LENGTH];
        SECURE_RANDOM.nextBytes(randomBytes);
        String seed = SM4Utils.generateKeyHex()
                + SM4Utils.generateIvHex()
                + HEX.formatHex(randomBytes)
                + Instant.now().toEpochMilli();
        return SM3Utils.digestHex(seed);
    }

    /**
     * 使用 SM4 对 JWT secret 加密（Base64 输出）
     *
     * @param secret   明文 secret
     * @param sm4KeyHex SM4 密钥（十六进制）
     * @return Base64 密文
     */
    public static String encryptSecretBySm4(String secret, String sm4KeyHex) {
        return SM4Utils.encryptEcbToBase64(requireText(secret, "secret 不能为空"), requireText(sm4KeyHex, "sm4KeyHex 不能为空"));
    }

    /**
     * 使用 SM4 解密 JWT secret（Base64 输入）
     *
     * @param encryptedSecretBase64 Base64 密文
     * @param sm4KeyHex             SM4 密钥（十六进制）
     * @return 明文 secret
     */
    public static String decryptSecretBySm4(String encryptedSecretBase64, String sm4KeyHex) {
        return SM4Utils.decryptEcbFromBase64(
                requireText(encryptedSecretBase64, "encryptedSecretBase64 不能为空"),
                requireText(sm4KeyHex, "sm4KeyHex 不能为空")
        );
    }

    /**
     * 使用 SM2 公钥加密 JWT secret（Base64 输出）
     *
     * @param secret       明文 secret
     * @param publicKeyHex SM2 公钥（十六进制，X.509 编码）
     * @return Base64 密文
     */
    public static String encryptSecretBySm2(String secret, String publicKeyHex) {
        return SM2Utils.encryptToBase64(requireText(secret, "secret 不能为空"), requireText(publicKeyHex, "publicKeyHex 不能为空"));
    }

    /**
     * 使用 SM2 私钥解密 JWT secret（Base64 输入）
     *
     * @param encryptedSecretBase64 Base64 密文
     * @param privateKeyHex         SM2 私钥（十六进制，PKCS#8 编码）
     * @return 明文 secret
     */
    public static String decryptSecretBySm2(String encryptedSecretBase64, String privateKeyHex) {
        return SM2Utils.decryptFromBase64(
                requireText(encryptedSecretBase64, "encryptedSecretBase64 不能为空"),
                requireText(privateKeyHex, "privateKeyHex 不能为空")
        );
    }

    private static String requireText(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new SxwlBusinessException(400, message);
        }
        return value.trim();
    }
}

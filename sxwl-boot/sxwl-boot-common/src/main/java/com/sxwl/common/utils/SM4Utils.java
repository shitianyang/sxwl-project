package com.sxwl.common.utils;

import com.sxwl.common.exception.SxwlBusinessException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Objects;

/**
 * SM4 对称加密工具类
 * <p>
 * 提供常见的 ECB / CBC 模式加解密能力。
 * </p>
 *
 * @author shitianyang
 * @date 2026/6/13
 */
public final class SM4Utils {

    /**
     * SM4 密钥长度（字节）
     */
    private static final int SM4_KEY_LENGTH = 16;

    /**
     * 十六进制编解码工具（小写）
     */
    private static final HexFormat HEX = HexFormat.of();

    static {
        BouncyCastleRegistrar.ensureRegistered();
    }

    /**
     * 工具类不允许实例化
     */
    private SM4Utils() {
        throw new UnsupportedOperationException("SM4Utils 工具类，不允许实例化");
    }

    /**
     * 生成随机 SM4 密钥
     *
     * @return 16 字节密钥
     */
    public static byte[] generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("SM4", BouncyCastleProvider.PROVIDER_NAME);
            keyGenerator.init(128, new SecureRandom());
            return keyGenerator.generateKey().getEncoded();
        } catch (Exception e) {
            throw new SxwlBusinessException(500, "SM4 密钥生成失败", e);
        }
    }

    /**
     * 生成随机 SM4 密钥（十六进制）
     *
     * @return 十六进制密钥
     */
    public static String generateKeyHex() {
        return HEX.formatHex(generateKey());
    }

    /**
     * 生成随机 IV（16 字节）
     *
     * @return 16 字节 IV
     */
    public static byte[] generateIv() {
        byte[] iv = new byte[SM4_KEY_LENGTH];
        new SecureRandom().nextBytes(iv);
        return iv;
    }

    /**
     * 生成随机 IV（十六进制）
     *
     * @return 十六进制 IV
     */
    public static String generateIvHex() {
        return HEX.formatHex(generateIv());
    }

    /**
     * SM4-ECB-PKCS5Padding 加密
     *
     * @param plainData 明文字节
     * @param key       16 字节密钥
     * @return 密文字节
     */
    public static byte[] encryptEcb(byte[] plainData, byte[] key) {
        return doFinal("SM4/ECB/PKCS5Padding", Cipher.ENCRYPT_MODE, plainData, key, null);
    }

    /**
     * SM4-ECB-PKCS5Padding 解密
     *
     * @param cipherData 密文字节
     * @param key        16 字节密钥
     * @return 明文字节
     */
    public static byte[] decryptEcb(byte[] cipherData, byte[] key) {
        return doFinal("SM4/ECB/PKCS5Padding", Cipher.DECRYPT_MODE, cipherData, key, null);
    }

    /**
     * SM4-CBC-PKCS5Padding 加密
     *
     * @param plainData 明文字节
     * @param key       16 字节密钥
     * @param iv        16 字节 IV
     * @return 密文字节
     */
    public static byte[] encryptCbc(byte[] plainData, byte[] key, byte[] iv) {
        return doFinal("SM4/CBC/PKCS5Padding", Cipher.ENCRYPT_MODE, plainData, key, iv);
    }

    /**
     * SM4-CBC-PKCS5Padding 解密
     *
     * @param cipherData 密文字节
     * @param key        16 字节密钥
     * @param iv         16 字节 IV
     * @return 明文字节
     */
    public static byte[] decryptCbc(byte[] cipherData, byte[] key, byte[] iv) {
        return doFinal("SM4/CBC/PKCS5Padding", Cipher.DECRYPT_MODE, cipherData, key, iv);
    }

    /**
     * SM4-ECB 文本加密（输入明文，输出 Base64 密文）
     *
     * @param plainText 明文
     * @param keyHex    十六进制密钥（16 字节）
     * @return Base64 密文
     */
    public static String encryptEcbToBase64(String plainText, String keyHex) {
        Objects.requireNonNull(plainText, "plainText 不能为空");
        byte[] cipher = encryptEcb(plainText.getBytes(StandardCharsets.UTF_8), parseHex(keyHex));
        return Base64.getEncoder().encodeToString(cipher);
    }

    /**
     * SM4-ECB 文本解密（输入 Base64 密文，输出明文）
     *
     * @param cipherTextBase64 Base64 密文
     * @param keyHex           十六进制密钥（16 字节）
     * @return 明文字符串
     */
    public static String decryptEcbFromBase64(String cipherTextBase64, String keyHex) {
        Objects.requireNonNull(cipherTextBase64, "cipherTextBase64 不能为空");
        byte[] cipher = Base64.getDecoder().decode(cipherTextBase64);
        byte[] plain = decryptEcb(cipher, parseHex(keyHex));
        return new String(plain, StandardCharsets.UTF_8);
    }

    /**
     * SM4-CBC 文本加密（输入明文，输出 Base64 密文）
     *
     * @param plainText 明文
     * @param keyHex    十六进制密钥（16 字节）
     * @param ivHex     十六进制 IV（16 字节）
     * @return Base64 密文
     */
    public static String encryptCbcToBase64(String plainText, String keyHex, String ivHex) {
        Objects.requireNonNull(plainText, "plainText 不能为空");
        byte[] cipher = encryptCbc(plainText.getBytes(StandardCharsets.UTF_8), parseHex(keyHex), parseIvHex(ivHex));
        return Base64.getEncoder().encodeToString(cipher);
    }

    /**
     * SM4-CBC 文本解密（输入 Base64 密文，输出明文）
     *
     * @param cipherTextBase64 Base64 密文
     * @param keyHex           十六进制密钥（16 字节）
     * @param ivHex            十六进制 IV（16 字节）
     * @return 明文字符串
     */
    public static String decryptCbcFromBase64(String cipherTextBase64, String keyHex, String ivHex) {
        Objects.requireNonNull(cipherTextBase64, "cipherTextBase64 不能为空");
        byte[] cipher = Base64.getDecoder().decode(cipherTextBase64);
        byte[] plain = decryptCbc(cipher, parseHex(keyHex), parseIvHex(ivHex));
        return new String(plain, StandardCharsets.UTF_8);
    }

    /**
     * 执行加解密主流程
     */
    private static byte[] doFinal(String transformation, int mode, byte[] input, byte[] key, byte[] iv) {
        Objects.requireNonNull(input, "input 不能为空");
        validateKey(key);
        try {
            Cipher cipher = Cipher.getInstance(transformation, BouncyCastleProvider.PROVIDER_NAME);
            SecretKeySpec keySpec = new SecretKeySpec(key, "SM4");
            if (iv == null) {
                cipher.init(mode, keySpec);
            } else {
                validateIv(iv);
                cipher.init(mode, keySpec, new IvParameterSpec(iv));
            }
            return cipher.doFinal(input);
        } catch (Exception e) {
            throw new SxwlBusinessException(500, "SM4 加解密失败", e);
        }
    }

    /**
     * 校验密钥长度（16 字节）
     */
    private static void validateKey(byte[] key) {
        Objects.requireNonNull(key, "key 不能为空");
        if (key.length != SM4_KEY_LENGTH) {
            throw new SxwlBusinessException(400, "SM4 key 长度必须为 16 字节");
        }
    }

    /**
     * 校验 IV 长度（16 字节）
     */
    private static void validateIv(byte[] iv) {
        Objects.requireNonNull(iv, "iv 不能为空");
        if (iv.length != SM4_KEY_LENGTH) {
            throw new SxwlBusinessException(400, "SM4 iv 长度必须为 16 字节");
        }
    }

    /**
     * 解析十六进制字符串为字节数组
     */
    private static byte[] parseHex(String hex) {
        Objects.requireNonNull(hex, "hex 不能为空");
        try {
            byte[] bytes = HEX.parseHex(hex);
            validateKey(bytes);
            return bytes;
        } catch (IllegalArgumentException e) {
            throw new SxwlBusinessException(400, "无效的十六进制密钥", e);
        }
    }

    /**
     * 解析十六进制 IV
     */
    private static byte[] parseIvHex(String ivHex) {
        Objects.requireNonNull(ivHex, "ivHex 不能为空");
        try {
            byte[] iv = HEX.parseHex(ivHex);
            validateIv(iv);
            return iv;
        } catch (IllegalArgumentException e) {
            throw new SxwlBusinessException(400, "无效的十六进制 IV", e);
        }
    }
}

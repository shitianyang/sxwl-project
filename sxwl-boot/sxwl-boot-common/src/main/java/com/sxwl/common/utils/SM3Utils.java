package com.sxwl.common.utils;

import com.sxwl.common.exception.SxwlBusinessException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.Objects;

/**
 * SM3 摘要工具类
 * <p>
 * 提供常用的 SM3 摘要计算与校验能力。
 * </p>
 *
 * @author shitianyang
 * @date 2026/6/13
 */
public final class SM3Utils {

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
    private SM3Utils() {
        throw new UnsupportedOperationException("SM3Utils 工具类，不允许实例化");
    }

    /**
     * 对字节数组执行 SM3 摘要
     *
     * @param data 待摘要数据
     * @return 摘要结果字节数组（32 字节）
     */
    public static byte[] digest(byte[] data) {
        Objects.requireNonNull(data, "data 不能为空");
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SM3", BouncyCastleProvider.PROVIDER_NAME);
            return messageDigest.digest(data);
        } catch (Exception e) {
            throw new SxwlBusinessException(500, "SM3 摘要计算失败", e);
        }
    }

    /**
     * 对字符串执行 SM3 摘要（UTF-8 编码）
     *
     * @param content 待摘要字符串
     * @return 十六进制摘要字符串（小写）
     */
    public static String digestHex(String content) {
        Objects.requireNonNull(content, "content 不能为空");
        return digestHex(content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 对字节数组执行 SM3 摘要并返回十六进制字符串
     *
     * @param data 待摘要数据
     * @return 十六进制摘要字符串（小写）
     */
    public static String digestHex(byte[] data) {
        return HEX.formatHex(digest(data));
    }

    /**
     * 校验字符串的 SM3 摘要值
     *
     * @param content           原文（UTF-8 编码）
     * @param expectedDigestHex 期望摘要十六进制字符串
     * @return true-校验通过，false-校验失败
     */
    public static boolean verify(String content, String expectedDigestHex) {
        Objects.requireNonNull(content, "content 不能为空");
        Objects.requireNonNull(expectedDigestHex, "expectedDigestHex 不能为空");
        return digestHex(content).equalsIgnoreCase(expectedDigestHex);
    }
}

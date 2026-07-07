package com.sxwl.common.utils;

import com.sxwl.common.exception.SxwlBusinessException;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.math.BigInteger;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Objects;

/**
 * SM2 非对称加密工具类
 * <p>
 * 提供 SM2 密钥对生成、加解密、签名与验签能力。
 * </p>
 *
 * @author shitianyang
 * @date 2026/6/13
 */
public final class SM2Utils {

    /**
     * SM2 椭圆曲线名称
     */
    private static final String SM2_CURVE_NAME = "sm2p256v1";

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
    private SM2Utils() {
        throw new UnsupportedOperationException("SM2Utils 工具类，不允许实例化");
    }

    /**
     * 从 X.509 编码公钥提取裸公钥（04||x||y 格式，供前端 sm-crypto 等 JS 库使用）
     *
     * @param x509PublicKeyHex X.509 编码的十六进制公钥
     * @return 裸公钥十六进制（130 字符，04 + 64 字符 x + 64 字符 y）
     */
    public static String toRawPublicKeyHex(String x509PublicKeyHex) {
        PublicKey publicKey = toPublicKey(x509PublicKeyHex);
        if (!(publicKey instanceof ECPublicKey ecPub)) {
            throw new SxwlBusinessException(400, "非 EC 公钥，无法提取裸公钥");
        }
        ECPoint w = ecPub.getW();
        String x = padLeft(w.getAffineX().toString(16), 64);
        String y = padLeft(w.getAffineY().toString(16), 64);
        return "04" + x + y;
    }

    private static String padLeft(String hex, int length) {
        if (hex.length() >= length) return hex;
        StringBuilder sb = new StringBuilder(length);
        for (int i = hex.length(); i < length; i++) sb.append('0');
        sb.append(hex);
        return sb.toString();
    }

    /**
     * 生成 SM2 密钥对
     *
     * @return Java KeyPair
     */
    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
            keyPairGenerator.initialize(new ECGenParameterSpec(SM2_CURVE_NAME), new SecureRandom());
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new SxwlBusinessException(500, "SM2 密钥对生成失败", e);
        }
    }

    /**
     * 生成 SM2 密钥对（十六进制）
     *
     * @return 十六进制公私钥对
     */
    public static SM2KeyPair generateKeyPairHex() {
        KeyPair keyPair = generateKeyPair();
        return new SM2KeyPair(toHex(keyPair.getPublic().getEncoded()), toHex(keyPair.getPrivate().getEncoded()));
    }

    /**
     * 使用公钥执行 SM2 加密
     * <p>
     * 说明：SM2 更适合加密短数据，长文本建议先使用 SM4 加密，再使用 SM2 加密 SM4 密钥。
     * </p>
     *
     * @param plainData    明文字节
     * @param publicKeyHex 十六进制公钥（X.509 编码）
     * @return 密文字节（C1C3C2）
     */
    public static byte[] encrypt(byte[] plainData, String publicKeyHex) {
        Objects.requireNonNull(plainData, "plainData 不能为空");
        Objects.requireNonNull(publicKeyHex, "publicKeyHex 不能为空");
        try {
            AsymmetricKeyParameter publicKeyParameter = PublicKeyFactory.createKey(parseHex(publicKeyHex));
            SM2Engine sm2Engine = new SM2Engine(SM2Engine.Mode.C1C3C2);
            sm2Engine.init(true, new ParametersWithRandom(publicKeyParameter, new SecureRandom()));
            return sm2Engine.processBlock(plainData, 0, plainData.length);
        } catch (Exception e) {
            throw new SxwlBusinessException(500, "SM2 加密失败", e);
        }
    }

    /**
     * 使用私钥执行 SM2 解密
     *
     * @param cipherData    密文字节（C1C3C2）
     * @param privateKeyHex 十六进制私钥（PKCS#8 编码）
     * @return 明文字节
     */
    public static byte[] decrypt(byte[] cipherData, String privateKeyHex) {
        Objects.requireNonNull(cipherData, "cipherData 不能为空");
        Objects.requireNonNull(privateKeyHex, "privateKeyHex 不能为空");
        try {
            AsymmetricKeyParameter privateKeyParameter = PrivateKeyFactory.createKey(parseHex(privateKeyHex));
            SM2Engine sm2Engine = new SM2Engine(SM2Engine.Mode.C1C3C2);
            sm2Engine.init(false, privateKeyParameter);
            return sm2Engine.processBlock(cipherData, 0, cipherData.length);
        } catch (Exception e) {
            throw new SxwlBusinessException(500, "SM2 解密失败", e);
        }
    }

    /**
     * SM2 文本加密（输出 Base64 密文）
     *
     * @param plainText    明文文本（UTF-8）
     * @param publicKeyHex 十六进制公钥（X.509 编码）
     * @return Base64 密文
     */
    public static String encryptToBase64(String plainText, String publicKeyHex) {
        Objects.requireNonNull(plainText, "plainText 不能为空");
        byte[] cipherData = encrypt(plainText.getBytes(StandardCharsets.UTF_8), publicKeyHex);
        return Base64.getEncoder().encodeToString(cipherData);
    }

    /**
     * SM2 文本解密（输入 Base64 密文，输出明文）
     *
     * @param cipherTextBase64 Base64 密文
     * @param privateKeyHex    十六进制私钥（PKCS#8 编码）
     * @return 明文文本（UTF-8）
     */
    public static String decryptFromBase64(String cipherTextBase64, String privateKeyHex) {
        Objects.requireNonNull(cipherTextBase64, "cipherTextBase64 不能为空");
        byte[] cipherData = Base64.getDecoder().decode(cipherTextBase64);
        byte[] plainData = decrypt(cipherData, privateKeyHex);
        return new String(plainData, StandardCharsets.UTF_8);
    }

    /**
     * 使用 SM3withSM2 算法签名
     *
     * @param data          待签名字节
     * @param privateKeyHex 十六进制私钥（PKCS#8 编码）
     * @return 签名字节
     */
    public static byte[] sign(byte[] data, String privateKeyHex) {
        Objects.requireNonNull(data, "data 不能为空");
        Objects.requireNonNull(privateKeyHex, "privateKeyHex 不能为空");
        try {
            Signature signature = Signature.getInstance("SM3withSM2", BouncyCastleProvider.PROVIDER_NAME);
            signature.initSign(toPrivateKey(privateKeyHex), new SecureRandom());
            signature.update(data);
            return signature.sign();
        } catch (Exception e) {
            throw new SxwlBusinessException(500, "SM2 签名失败", e);
        }
    }

    /**
     * 使用 SM3withSM2 算法签名，返回十六进制签名串
     *
     * @param content       待签名字符串（UTF-8）
     * @param privateKeyHex 十六进制私钥（PKCS#8 编码）
     * @return 十六进制签名
     */
    public static String signToHex(String content, String privateKeyHex) {
        Objects.requireNonNull(content, "content 不能为空");
        return toHex(sign(content.getBytes(StandardCharsets.UTF_8), privateKeyHex));
    }

    /**
     * 使用 SM3withSM2 算法验签
     *
     * @param data         原文字节
     * @param signData     签名字节
     * @param publicKeyHex 十六进制公钥（X.509 编码）
     * @return true-验签成功，false-验签失败
     */
    public static boolean verify(byte[] data, byte[] signData, String publicKeyHex) {
        Objects.requireNonNull(data, "data 不能为空");
        Objects.requireNonNull(signData, "signData 不能为空");
        Objects.requireNonNull(publicKeyHex, "publicKeyHex 不能为空");
        try {
            Signature signature = Signature.getInstance("SM3withSM2", BouncyCastleProvider.PROVIDER_NAME);
            signature.initVerify(toPublicKey(publicKeyHex));
            signature.update(data);
            return signature.verify(signData);
        } catch (Exception e) {
            throw new SxwlBusinessException(500, "SM2 验签失败", e);
        }
    }

    /**
     * 使用十六进制签名串进行验签
     *
     * @param content      原文字符串（UTF-8）
     * @param signHex      十六进制签名
     * @param publicKeyHex 十六进制公钥（X.509 编码）
     * @return true-验签成功，false-验签失败
     */
    public static boolean verifyHexSign(String content, String signHex, String publicKeyHex) {
        Objects.requireNonNull(content, "content 不能为空");
        Objects.requireNonNull(signHex, "signHex 不能为空");
        return verify(content.getBytes(StandardCharsets.UTF_8), parseHex(signHex), publicKeyHex);
    }

    /**
     * 十六进制字符串转公钥对象
     */
    public static PublicKey toPublicKey(String publicKeyHex) {
        Objects.requireNonNull(publicKeyHex, "publicKeyHex 不能为空");
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
            return keyFactory.generatePublic(new X509EncodedKeySpec(parseHex(publicKeyHex)));
        } catch (Exception e) {
            throw new SxwlBusinessException(400, "无效的 SM2 公钥", e);
        }
    }

    /**
     * 十六进制字符串转私钥对象
     */
    public static PrivateKey toPrivateKey(String privateKeyHex) {
        Objects.requireNonNull(privateKeyHex, "privateKeyHex 不能为空");
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("EC", BouncyCastleProvider.PROVIDER_NAME);
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(parseHex(privateKeyHex)));
        } catch (Exception e) {
            throw new SxwlBusinessException(400, "无效的 SM2 私钥", e);
        }
    }

    /**
     * 字节数组转十六进制
     */
    private static String toHex(byte[] data) {
        return HEX.formatHex(data);
    }

    /**
     * 十六进制字符串转字节数组
     */
    private static byte[] parseHex(String hex) {
        Objects.requireNonNull(hex, "hex 不能为空");
        try {
            return HEX.parseHex(hex);
        } catch (IllegalArgumentException e) {
            throw new SxwlBusinessException(400, "无效的十六进制字符串", e);
        }
    }

    /**
     * SM2 十六进制密钥对模型
     */
    public static final class SM2KeyPair {
        /**
         * 公钥（X.509 编码十六进制）
         */
        private final String publicKeyHex;

        /**
         * 私钥（PKCS#8 编码十六进制）
         */
        private final String privateKeyHex;

        public SM2KeyPair(String publicKeyHex, String privateKeyHex) {
            this.publicKeyHex = publicKeyHex;
            this.privateKeyHex = privateKeyHex;
        }

        public String getPublicKeyHex() {
            return publicKeyHex;
        }

        public String getPrivateKeyHex() {
            return privateKeyHex;
        }

        @Override
        public String toString() {
            return "SM2KeyPair{" +
                    "publicKeyHex='" + publicKeyHex + '\'' +
                    ", privateKeyHex='" + privateKeyHex + '\'' +
                    '}';
        }
    }
}

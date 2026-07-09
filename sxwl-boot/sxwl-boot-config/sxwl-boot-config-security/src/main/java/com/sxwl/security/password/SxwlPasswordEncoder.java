package com.sxwl.security.password;

import com.sxwl.common.utils.SM3Utils;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * SM3 密码编码器
 * <p>
 * 实现 Spring Security {@link PasswordEncoder} 接口，使用 SM3 哈希算法 + 随机盐。
 * </p>
 *
 * <h3>编码格式</h3>
 * <pre>{sm3}{base64Salt}$hash</pre>
 * <ul>
 *   <li>{sm3} — 算法标识前缀</li>
 *   <li>{base64Salt} — 16 字节随机盐的 Base64 编码</li>
 *   <li>$ — 分隔符</li>
 *   <li>hash — SM3(password + salt, 10000 轮) 的十六进制输出</li>
 * </ul>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public class SxwlPasswordEncoder implements PasswordEncoder {

    /** SM3 迭代轮数 */
    private static final int ITERATIONS = 10000;

    /** 盐长度（字节） */
    private static final int SALT_LENGTH = 16;

    /** 算法标识前缀 */
    private static final String PREFIX = "{sm3}";

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Override
    public String encode(CharSequence rawPassword) {
        byte[] salt = new byte[SALT_LENGTH];
        SECURE_RANDOM.nextBytes(salt);
        String saltBase64 = Base64.getEncoder().encodeToString(salt);

        String hash = sm3Hash(rawPassword.toString(), salt);
        return PREFIX + saltBase64 + "$" + hash;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        if (!encodedPassword.startsWith(PREFIX)) {
            return false;
        }

        // 解析 salt 和 hash
        String body = encodedPassword.substring(PREFIX.length());
        int dollarIndex = body.indexOf('$');
        if (dollarIndex <= 0) {
            return false;
        }

        String saltBase64 = body.substring(0, dollarIndex);
        String expectedHash = body.substring(dollarIndex + 1);

        byte[] salt = Base64.getDecoder().decode(saltBase64);
        String actualHash = sm3Hash(rawPassword.toString(), salt);

        return actualHash.equals(expectedHash);
    }

    /**
     * SM3 多轮哈希
     *
     * @param password 明文密码
     * @param salt     盐字节数组
     * @return 十六进制哈希字符串
     */
    private String sm3Hash(String password, byte[] salt) {
        byte[] data = password.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] combined = new byte[data.length + salt.length];
        System.arraycopy(data, 0, combined, 0, data.length);
        System.arraycopy(salt, 0, combined, data.length, salt.length);

        // 多轮迭代
        byte[] hash = combined;
        for (int i = 0; i < ITERATIONS; i++) {
            hash = SM3Utils.digest(hash);
        }
        return java.util.HexFormat.of().formatHex(hash);
    }
}

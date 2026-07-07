package com.sxwl.auth.crypto;

import com.sxwl.common.utils.SM2Utils;
import com.sxwl.security.config.SxwlSecurityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * SM2 密码解密器
 * <p>
 * 职责：将前端传来的 SM2 加密密码（Base64 密文）还原为明文。
 * 私钥为空时（开发环境）直接返回原文，方便本地调试。
 * </p>
 *
 * <h3>为什么独立成类</h3>
 * <ul>
 *   <li>解密算法和策略流程解耦——后续换 SM4 只需改这一个类</li>
 *   <li>可独立单元测试，不依赖 Spring Security 上下文</li>
 * </ul>
 *
 * @author shitianyang
 * @date 2026/7/7
 * @since 0.1.0
 */
@Component
public class SxwlPasswordDecryptor {

    private static final Logger log = LoggerFactory.getLogger(SxwlPasswordDecryptor.class);

    private final String privateKey;

    public SxwlPasswordDecryptor(SxwlSecurityProperties properties) {
        this.privateKey = properties.getSm2PrivateKey();
        if (privateKey == null || privateKey.isBlank()) {
            log.warn("SM2 私钥未配置（sxwl.security.sm2-private-key），密码将按明文处理——仅限开发环境！");
        }
    }

    /**
     * 解密前端传来的 SM2 加密密码
     *
     * @param encryptedPassword Base64 密文（前端用 SM2 公钥加密后 Base64 编码）
     * @return 明文密码；私钥为空时直接返回原文（开发环境兼容）
     */
    public String decrypt(String encryptedPassword) {
        if (encryptedPassword == null || encryptedPassword.isBlank()) {
            return "";
        }
        // 开发环境：未配置私钥时跳过解密
        if (privateKey == null || privateKey.isBlank()) {
            log.debug("SM2 私钥为空，跳过解密，返回原文");
            return encryptedPassword;
        }
        try {
            return SM2Utils.decryptFromBase64(encryptedPassword, privateKey);
        } catch (Exception e) {
            log.error("SM2 密码解密失败", e);
            throw e;
        }
    }
}

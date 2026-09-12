package com.sxwl;

import com.sxwl.security.password.SxwlPasswordEncoder;
import com.sxwl.common.utils.SM2Utils;
import com.sxwl.common.utils.SM2Utils.SM2KeyPair;
import com.sxwl.common.utils.SxwlJwtUtils;
import com.sxwl.common.utils.SxwlSnowFlakeUtils;
import com.sxwl.common.utils.SxwlSnowFlakeUtils.SnowFlakeIdMeta;
import com.sxwl.security.config.SxwlSecurityProperties;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import java.util.Base64;

/**
 * 简单测试：生成测试用户密码哈希 + 验证 JWT 配置
 *
 * @author shitianyang
 * @date 2026/7/7
 * @since 0.1.0
 */
public class SxwlApplicationTest {

    /**
     * 生成测试用户密码哈希（SM3 + 随机盐 + 10000 轮迭代）
     * <p>
     * 运行后将输出 SQL INSERT 语句，直接复制到数据库执行即可。
     * </p>
     */
    @Test
    public void generatePasswordHash() {
        SxwlPasswordEncoder encoder = new SxwlPasswordEncoder();

        String rawPassword = "@Sxwl0324!";
        String encodedPassword = encoder.encode(rawPassword);

        // 验证密码匹配
        boolean matches = encoder.matches(rawPassword, encodedPassword);

        System.out.println("================================================");
        System.out.println("  明文密码: " + rawPassword);
        System.out.println("  编码结果: " + encodedPassword);
        System.out.println("  匹配验证: " + (matches ? "通过 ✓" : "失败 ✗"));
        System.out.println("================================================");
        System.out.println();
        System.out.println("  -- 复制以下 SQL 插入测试用户 --");
        System.out.println("  INSERT INTO sys_user_info (");
        System.out.println("      id, username, password, real_name, nickname,");
        System.out.println("      phone, status, create_by, create_org, create_time");
        System.out.println("  ) VALUES (");
        System.out.println("      1,");
        System.out.println("      'admin',");
        System.out.println("      '" + encodedPassword + "',");
        System.out.println("      '管理员',");
        System.out.println("      'Admin',");
        System.out.println("      '13800000000',");
        System.out.println("      1,");
        System.out.println("      1,");
        System.out.println("      1,");
        System.out.println("      NOW()");
        System.out.println("  );");
        System.out.println();
        System.out.println("  -- 登录测试 --");
        System.out.println("  POST http://localhost:8080/auth/login/password");
        System.out.println("  Content-Type: application/json");
        System.out.println("  {");
        System.out.println("    \"username\": \"admin\",");
        System.out.println("    \"password\": \"" + rawPassword + "\"");
        System.out.println("  }");
        System.out.println("  （SM2 私钥为空时前端直接发明文密码即可）");
        System.out.println("================================================");

        // 断言：编码结果以 {sm3} 开头，匹配验证通过
        assert encodedPassword.startsWith("{sm3}")
                : "编码结果应以 {sm3} 开头，实际: " + encodedPassword;
        assert matches : "密码匹配验证失败";
    }

    /**
     * 生成 JWT Token（access + refresh），并验证解析/类型/过期时间
     */
    @Test
    public void generateJwtToken() {
        String secret = "sxwl-test-jwt-secret-key-2026";
        long userId = 1L;
        String deviceId = "test-device-001";

        // 1. 生成 Access Token
        long accessExpire = 1800L;
        String accessToken = SxwlJwtUtils.builder()
                .userId(userId)
                .tokenType(SxwlJwtUtils.TOKEN_TYPE_ACCESS)
                .deviceId(deviceId)
                .expireSeconds(accessExpire)
                .build(secret);

        // 2. 生成 Refresh Token
        long refreshExpire = 604800L;
        String refreshToken = SxwlJwtUtils.builder()
                .userId(userId)
                .tokenType(SxwlJwtUtils.TOKEN_TYPE_REFRESH)
                .deviceId(deviceId)
                .expireSeconds(refreshExpire)
                .build(secret);

        System.out.println("================================================");
        System.out.println("  JWT Token 生成测试");
        System.out.println("  secret: " + secret);
        System.out.println("  userId: " + userId);
        System.out.println("  deviceId: " + deviceId);
        System.out.println("------------------------------------------------");
        System.out.println("  Access Token (" + accessExpire + "s):");
        System.out.println("  " + accessToken);
        System.out.println();
        System.out.println("  Refresh Token (" + refreshExpire + "s):");
        System.out.println("  " + refreshToken);
        System.out.println("================================================");

        // 3. 解析 Access Token 并校验
        Claims accessClaims = SxwlJwtUtils.parseClaims(accessToken, secret);
        Long parsedUserId = SxwlJwtUtils.resolveUserId(accessClaims);
        String parsedType = SxwlJwtUtils.resolveTokenType(accessClaims);
        String parsedDeviceId = SxwlJwtUtils.resolveDeviceId(accessClaims);
        String parsedJti = SxwlJwtUtils.resolveJwtId(accessClaims);

        assert parsedUserId != null && parsedUserId == userId
                : "userId 解析不匹配，期望: " + userId + "，实际: " + parsedUserId;
        assert SxwlJwtUtils.TOKEN_TYPE_ACCESS.equals(parsedType)
                : "tokenType 解析不匹配，期望: " + SxwlJwtUtils.TOKEN_TYPE_ACCESS + "，实际: " + parsedType;
        assert deviceId.equals(parsedDeviceId)
                : "deviceId 解析不匹配，期望: " + deviceId + "，实际: " + parsedDeviceId;
        assert parsedJti != null && !parsedJti.isEmpty()
                : "jti 不能为空";

        // 4. 解析 Refresh Token 并校验
        Claims refreshClaims = SxwlJwtUtils.parseClaims(refreshToken, secret);
        assert SxwlJwtUtils.TOKEN_TYPE_REFRESH.equals(SxwlJwtUtils.resolveTokenType(refreshClaims))
                : "Refresh Token type 解析错误";

        // 5. validateToken
        assert SxwlJwtUtils.validateToken(accessToken, secret) : "validateToken 应该返回 true";

        System.out.println();
        System.out.println("  解析校验:");
        System.out.println("    userId=" + parsedUserId
                + "  type=" + parsedType
                + "  deviceId=" + parsedDeviceId
                + "  jti=" + parsedJti);
        System.out.println("================================================");
        System.out.println("  JWT 生成 + 解析全部通过 ✓");
        System.out.println("================================================");
    }

    /**
     * 生成随机 JWT 签名密钥（256 位十六进制），复制到 yaml 即可用
     */
    @Test
    public void generateJwtSecret() {
        // 随机种子 → SM3 哈希 → 256 位密钥
        byte[] seed = new byte[32];
        new java.security.SecureRandom().nextBytes(seed);
        byte[] hash = com.sxwl.common.utils.SM3Utils.digest(seed);
        String secret = java.util.HexFormat.of().formatHex(hash);

        assert secret.length() == 64 : "密钥长度应为 64，实际: " + secret.length();

        System.out.println("================================================");
        System.out.println("  随机 JWT 签名密钥（SecureRandom → SM3 → 256 位）");
        System.out.println("------------------------------------------------");
        System.out.println("  " + secret);
        System.out.println("------------------------------------------------");
        System.out.println("  复制到 application-*.yaml:");
        System.out.println("  sxwl:");
        System.out.println("    security:");
        System.out.println("      jwt-secret: " + secret);
        System.out.println("================================================");
    }

    // ==================== 雪花算法测试 ====================

    /**
     * 雪花算法基础测试：生成 → 解析 → 校验
     */
    @Test
    public void snowflakeBasicTest() {
        long dataCenterId = SxwlSnowFlakeUtils.getDefaultDataCenterId();
        long workerId = SxwlSnowFlakeUtils.getDefaultWorkerId();
        long epoch = SxwlSnowFlakeUtils.getDefaultEpoch();

        System.out.println("================================================");
        System.out.println("  雪花算法基础信息");
        System.out.println("  起始时间戳 (epoch): " + epoch
                + " (" + java.time.Instant.ofEpochMilli(epoch) + ")");
        System.out.println("  数据中心 ID: " + dataCenterId);
        System.out.println("  机器节点 ID: " + workerId);
        System.out.println("================================================");

        // 1. 生成 10 个 ID，校验不重复且递增
        long previousId = 0;
        for (int i = 0; i < 10; i++) {
            long id = SxwlSnowFlakeUtils.nextId();

            // 必须 > 0
            assert id > 0 : "雪花 ID 必须大于 0，实际: " + id;

            // 必须递增
            assert id > previousId : "雪花 ID 必须递增，前一个: " + previousId + "，当前: " + id;
            previousId = id;

            // 解析 ID
            SnowFlakeIdMeta meta = SxwlSnowFlakeUtils.parseId(id);

            // 校验数据中心和机器节点
            assert meta.getDataCenterId() == dataCenterId
                    : "数据中心 ID 不匹配，期望: " + dataCenterId + "，实际: " + meta.getDataCenterId();
            assert meta.getWorkerId() == workerId
                    : "机器节点 ID 不匹配，期望: " + workerId + "，实际: " + meta.getWorkerId();

            System.out.println("  [" + i + "] id=" + id
                    + " | dataCenter=" + meta.getDataCenterId()
                    + " | worker=" + meta.getWorkerId()
                    + " | seq=" + meta.getSequence()
                    + " | time=" + meta.getInstant());
        }

        // 2. nextIdStr() 测试
        String idStr = SxwlSnowFlakeUtils.nextIdStr();
        assert idStr != null && !idStr.isEmpty() : "nextIdStr() 不能返回空字符串";
        assert idStr.matches("\\d+") : "nextIdStr() 必须为纯数字，实际: " + idStr;
        System.out.println();
        System.out.println("  nextIdStr() = " + idStr + " ✓");

        System.out.println("================================================");
        System.out.println("  基础测试全部通过 ✓");
        System.out.println("================================================");
    }

    /**
     * 雪花算法性能测试：单线程 10 万次
     */
    @Test
    public void snowflakePerformanceTest() {
        int count = 100_000;

        long start = System.currentTimeMillis();
        long lastId = 0;
        for (int i = 0; i < count; i++) {
            long id = SxwlSnowFlakeUtils.nextId();
            assert id > lastId : "ID 必须递增，位置: " + i;
            lastId = id;
        }
        long elapsed = System.currentTimeMillis() - start;

        double qps = count * 1000.0 / Math.max(elapsed, 1);

        System.out.println("================================================");
        System.out.println("  雪花算法性能测试");
        System.out.println("  生成数量: " + count);
        System.out.println("  耗时: " + elapsed + " ms");
        System.out.println("  QPS: " + String.format("%.0f", qps) + " /s");
        System.out.println("================================================");

        // 10 万次必须在 5 秒内完成（预期 < 1 秒）
        assert elapsed < 5000
                : "性能异常：10 万次生成耗时 " + elapsed + " ms，超过 5 秒上限";
    }

    /**
     * 自定义生成器测试：指定 dataCenterId 和 workerId
     */
    @Test
    public void snowflakeCustomGeneratorTest() {
        long customDataCenterId = 1;
        long customWorkerId = 2;

        SxwlSnowFlakeUtils.SnowFlakeGenerator generator =
                SxwlSnowFlakeUtils.createGenerator(customDataCenterId, customWorkerId);

        System.out.println("================================================");
        System.out.println("  自定义生成器测试");
        System.out.println("  dataCenterId=" + customDataCenterId
                + "  workerId=" + customWorkerId);
        System.out.println("================================================");

        for (int i = 0; i < 5; i++) {
            long id = generator.nextId();
            SnowFlakeIdMeta meta = SxwlSnowFlakeUtils.parseId(id);

            assert meta.getDataCenterId() == customDataCenterId
                    : "自定义生成器 dataCenterId 不匹配";
            assert meta.getWorkerId() == customWorkerId
                    : "自定义生成器 workerId 不匹配";

            System.out.println("  [" + i + "] id=" + id
                    + " | dc=" + meta.getDataCenterId()
                    + " | wk=" + meta.getWorkerId()
                    + " | seq=" + meta.getSequence());
        }

        // nextIdStr()
        String idStr = generator.nextIdStr();
        assert idStr != null && Long.parseLong(idStr) > 0;
        System.out.println();
        System.out.println("  nextIdStr() = " + idStr + " ✓");
        System.out.println("================================================");
        System.out.println("  自定义生成器测试通过 ✓");
        System.out.println("================================================");
    }

    // ==================== SM2 密钥对生成 ====================

    /**
     * 生成 SM2 密钥对（私钥 + 公钥），验证加解密后输出配置指南
     */
    @Test
    public void generateSm2KeyPair() {
        // 1. 生成密钥对 
        // 私钥 308193020100301306072a8648ce3d020106082a811ccf5501822d0479307702010104207254706dd1da4d2aaccd5baad8e64e76210b29fd1b20f07c3b52f9a9a31a295fa00a06082a811ccf5501822da144034200048ab625630f36ddeeeeb3e8ca2f3b35f2920326cc95e2538a8c0f37ffe88e8541f635f98edec194493803c8c0e49d16cd923c23dd53ac575e0df6e8fc34d25d15
        // 公钥 3059301306072a8648ce3d020106082a811ccf5501822d034200048ab625630f36ddeeeeb3e8ca2f3b35f2920326cc95e2538a8c0f37ffe88e8541f635f98edec194493803c8c0e49d16cd923c23dd53ac575e0df6e8fc34d25d15
        SM2KeyPair keyPair = SM2Utils.generateKeyPairHex();
        String publicKeyHex = keyPair.getPublicKeyHex();
        String privateKeyHex = keyPair.getPrivateKeyHex();

        assert publicKeyHex != null && !publicKeyHex.isBlank() : "公钥不能为空";
        assert privateKeyHex != null && !privateKeyHex.isBlank() : "私钥不能为空";

        // 2. 加密 → 解密 验证
        String plainText = "hello SM2 test";
        String cipherBase64 = SM2Utils.encryptToBase64(plainText, publicKeyHex);
        String decrypted = SM2Utils.decryptFromBase64(cipherBase64, privateKeyHex);
        assert plainText.equals(decrypted) : "SM2 加解密验证失败，原文: " + plainText + "，解密结果: " + decrypted;

        // 3. 签名 → 验签 验证
        String signHex = SM2Utils.signToHex(plainText, privateKeyHex);
        boolean verified = SM2Utils.verifyHexSign(plainText, signHex, publicKeyHex);
        assert verified : "SM2 验签失败";

        System.out.println("================================================");
        System.out.println("  SM2 密钥对生成成功（加解密 + 签名验签验证通过 ✓）");
        System.out.println("================================================");
        System.out.println();
        System.out.println("  【公钥-X.509】（后端加解密用）");
        System.out.println("  " + publicKeyHex);
        System.out.println();
        System.out.println("  【公钥-裸格式】（前端 sm-crypto 用，04||x||y）");
        System.out.println("  " + SM2Utils.toRawPublicKeyHex(publicKeyHex));
        System.out.println();
        System.out.println("  【私钥】");
        System.out.println("  " + privateKeyHex);
        System.out.println();
        System.out.println("------------------------------------------------");
        System.out.println("  注意：sm2-private-key 已废弃，SM2 密钥轮换管理器自动生成随机密钥。");
        System.out.println("  此密钥对仅用于手动验证加解密链路，无需配置到 YAML 中。");
        System.out.println("================================================");
        System.out.println();
        System.out.println("  【验证过程】");
        System.out.println("  原文: " + plainText);
        System.out.println("  密文: " + cipherBase64);
        System.out.println("  解密: " + decrypted + " ✓");
        System.out.println("  签名: " + signHex);
        System.out.println("  验签: " + (verified ? "通过 ✓" : "失败 ✗"));
    }

    /**
     * 模拟"前端 SM2 加密 → 后端 SM2 解密"完整链路
     * <p>
     * 使用临时的 SM2 密钥对验证整个加解密流程，不依赖配置文件。
     * </p>
     */
    @Test
    public void sm2FrontendToBackendTest() {
        // 生成临时密钥对模拟完整流程
        SM2KeyPair keyPair = SM2Utils.generateKeyPairHex();
        String x509PublicKey = keyPair.getPublicKeyHex();    // 后端加解密用
        String privateKey = keyPair.getPrivateKeyHex();      // 后端解密用
        String rawPublicKey = SM2Utils.toRawPublicKeyHex(x509PublicKey); // 前端 sm-crypto 用

        // 1. 模拟前端：sm-crypto 用裸公钥加密
        String plainPassword = "@Sxwl0324!";
        String frontendEncrypted = simulateFrontendEncrypt(plainPassword, rawPublicKey);
        assert frontendEncrypted != null && !frontendEncrypted.isBlank() : "前端加密结果不能为空";

        // 2. 模拟后端：SM2Utils 用 PKCS#8 私钥解密
        String backendDecrypted = SM2Utils.decryptFromBase64(frontendEncrypted, privateKey);
        assert plainPassword.equals(backendDecrypted)
                : "前后端加解密不匹配！原文: " + plainPassword + "，解密结果: " + backendDecrypted;

        System.out.println("================================================");
        System.out.println("  前后端 SM2 加解密链路验证");
        System.out.println("================================================");
        System.out.println("  【前端】裸公钥: " + rawPublicKey);
        System.out.println("  【前端】明文密码: " + plainPassword);
        System.out.println("  【前端】加密后 Base64: " + frontendEncrypted);
        System.out.println();
        System.out.println("  【后端】PKCS#8 私钥: " + privateKey);
        System.out.println("  【后端】解密结果: " + backendDecrypted);
        System.out.println();
        System.out.println("  验证: " + (plainPassword.equals(backendDecrypted) ? "通过 ✓" : "失败 ✗"));
        System.out.println("================================================");
        System.out.println();
        System.out.println("  前端代码使用方式:");
        System.out.println("  import { encryptPassword } from '@/utils/sm2';");
        System.out.println("  const encrypted = encryptPassword('" + plainPassword + "', rawPublicKey);");
        System.out.println("  // POST /auth/login/password  { password: encrypted }");
        System.out.println("================================================");
    }

    /**
     * 模拟前端 sm-crypto 加密（用 Java 的 BouncyCastle 模拟，输出 Base64）
     */
    private String simulateFrontendEncrypt(String plainText, String rawPublicKeyHex) {
        java.math.BigInteger x = new java.math.BigInteger(rawPublicKeyHex.substring(2, 66), 16);
        java.math.BigInteger y = new java.math.BigInteger(rawPublicKeyHex.substring(66), 16);
        try {
            // 从 SM2 命名曲线获取域参数（避免 getCurve()/getG() 返回 JCE 类型导致的类型不匹配）
            org.bouncycastle.asn1.x9.X9ECParameters x9 =
                    org.bouncycastle.asn1.gm.GMNamedCurves.getByName("sm2p256v1");
            org.bouncycastle.crypto.params.ECDomainParameters domain =
                    new org.bouncycastle.crypto.params.ECDomainParameters(
                            x9.getCurve(), x9.getG(), x9.getN(), x9.getH());

            // 用裸坐标构造 BC EC 点
            org.bouncycastle.math.ec.ECPoint bcPoint = domain.getCurve().createPoint(x, y);
            org.bouncycastle.crypto.params.ECPublicKeyParameters pubKeyParams =
                    new org.bouncycastle.crypto.params.ECPublicKeyParameters(bcPoint, domain);

            org.bouncycastle.crypto.engines.SM2Engine engine =
                    new org.bouncycastle.crypto.engines.SM2Engine(org.bouncycastle.crypto.engines.SM2Engine.Mode.C1C3C2);
            engine.init(true, new org.bouncycastle.crypto.params.ParametersWithRandom(pubKeyParams, new java.security.SecureRandom()));
            byte[] data = plainText.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            byte[] cipherBytes = engine.processBlock(data, 0, data.length);
            return Base64.getEncoder().encodeToString(cipherBytes);
        } catch (Exception e) {
            throw new RuntimeException("模拟前端加密失败", e);
        }
    }
}

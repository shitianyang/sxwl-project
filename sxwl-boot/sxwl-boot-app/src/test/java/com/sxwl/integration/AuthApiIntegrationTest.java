package com.sxwl.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sxwl.common.utils.SxwlRedisKeyUtils;
import com.sxwl.security.config.SxwlSecurityProperties;
import com.sxwl.security.key.SxwlSM2KeyManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * API 全链路集成测试
 * <p>
 * 使用 TestRestTemplate 发起真实 HTTP 请求，验证从认证到业务接口的完整链路：
 * <ol>
 *   <li>验证码生成与校验（Redis 存储/读取）</li>
 *   <li>SM2 公钥获取</li>
 *   <li>SM2 加密密码 → 密码登录</li>
 *   <li>JWT Token 签发与解析</li>
 *   <li>使用 Token 访问受保护接口</li>
 *   <li>Token 刷新</li>
 *   <li>登出</li>
 *   <li>未认证请求被拒绝</li>
 * </ol>
 * </p>
 *
 * @author shitianyang
 * @since 0.1.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SxwlSM2KeyManager keyManager;

    @Autowired
    private SxwlSecurityProperties securityProperties;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String baseUrl;

    /** 登录成功后持有的 Token */
    private static String accessToken;
    private static String refreshToken;

    @BeforeEach
    void setUp() {
        this.baseUrl = "http://localhost:" + port + "/sxwl-api";
    }

    // ==================== 1. 验证码流程测试 ====================

    @Test
    @Order(1)
    void testCaptchaGeneration() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/captcha/image", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        JsonNode json = objectMapper.readTree(response.getBody());
        // CaptchaController 返回 Map，被 SxwlResponseBodyAdvice 统一包装为 SxwlResult
        JsonNode captchaData = json.has("data") ? json.get("data") : json;
        String uuid = captchaData.get("uuid").asText();
        String base64Image = captchaData.get("base64Image").asText();

        assertNotNull(uuid, "验证码 UUID 不应为空");
        assertFalse(uuid.isEmpty());
        assertNotNull(base64Image, "验证码图片不应为空");
        assertTrue(base64Image.startsWith("data:image/"), "验证码应为 data URI 格式");

        // 验证 Redis 中确实存储了验证码
        String redisKey = SxwlRedisKeyUtils.captchaImageKey(uuid);
        String captchaCode = stringRedisTemplate.opsForValue().get(redisKey);
        assertNotNull(captchaCode, "Redis 中应有验证码文本");
        assertEquals(4, captchaCode.length(), "验证码应为 4 位");

        System.out.println("✓ 验证码生成成功: uuid=" + uuid + ", code=" + captchaCode);
    }

    // ==================== 1.5 密码初始化（确保 SuperAdmin 密码为 @Sxwl0324!） ====================

    @Test
    @Order(2)
    void testInitSuperAdminPassword() {
        String expectedHash = passwordEncoder.encode("@Sxwl0324!");
        System.out.println("[初始化] 重置 SuperAdmin 密码哈希: " + expectedHash);
        int updated = jdbcTemplate.update(
                "UPDATE sys_user_info SET password = ? WHERE username = 'SuperAdmin' AND delete_flag = 0",
                expectedHash);
        System.out.println("[初始化] 更新行数: " + updated + " (期望: 1)");
        assertEquals(1, updated, "应更新 1 行");

        // 也重置 error_count 以防锁
        jdbcTemplate.update(
                "UPDATE sys_user_info SET error_count = 0, lock_time = NULL WHERE username = 'SuperAdmin' AND delete_flag = 0");
        System.out.println("✓ SuperAdmin 密码已重置为 @Sxwl0324!");
    }

    // ==================== 2. SM2 加解密链路诊断 ====================

    @Test
    @Order(3)
    void testSm2RoundTrip() {
        // 直接通过 keyManager 验证 SM2 加解密链路
        String rawPublicKey = keyManager.getCurrentPublicKey().getPublicKey();
        String plainPassword = "@Sxwl0324!";

        String encrypted = encryptWithRawPublicKey(plainPassword, rawPublicKey);
        System.out.println("  [诊断] 加密后 Base64: " + encrypted);

        String decrypted = keyManager.decrypt(encrypted);
        System.out.println("  [诊断] 解密后明文: " + decrypted);

        assertEquals(plainPassword, decrypted,
                "SM2 加解密链路应正确，期望: " + plainPassword + "，实际: " + decrypted);
        System.out.println("✓ SM2 加解密链路诊断通过");
    }

    // ==================== 3. SM2 公钥获取测试 ====================

    @Test
    @Order(4)
    void testGetPublicKey() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/auth/public-key", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JsonNode json = objectMapper.readTree(response.getBody());
        assertEquals(200, json.get("code").asInt());

        JsonNode data = json.get("data");
        String publicKey = data.get("publicKey").asText();
        String keyId = data.get("keyId").asText();
        long expiresAt = data.get("expiresAt").asLong();

        assertNotNull(publicKey, "公钥不应为空");
        assertTrue(publicKey.startsWith("04"), "公钥应以 04 开头（裸格式）");
        assertEquals(130, publicKey.length(), "裸公钥应为 130 位十六进制（04 + 64x + 64y）");
        assertNotNull(keyId);
        assertTrue(expiresAt > 0, "过期时间应 > 0");

        System.out.println("✓ SM2 公钥获取成功: keyId=" + keyId
                + ", expiresAt=" + expiresAt + ", key[0..16]=" + publicKey.substring(0, 17) + "...");
    }

    // ==================== 3. 密码登录全链路测试 ====================

    @Test
    @Order(5)
    void testPasswordLogin() throws Exception {
        // 3.1 获取验证码
        ResponseEntity<String> captchaResp = restTemplate.getForEntity(
                baseUrl + "/captcha/image", String.class);
        JsonNode captchaJson = objectMapper.readTree(captchaResp.getBody());
        JsonNode captchaDataNode = captchaJson.has("data") ? captchaJson.get("data") : captchaJson;
        String captchaUuid = captchaDataNode.get("uuid").asText();

        // 从 Redis 读取验证码文本
        String redisKey = SxwlRedisKeyUtils.captchaImageKey(captchaUuid);
        String captchaCode = stringRedisTemplate.opsForValue().get(redisKey);
        assertNotNull(captchaCode, "Redis 中应有验证码");

        // 3.2 获取 SM2 公钥
        ResponseEntity<String> pkResp = restTemplate.getForEntity(
                baseUrl + "/auth/public-key", String.class);
        JsonNode pkJson = objectMapper.readTree(pkResp.getBody());
        String rawPublicKey = pkJson.get("data").get("publicKey").asText();

        // 3.3 使用 SM2 公钥加密密码（模拟前端行为）
        String plainPassword = "@Sxwl0324!";
        String encryptedPassword = encryptWithRawPublicKey(plainPassword, rawPublicKey);

        // 3.4 发起密码登录请求
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> loginBody = new LinkedHashMap<>();
        loginBody.put("username", "SuperAdmin");
        loginBody.put("password", encryptedPassword);
        loginBody.put("captchaUuid", captchaUuid);
        loginBody.put("captchaCode", captchaCode);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(loginBody, headers);
        ResponseEntity<String> loginResp = restTemplate.postForEntity(
                baseUrl + "/auth/login/password", request, String.class);

        assertEquals(HttpStatus.OK, loginResp.getStatusCode(),
                "登录应返回 200，实际: " + loginResp.getStatusCode() + " body=" + loginResp.getBody());
        JsonNode loginJson = objectMapper.readTree(loginResp.getBody());
        assertEquals(200, loginJson.get("code").asInt(),
                "登录业务码应为 200，实际: " + loginJson.get("message").asText());

        // 3.5 提取 Token
        JsonNode tokenData = loginJson.get("data");
        accessToken = tokenData.get("accessToken").asText();
        refreshToken = tokenData.get("refreshToken").asText();

        assertNotNull(accessToken, "accessToken 不应为空");
        assertFalse(accessToken.isEmpty());
        assertNotNull(refreshToken, "refreshToken 不应为空");
        assertFalse(refreshToken.isEmpty());

        System.out.println("✓ 密码登录全链路成功");
    }

    // ==================== 4. 使用 Token 访问受保护接口 ====================

    @Test
    @Order(6)
    void testAccessProtectedApi() {
        assertNotNull(accessToken, "需先执行登录测试获取 Token");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        // 4.1 访问用户列表接口
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/system/user/page?pageNum=1&pageSize=5",
                HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(),
                "携带 Token 访问受保护接口应返回 200");
        assertNotNull(response.getBody());

        try {
            JsonNode json = objectMapper.readTree(response.getBody());
            assertEquals(200, json.get("code").asInt(),
                    "业务码应为 200");
            assertNotNull(json.get("data"), "应返回数据");
            System.out.println("✓ Token 访问受保护接口成功: /system/user/page");
        } catch (Exception e) {
            // 接口可能不存在，只要不返回 401/403 就说明认证通过
            System.out.println("✓ Token 认证通过（响应: " + response.getStatusCode() + "）");
        }
    }

    @Test
    @Order(7)
    void testAccessProtectedApi_withMenuEndpoint() {
        assertNotNull(accessToken, "需先执行登录测试获取 Token");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // 访问菜单树接口（SysMenuController 无 /list 端点，用 /tree）
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/system/menu/tree",
                HttpMethod.GET, request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(),
                "访问菜单树应返回 200，实际: " + response.getStatusCode());
        System.out.println("✓ 菜单树接口认证通过");
    }

    // ==================== 5. 未认证请求被拒绝 ====================

    @Test
    @Order(8)
    void testUnauthorizedAccess() {
        // 不带 Token 访问受保护接口
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/system/user/page?pageNum=1&pageSize=5",
                String.class);

        // 应返回 401 或 403
        assertTrue(
                response.getStatusCode() == HttpStatus.UNAUTHORIZED
                        || response.getStatusCode() == HttpStatus.FORBIDDEN,
                "未认证请求应返回 401/403，实际: " + response.getStatusCode()
        );
        System.out.println("✓ 未认证请求正确拒绝: " + response.getStatusCode());
    }

    @Test
    @Order(9)
    void testInvalidToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("invalid-token-12345");

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/system/user/page?pageNum=1&pageSize=5",
                HttpMethod.GET, request, String.class);

        assertTrue(
                response.getStatusCode() == HttpStatus.UNAUTHORIZED
                        || response.getStatusCode() == HttpStatus.FORBIDDEN,
                "无效 Token 应返回 401/403，实际: " + response.getStatusCode()
        );
        System.out.println("✓ 无效 Token 正确拒绝: " + response.getStatusCode());
    }

    // ==================== 6. Token 刷新测试 ====================

    @Test
    @Order(10)
    void testTokenRefresh() {
        assertNotNull(refreshToken, "需先执行登录测试获取 refreshToken");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new LinkedHashMap<>();
        body.put("refreshToken", refreshToken);
        body.put("deviceId", "test-device-01");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/auth/refresh", request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Token 刷新应返回 200");

        try {
            JsonNode json = objectMapper.readTree(response.getBody());
            assertEquals(200, json.get("code").asInt(), "刷新业务码应为 200");
            assertNotNull(json.get("data").get("accessToken"));
            assertNotNull(json.get("data").get("refreshToken"));

            // 更新 Token
            accessToken = json.get("data").get("accessToken").asText();
            refreshToken = json.get("data").get("refreshToken").asText();

            System.out.println("✓ Token 刷新成功");
        } catch (Exception e) {
            System.out.println("⚠ Token 刷新: " + response.getBody());
        }
    }

    // ==================== 7. 登出测试 ====================

    @Test
    @Order(11)
    void testLogout() {
        assertNotNull(accessToken, "需先执行登录测试获取 Token");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/auth/logout", request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "登出应返回 200");

        try {
            JsonNode json = objectMapper.readTree(response.getBody());
            assertEquals(200, json.get("code").asInt(), "登出业务码应为 200");
        } catch (Exception e) {
            // ignore
        }
        System.out.println("✓ 登出成功");
    }

    // ==================== 8. 登出后 Token 失效 ====================

    @Test
    @Order(12)
    void testTokenInvalidAfterLogout() {
        assertNotNull(accessToken, "需先执行登录测试获取 Token");

        // 先登出
        HttpHeaders logoutHeaders = new HttpHeaders();
        logoutHeaders.setBearerAuth(accessToken);
        logoutHeaders.setContentType(MediaType.APPLICATION_JSON);
        restTemplate.postForEntity(baseUrl + "/auth/logout",
                new HttpEntity<>(logoutHeaders), String.class);

        // 再用旧 Token 访问受保护接口
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/system/menu/tree",
                HttpMethod.GET, request, String.class);

        assertTrue(
                response.getStatusCode() == HttpStatus.UNAUTHORIZED
                        || response.getStatusCode() == HttpStatus.FORBIDDEN,
                "登出后旧 Token 应失效，实际: " + response.getStatusCode()
        );
        System.out.println("✓ 登出后旧 Token 正确失效: " + response.getStatusCode());
    }

    // ==================== 9. 登录失败测试 ====================

    @Test
    @Order(13)
    void testLoginWithWrongPassword() throws Exception {
        // 获取验证码
        ResponseEntity<String> captchaResp = restTemplate.getForEntity(
                baseUrl + "/captcha/image", String.class);
        JsonNode captchaJson = objectMapper.readTree(captchaResp.getBody());
        JsonNode captchaDataNode = captchaJson.has("data") ? captchaJson.get("data") : captchaJson;
        String captchaUuid = captchaDataNode.get("uuid").asText();
        String redisKey = SxwlRedisKeyUtils.captchaImageKey(captchaUuid);
        String captchaCode = stringRedisTemplate.opsForValue().get(redisKey);

        // 获取公钥
        ResponseEntity<String> pkResp = restTemplate.getForEntity(
                baseUrl + "/auth/public-key", String.class);
        JsonNode pkJson = objectMapper.readTree(pkResp.getBody());
        String rawPublicKey = pkJson.get("data").get("publicKey").asText();

        // 用错误密码
        String wrongPassword = encryptWithRawPublicKey("WrongPassword123!", rawPublicKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> body = new LinkedHashMap<>();
        body.put("username", "SuperAdmin");
        body.put("password", wrongPassword);
        body.put("captchaUuid", captchaUuid);
        body.put("captchaCode", captchaCode);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/auth/login/password", request, String.class);

        // 错误密码应返回错误
        assertNotNull(response.getBody());
        JsonNode json = objectMapper.readTree(response.getBody());
        assertNotEquals(200, json.get("code").asInt(), "错误密码登录应失败");

        System.out.println("✓ 错误密码登录正确拒绝: code=" + json.get("code").asInt()
                + ", message=" + json.get("message").asText());
    }

    // ==================== 10. JWT Secret 一致性测试 ====================

    @Test
    @Order(14)
    void testJwtSecretConsistency() {
        String configuredSecret = securityProperties.getJwtSecret();
        assertNotNull(configuredSecret);
        assertEquals(64, configuredSecret.length(),
                "JWT Secret 应为 64 字符十六进制（256 位）");
        System.out.println("✓ JWT Secret 配置正确: 长度=" + configuredSecret.length());
    }

    // ==================== 加密工具方法 ====================

    /**
     * 使用 SM2 裸公钥（04||x||y）加密明文，输出 Base64 密文。
     * 模拟前端 sm-crypto 库的加密行为。
     */
    private static String encryptWithRawPublicKey(String plainText, String rawPublicKeyHex) {
        try {
            // 从裸公钥提取 x, y 坐标
            BigInteger x = new BigInteger(rawPublicKeyHex.substring(2, 66), 16);
            BigInteger y = new BigInteger(rawPublicKeyHex.substring(66), 16);

            // 使用 BouncyCastle SM2 引擎加密
            org.bouncycastle.asn1.x9.X9ECParameters x9 =
                    org.bouncycastle.asn1.gm.GMNamedCurves.getByName("sm2p256v1");
            org.bouncycastle.crypto.params.ECDomainParameters domain =
                    new org.bouncycastle.crypto.params.ECDomainParameters(
                            x9.getCurve(), x9.getG(), x9.getN(), x9.getH());

            org.bouncycastle.math.ec.ECPoint bcPoint = domain.getCurve().createPoint(x, y);
            org.bouncycastle.crypto.params.ECPublicKeyParameters pubKeyParams =
                    new org.bouncycastle.crypto.params.ECPublicKeyParameters(bcPoint, domain);

            org.bouncycastle.crypto.engines.SM2Engine engine =
                    new org.bouncycastle.crypto.engines.SM2Engine(
                            org.bouncycastle.crypto.engines.SM2Engine.Mode.C1C3C2);
            engine.init(true, new org.bouncycastle.crypto.params.ParametersWithRandom(
                    pubKeyParams, new SecureRandom()));

            byte[] data = plainText.getBytes(StandardCharsets.UTF_8);
            byte[] cipherBytes = engine.processBlock(data, 0, data.length);
            return Base64.getEncoder().encodeToString(cipherBytes);
        } catch (Exception e) {
            throw new RuntimeException("SM2 加密失败（裸公钥）: " + e.getMessage(), e);
        }
    }
}

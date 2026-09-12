package com.sxwl.auth.controller;

import com.sxwl.common.utils.SxwlCaptchaUtils;
import com.sxwl.common.utils.SxwlRedisKeyUtils;
import com.sxwl.redis.helper.SxwlRedisHelper;
import com.sxwl.security.config.SxwlSmsProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * 验证码接口控制器
 * <p>
 * 提供图形验证码的生成接口，验证码文本存入 Redis 供后续校验。
 * </p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    private static final Logger log = LoggerFactory.getLogger(CaptchaController.class);

    /** 图形验证码 Redis TTL（120 秒） */
    private static final Duration CAPTCHA_TTL = Duration.ofSeconds(120);

    /** 短信验证码 Redis TTL（5 分钟） */
    private static final Duration SMS_CODE_TTL = Duration.ofMinutes(5);

    /** 短信验证码长度 */
    private static final int SMS_CODE_LENGTH = 6;

    /** 短信验证码发送间隔（60 秒） */
    private static final Duration SMS_SEND_INTERVAL = Duration.ofSeconds(60);

    /** 短信验证码每日最大发送次数 */
    private static final int SMS_MAX_PER_DAY = 10;

    /** 短信验证码每日统计 Redis TTL（24 小时） */
    private static final Duration SMS_DAILY_TTL = Duration.ofHours(24);

    private final SxwlRedisHelper redisHelper;
    private final SxwlSmsProperties smsProperties;

    public CaptchaController(SxwlRedisHelper redisHelper, SxwlSmsProperties smsProperties) {
        this.redisHelper = redisHelper;
        this.smsProperties = smsProperties;
    }

    /**
     * 获取图形验证码
     * <p>
     * 生成 4 位字母数字混合验证码，将文本存入 Redis（Key = captcha:image:{uuid}），
     * 返回 Base64 图片 URL 和 UUID，前端在登录时一并提交。
     * </p>
     *
     * @return 验证码信息（uuid + base64Image），由统一响应体包装为 SxwlResult
     */
    @GetMapping("/image")
    public Map<String, String> getImageCaptcha() {
        // 1. 生成验证码
        SxwlCaptchaUtils.CaptchaResult result = SxwlCaptchaUtils.generateImageCaptcha();
        String code = result.getCode();
        String base64Image = result.getBase64Image();

        // 2. 生成 UUID 并存入 Redis
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String key = SxwlRedisKeyUtils.captchaImageKey(uuid);
        redisHelper.set(key, code, CAPTCHA_TTL);

        log.debug("验证码已生成: uuid={}", uuid);
        return Map.of("uuid", uuid, "base64Image", base64Image);
    }

    /**
     * 获取短信验证码
     * <p>
     * 生成 6 位数字短信验证码，存入 Redis（Key = captcha:sms:{phone}），TTL = 5 分钟。
     * 同时限制同一手机号 60 秒内不得重复发送。
     * </p>
     * <p>
     * TODO: 对接短信服务商 API（阿里云 SMS / 腾讯云 SMS / 华为云 SMS）
     * </p>
     *
     * @param request 请求参数（包含 phone 字段）
     * @return 发送结果（sent = true/false）
     */
    @PostMapping("/sms")
    public Map<String, String> getSmsCaptcha(@RequestBody Map<String, String> request) {
        // 1. 参数校验
        String phone = request.get("phone");
        if (phone == null || phone.isEmpty()) {
            throw new IllegalArgumentException("手机号不能为空");
        }

        // 2. 验证手机号格式
        if (!phone.matches(/^1[3-9]\d{9}$/)) {
            throw new IllegalArgumentException("请输入正确的手机号");
        }

        // 3. 检查发送间隔（60 秒内不得重复发送）
        String sendTimeKey = SxwlRedisKeyUtils.captchaLimitKey(phone);
        if (redisHelper.exists(sendTimeKey)) {
            long remainingSeconds = redisHelper.getTtl(sendTimeKey).getSeconds();
            throw new IllegalArgumentException(String.format("请等待 %d 秒后重新获取", (int) remainingSeconds));
        }

        // 4. 检查每日发送次数限制（防刷机制）
        String dailyCountKey = buildDailySmsCountKey(phone);
        int todayCount = redisHelper.get(dailyCountKey, Integer.class, 0);
        if (todayCount >= SMS_MAX_PER_DAY) {
            throw new IllegalArgumentException(String.format("今日验证码已发送 %d 次，已达上限，请明天再试", SMS_MAX_PER_DAY));
        }

        // 4. 生成 6 位短信验证码
        String code = generateSmsCode();

        // 5. 存入 Redis
        String captchaKey = SxwlRedisKeyUtils.captchaSmsKey(phone);
        redisHelper.set(captchaKey, code, SMS_CODE_TTL);

        // 6. 记录发送时间（用于控制发送间隔）
        redisHelper.set(sendTimeKey, "1", SMS_SEND_INTERVAL);

        // 7. 增加每日发送次数统计
        int newCount = todayCount + 1;
        redisHelper.set(dailyCountKey, String.valueOf(newCount), SMS_DAILY_TTL);

        // 7. 调用短信服务发送（如果启用）
        if (smsProperties.isEnabled()) {
            sendSmsByProvider(phone, code);
        } else {
            log.info("短信服务未启用（仅生成验证码）: phone={}, code={}", maskPhone(phone), code);
        }

        log.info("短信验证码已生成: phone={}, key={}", maskPhone(phone), captchaKey);
        return Map.of("sent", "true");
    }

    /**
     * 生成 6 位随机数字验证码
     */
    private String generateSmsCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000; // 100000 ~ 999999
        return String.valueOf(code);
    }

    /**
     * 脱敏处理手机号
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return "***";
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 调用短信服务商发送验证码
     * <p>
     * TODO: 需要对接真实短信服务商（阿里云 SMS / 腾讯云 SMS / 华为云 SMS）
     * </p>
     *
     * @param phone   手机号
     * @param code    短信验证码
     */
    private void sendSmsByProvider(String phone, String code) {
        // TODO: 实现对接短信服务商
        // 示例：阿里云 SMS 配置
        // DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "accessKeyId", "accessKeySecret");
        // IAcsClient client = new DefaultAcsClient(profile);
        // SendSmsRequest request = new SendSmsRequest();
        // request.setPhoneNumbers(phone);
        // request.setSignName("你的签名");
        // request.setTemplateCode("SMS_123456789");
        // request.setTemplateParam("{\"code\":\"" + code + "\"}");
        // client.doAction(request);

        // 临时日志记录，实际部署时需要移除
        log.debug("短信发送待实现: phone={}, code={}", maskPhone(phone), code);
    }

    /**
     * 构建每日短信发送次数统计 Key
     * <p>格式：sms:daily:{phone}:{yyyy-MM-dd}</p>
     *
     * @param phone 手机号
     * @return Redis Key
     */
    private String buildDailySmsCountKey(String phone) {
        String date = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
        return "sms:daily:" + phone + ":" + date;
    }
}

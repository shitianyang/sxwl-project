package com.sxwl.common.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 验证码工具类
 * <p>
 * 提供两种验证码生成能力：
 * <ul>
 *   <li>图形验证码：4 位字母数字混合</li>
 *   <li>短信/邮箱验证码：6 位纯数字</li>
 * </ul>
 * 纯工具类，无外部依赖（不涉及 Redis 存储），存储逻辑由调用方负责。
 * </p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public final class SxwlCaptchaUtils {

    /**
     * 图形验证码默认位数
     */
    public static final int IMAGE_CODE_LENGTH = 4;

    /**
     * 短信/邮箱验证码默认位数
     */
    public static final int SMS_CODE_LENGTH = 6;

    /**
     * 默认图片宽度
     */
    public static final int DEFAULT_WIDTH = 120;

    /**
     * 默认图片高度
     */
    public static final int DEFAULT_HEIGHT = 40;

    /**
     * 图形验证码字符集（字母数字混合，去掉 0/O/1/I/l 避免混淆）
     */
    private static final String IMAGE_CHAR_POOL = "23456789ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz";

    /**
     * 短信/邮箱验证码字符集（纯数字，去掉 0 避免首位为 0 时前端展示异常）
     */
    private static final String NUMERIC_CHAR_POOL = "123456789";

    private static final SecureRandom RANDOM = new SecureRandom();

    private SxwlCaptchaUtils() {
        throw new UnsupportedOperationException("SxwlCaptchaUtils 工具类，不允许实例化");
    }

    // ==================== 验证码文本生成 ====================

    /**
     * 生成图形验证码文本（字母数字混合，默认 4 位）
     *
     * @return 随机验证码字符串
     */
    public static String generateImageCode() {
        return generateImageCode(IMAGE_CODE_LENGTH);
    }

    /**
     * 生成图形验证码文本（字母数字混合，指定位数）
     *
     * @param length 位数（建议 4-6）
     * @return 随机验证码字符串
     */
    public static String generateImageCode(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("验证码位数必须大于 0");
        }
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            code.append(IMAGE_CHAR_POOL.charAt(RANDOM.nextInt(IMAGE_CHAR_POOL.length())));
        }
        return code.toString();
    }

    /**
     * 生成短信/邮箱验证码文本（纯数字，默认 6 位）
     *
     * @return 随机数字验证码字符串
     */
    public static String generateNumericCode() {
        return generateNumericCode(SMS_CODE_LENGTH);
    }

    /**
     * 生成短信/邮箱验证码文本（纯数字，指定位数）
     *
     * @param length 位数（建议 4-6）
     * @return 随机数字验证码字符串
     */
    public static String generateNumericCode(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("验证码位数必须大于 0");
        }
        StringBuilder code = new StringBuilder(length);
        // 首位不能是 0，使用 NUMERIC_CHAR_POOL（1-9）
        for (int i = 0; i < length; i++) {
            if (i == 0) {
                code.append(NUMERIC_CHAR_POOL.charAt(RANDOM.nextInt(NUMERIC_CHAR_POOL.length())));
            } else {
                code.append((char) ('0' + RANDOM.nextInt(10)));
            }
        }
        return code.toString();
    }

    // ==================== 验证码图片生成 ====================

    /**
     * 根据验证码文本生成图片（默认尺寸 120×40）
     *
     * @param code 验证码文本
     * @return BufferedImage
     */
    public static BufferedImage generateImage(String code) {
        return generateImage(code, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * 根据验证码文本生成指定尺寸的图片
     *
     * @param code   验证码文本
     * @param width  图片宽度
     * @param height 图片高度
     * @return BufferedImage
     */
    public static BufferedImage generateImage(String code, int width, int height) {
        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("验证码文本不能为空");
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("图片宽高必须大于 0");
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 开启抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 背景色（浅灰）
        g.setColor(new Color(245, 245, 245));
        g.fillRect(0, 0, width, height);

        // 干扰线（6 条随机彩色线）
        for (int i = 0; i < 6; i++) {
            g.setColor(randomColor(180, 220));
            int x1 = RANDOM.nextInt(width);
            int y1 = RANDOM.nextInt(height);
            int x2 = RANDOM.nextInt(width);
            int y2 = RANDOM.nextInt(height);
            g.drawLine(x1, y1, x2, y2);
        }

        // 干扰点（30 个）
        for (int i = 0; i < 30; i++) {
            g.setColor(randomColor(160, 200));
            int x = RANDOM.nextInt(width);
            int y = RANDOM.nextInt(height);
            g.fillOval(x, y, 2, 2);
        }

        // 字体（加粗，高度自适应）
        int fontSize = Math.max(18, height - 8);
        Font font = new Font("Arial", Font.BOLD, fontSize);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics();

        // 计算整体居中
        int totalWidth = 0;
        for (char c : code.toCharArray()) {
            totalWidth += fm.charWidth(c);
        }
        int gap = Math.max(2, width / (code.length() * 3));
        totalWidth += gap * (code.length() - 1);

        int startX = (width - totalWidth) / 2;
        int baselineY = (height + fm.getAscent() - fm.getDescent()) / 2;

        // 逐字符绘制，带随机旋转 + 随机颜色
        int curX = startX;
        for (int i = 0; i < code.length(); i++) {
            g.setColor(randomColor(20, 120));
            char ch = code.charAt(i);
            int charWidth = fm.charWidth(ch);
            int cx = curX + charWidth / 2;
            double angle = (RANDOM.nextDouble() - 0.5) * 0.4;
            g.rotate(angle, cx, baselineY);
            g.drawString(String.valueOf(ch), curX, baselineY);
            g.rotate(-angle, cx, baselineY);
            curX += charWidth + gap;
        }

        g.dispose();
        return image;
    }

    // ==================== Base64 编码 ====================

    /**
     * 将 BufferedImage 转为 Base64 Data URL（PNG 格式）
     *
     * @param image BufferedImage
     * @return Base64 Data URL，如 "data:image/png;base64,iVBOR..."
     */
    public static String toBase64Png(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException("image 不能为空");
        }
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("验证码图片 Base64 编码失败", e);
        }
    }

    /**
     * 一步生成图形验证码（文本 + Base64 图片）
     * <p>等价于 {@code generateImageCode() + generateImage() + toBase64Png()}</p>
     *
     * @param length 验证码位数
     * @param width  图片宽度
     * @param height 图片高度
     * @return CaptchaResult（code + base64Image）
     */
    public static CaptchaResult generateImageCaptcha(int length, int width, int height) {
        String code = generateImageCode(length);
        BufferedImage image = generateImage(code, width, height);
        String base64 = toBase64Png(image);
        return new CaptchaResult(code, base64);
    }

    /**
     * 一步生成图形验证码（默认 4 位，120×40）
     */
    public static CaptchaResult generateImageCaptcha() {
        return generateImageCaptcha(IMAGE_CODE_LENGTH, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    // ==================== 内部方法 ====================

    /**
     * 生成随机颜色（指定亮度范围）
     */
    private static Color randomColor(int minBrightness, int maxBrightness) {
        int r = minBrightness + RANDOM.nextInt(maxBrightness - minBrightness + 1);
        int g = minBrightness + RANDOM.nextInt(maxBrightness - minBrightness + 1);
        int b = minBrightness + RANDOM.nextInt(maxBrightness - minBrightness + 1);
        return new Color(r, g, b);
    }

    // ==================== 结果模型 ====================

    /**
     * 图形验证码生成结果
     */
    public static final class CaptchaResult {

        private final String code;
        private final String base64Image;

        private CaptchaResult(String code, String base64Image) {
            this.code = code;
            this.base64Image = base64Image;
        }

        /** 验证码文本（存入 Redis） */
        public String getCode() {
            return code;
        }

        /** Base64 Data URL（返回前端） */
        public String getBase64Image() {
            return base64Image;
        }

        @Override
        public String toString() {
            return "CaptchaResult{code='" + code + "'}";
        }
    }
}

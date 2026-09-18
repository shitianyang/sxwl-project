package com.sxwl.common.utils;

import com.sxwl.common.constants.SxwlSystemConstants;

/**
 * 数据脱敏工具类
 *
 * <p>提供敏感信息的格式化隐藏功能，支持手机号、身份证号、邮箱、银行卡号、
 * 地址等常见敏感字段的脱敏处理。符合数据安全法和隐私保护合规要求。</p>
 *
 * <p><b>使用示例：</b></p>
 * <pre>{@code
 * // 手机号脱敏：13812345678 → 138****5678
 * String phone = SxwlDesensitizeUtils.mobile("13812345678");
 * 
 * // 身份证脱敏：110101199001011234 → 110101**********1234
 * String idCard = SxwlDesensitizeUtils.idCard("110101199001011234");
 * 
 * // 邮箱脱敏：zhangsan@example.com → z***@example.com
 * String email = SxwlDesensitizeUtils.email("zhangsan@example.com");
 * 
 * // 银行卡脱敏：6222021234567890 → ************7890
 * String bankCard = SxwlDesensitizeUtils.bankCard("6222021234567890");
 * 
 * // 地址脱敏：北京市朝阳区xxx → xxx*******
 * String address = SxwlDesensitizeUtils.address("北京市朝阳区xxx街道xx号");
 * }</pre>
 *
 * @author shitianyang
 * @date 2026/9/17
 * @since 0.1.0
 */
public final class SxwlDesensitizeUtils {

    private SxwlDesensitizeUtils() {
        throw new UnsupportedOperationException("SxwlDesensitizeUtils 工具类，不允许实例化");
    }

    /**
     * 手机号脱敏（保留前 3 位和后 4 位）
     *
     * <p>脱敏规则：</p>
     * <ul>
     *   <li>11 位手机号：如 13812345678 → 138****5678</li>
     *   <li>不足 11 位：保留前 2 位，其余用 * 替换</li>
     * </ul>
     *
     * @param mobile 原始手机号
     * @return 脱敏后的手机号
     */
    public static String mobile(String mobile) {
        if (mobile == null || mobile.isEmpty()) {
            return "";
        }

        // 清理可能的格式字符（空格、横杠）
        String cleanMobile = mobile.replaceAll("[\\s\\-]", "");

        // 手机号必须是数字
        if (!cleanMobile.matches("^\\d+$")) {
            return "";
        }

        // 11 位标准手机号
        if (cleanMobile.length() == 11) {
            return cleanMobile.substring(0, 3) + SxwlSystemConstants.MASK_MOBILE + cleanMobile.substring(7);
        }

        // 其他长度：保留前 2 位，后 4 位可见（如果有）
        int visibleEnd = Math.min(4, cleanMobile.length() - 2);
        int maskCount = cleanMobile.length() - 2 - visibleEnd;
        
        StringBuilder result = new StringBuilder();
        result.append(cleanMobile.substring(0, 2));
        for (int i = 0; i < maskCount; i++) {
            result.append(SxwlSystemConstants.MASK_CHARACTER);
        }
        if (visibleEnd > 0) {
            result.append(cleanMobile.substring(cleanMobile.length() - visibleEnd));
        }

        return result.toString();
    }

    /**
     * 身份证号脱敏（保留前 3 位后 4 位）
     *
     * <p>脱敏规则：</p>
     * <ul>
     *   <li>18 位身份证：如 110101199001011234 → 110101**********1234</li>
     *   <li>15 位老身份证：如 110101900101123 → 110101******123</li>
     *   <li>其他长度：中间部分脱敏</li>
     * </ul>
     *
     * @param idCard 原始身份证号
     * @return 脱敏后的身份证号
     */
    public static String idCard(String idCard) {
        if (idCard == null || idCard.isEmpty()) {
            return "";
        }

        // 清理输入
        String cleanIdCard = idCard.replaceAll("[\\s\\-]", "");

        if (!cleanIdCard.matches("^\\d+$")) {
            return "";
        }

        // 18 位身份证：保留前 6 位和后 4 位
        if (cleanIdCard.length() >= 18) {
            int prefixVisible = 6;
            int suffixVisible = 4;
            int maskCount = cleanIdCard.length() - prefixVisible - suffixVisible;
            
            return cleanIdCard.substring(0, prefixVisible) 
                    + "*".repeat(maskCount) 
                    + cleanIdCard.substring(cleanIdCard.length() - suffixVisible);
        }

        // 15 位老身份证：保留前 6 位和后 3 位
        if (cleanIdCard.length() == 15) {
            return cleanIdCard.substring(0, 6) 
                    + "******" 
                    + cleanIdCard.substring(12);
        }

        // 其他长度：保留前后各 2 位
        int prefixVisible = Math.min(2, cleanIdCard.length() / 2);
        int suffixVisible = Math.min(2, cleanIdCard.length() - prefixVisible);
        int maskCount = cleanIdCard.length() - prefixVisible - suffixVisible;

        StringBuilder result = new StringBuilder();
        result.append(cleanIdCard.substring(0, prefixVisible));
        for (int i = 0; i < maskCount; i++) {
            result.append(SxwlSystemConstants.MASK_ID_CARD);
        }
        if (suffixVisible > 0) {
            result.append(cleanIdCard.substring(cleanIdCard.length() - suffixVisible));
        }

        return result.toString();
    }

    /**
     * 邮箱脱敏（用户名部分脱敏，域名保留）
     *
     * <p>脱敏规则：</p>
     * <ul>
     *   <li>用户名字符数 >= 3：保留第 1 和最后 1 位，中间用 * 替换（如 z***@example.com）</li>
     *   <li>用户名字符数 = 2：替换为 **@domain.com</li>
     *   <li>用户名字符数 = 1：替换为 *@domain.com</li>
     * </ul>
     *
     * @param email 原始邮箱地址
     * @return 脱敏后的邮箱地址
     */
    public static String email(String email) {
        if (email == null || email.isEmpty()) {
            return "";
        }

        // 检查是否为有效的邮箱格式
        if (!email.contains("@")) {
            return "";
        }

        String[] parts = email.split("@", 2);
        if (parts.length != 2) {
            return "";
        }

        String username = parts[0];
        String domain = parts[1];

        if (username.isEmpty() || domain.isEmpty()) {
            return "";
        }

        // 脱敏用户名部分
        String maskedUsername;
        if (username.length() >= 3) {
            // 保留第 1 和最后 1 位
            maskedUsername = username.charAt(0) + "*".repeat(username.length() - 2) + username.charAt(username.length() - 1);
        } else if (username.length() == 2) {
            maskedUsername = "**";
        } else {
            maskedUsername = "*";
        }

        return maskedUsername + "@" + domain;
    }

    /**
     * 银行卡号脱敏（仅保留后 4 位）
     *
     * <p>脱敏规则：</p>
     * <ul>
     *   <li>卡号长度 >= 8：保留后 4 位，前面用 * 替换（如 ************7890）</li>
     *   <li>卡号长度 < 8：保留后 2 位，前面用 * 替换</li>
     * </ul>
     *
     * @param bankCard 原始银行卡号
     * @return 脱敏后的银行卡号
     */
    public static String bankCard(String bankCard) {
        if (bankCard == null || bankCard.isEmpty()) {
            return "";
        }

        // 清理输入（去除空格和横杠）
        String cleanBankCard = bankCard.replaceAll("[\\s\\-]", "");

        if (!cleanBankCard.matches("^\\d+$")) {
            return "";
        }

        // 仅保留后 4 位（如果卡号够长）
        int suffixVisible = Math.min(4, cleanBankCard.length());
        int prefixMask = cleanBankCard.length() - suffixVisible;

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < prefixMask; i++) {
            result.append(SxwlSystemConstants.MASK_CHARACTER);
        }
        if (suffixVisible > 0) {
            result.append(cleanBankCard.substring(cleanBankCard.length() - suffixVisible));
        }

        return result.toString();
    }

    /**
     * 地址脱敏（仅保留区名，后续内容脱敏）
     *
     * <p>脱敏规则：</p>
     * <ul>
     *   <li>包含省：保留"省"/"市"/"自治区"，后续脱敏（如 北京市*******）</li>
     *   <li>包含区/县：保留区县名，后续脱敏</li>
     *   <li>都不包含：保留前 6 位，后续脱敏</li>
     * </ul>
     *
     * @param address 原始地址
     * @return 脱敏后的地址
     */
    public static String address(String address) {
        if (address == null || address.isEmpty()) {
            return "";
        }

        // 尝试匹配省级行政区
        String provinceMatch = "";
        String remaining = address;

        // 匹配省、自治区、直辖市
        if (address.contains("省")) {
            int index = address.indexOf("省");
            provinceMatch = address.substring(0, index + 1);
            remaining = address.substring(index + 1);
        } else if (address.contains("自治区")) {
            int index = address.indexOf("自治区");
            provinceMatch = address.substring(0, index + 3);
            remaining = address.substring(index + 3);
        } else if (address.contains("市")) {
            int index = address.indexOf("市");
            provinceMatch = address.substring(0, index + 1);
            remaining = address.substring(index + 1);
        }

        // 如果没有匹配到省，尝试匹配区/县
        if (provinceMatch.isEmpty()) {
            if (address.contains("区") || address.contains("县")) {
                int index = address.contains("区") ? 
                        address.indexOf("区") : address.indexOf("县");
                provinceMatch = address.substring(0, index + 1);
                remaining = address.substring(index + 1);
            } else if (address.length() >= 6) {
                // 都不匹配，保留前 6 位
                provinceMatch = address.substring(0, 6);
                remaining = address.substring(6);
            }
        }

        // 剩余内容脱敏
        if (remaining.isEmpty()) {
            return provinceMatch;
        }

        int maskCount = Math.max(3, remaining.length());
        return provinceMatch + "*".repeat(Math.min(maskCount, remaining.length()));
    }

    /**
     * 姓名脱敏（双字节姓名显示姓氏，三字姓名显示首尾）
     *
     * <p>脱敏规则：</p>
     * <ul>
     *   <li>单字姓名：如 李 → 李*</li>
     *   <li>双字姓名：如 张三 → 张*</li>
     *   <li>三字及以上姓名：如 欧阳锋 → **锋，欧阳小明 → 欧**明</li>
     * </ul>
     *
     * @param name 原始姓名
     * @return 脱敏后的姓名
     */
    public static String name(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }

        int length = name.length();

        if (length == 1) {
            return name.charAt(0) + SxwlSystemConstants.MASK_CHARACTER;
        }

        if (length == 2) {
            return name.charAt(0) + SxwlSystemConstants.MASK_CHARACTER;
        }

        // 三字及以上：保留首字和末字
        StringBuilder result = new StringBuilder();
        result.append(name.charAt(0));
        for (int i = 1; i < length - 1; i++) {
            result.append(SxwlSystemConstants.MASK_CHARACTER);
        }
        result.append(name.charAt(length - 1));

        return result.toString();
    }

    /**
     * 银行卡类型检测
     *
     * @param bankCard 银行卡号
     * @return 银行名称（简化版，仅检测前缀）
     */
    public static String detectBankName(String bankCard) {
        if (bankCard == null || bankCard.isEmpty()) {
            return "未知";
        }

        String cleanCard = bankCard.replaceAll("[\\s\\-]", "");
        
        if (cleanCard.startsWith("62")) {
            return "中国银联";
        } else if (cleanCard.startsWith("4") || cleanCard.startsWith("5")) {
            return "Visa/MasterCard";
        } else if (cleanCard.startsWith("6010")) {
            return "建设银行";
        } else if (cleanCard.startsWith("6222") || cleanCard.startsWith("6227")) {
            return "工商银行";
        } else if (cleanCard.startsWith("6228")) {
            return "农业银行";
        } else if (cleanCard.startsWith("6222")) {
            return "中国银行";
        } else {
            return "其他银行";
        }
    }
}

package com.sxwl.security.password;

import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.security.config.SxwlSecurityProperties;

import java.util.regex.Pattern;

/**
 * 密码强度校验器
 * <p>
 * 校验密码是否满足安全策略：长度、复杂度（大小写+数字+特殊字符至少 3 种）。
 * </p>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
public class SxwlPasswordValidator {

    private static final Pattern LOWERCASE = Pattern.compile("[a-z]");
    private static final Pattern UPPERCASE = Pattern.compile("[A-Z]");
    private static final Pattern DIGIT = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL = Pattern.compile("[^a-zA-Z0-9]");

    private final SxwlSecurityProperties properties;

    public SxwlPasswordValidator(SxwlSecurityProperties properties) {
        this.properties = properties;
    }

    /**
     * 校验密码强度，不满足则抛出异常
     *
     * @param password 明文密码
     * @throws SxwlBusinessException 密码不符合要求时抛出
     */
    public void validate(String password) {
        if (password == null || password.isEmpty()) {
            throw new SxwlBusinessException("密码不能为空");
        }

        int minLen = properties.getPasswordMinLength();
        int maxLen = properties.getPasswordMaxLength();

        if (password.length() < minLen) {
            throw new SxwlBusinessException("密码长度不能少于 " + minLen + " 位");
        }
        if (password.length() > maxLen) {
            throw new SxwlBusinessException("密码长度不能超过 " + maxLen + " 位");
        }

        // 复杂度检查：至少包含 3 种字符类型
        int complexity = 0;
        if (LOWERCASE.matcher(password).find()) complexity++;
        if (UPPERCASE.matcher(password).find()) complexity++;
        if (DIGIT.matcher(password).find()) complexity++;
        if (SPECIAL.matcher(password).find()) complexity++;

        if (complexity < 3) {
            throw new SxwlBusinessException("密码必须包含大写字母、小写字母、数字、特殊字符中的至少 3 种");
        }
    }
}

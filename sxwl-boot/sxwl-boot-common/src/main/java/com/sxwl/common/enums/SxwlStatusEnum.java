package com.sxwl.common.enums;

/**
 * 通用状态码枚举
 *
 * @author shitianyang
 * @date 2026/6/28
 * @since 0.1.0
 */
public enum SxwlStatusEnum {

    SUCCESS(200, "操作成功"),
    UNAUTHORIZED(401, "未认证"),
    FORBIDDEN(403, "无权限"),
    FAIL(10001, "业务校验失败");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 描述
     */
    private final String description;

    /**
     * 枚举构造函数
     *
     * @param code        状态码
     * @param description 描述
     */
    SxwlStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 获取状态码
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 根据状态码查找枚举
     *
     * @param code 状态码
     * @return 对应的枚举实例，找不到返回 null
     */
    public static SxwlStatusEnum fromCode(int code) {
        for (SxwlStatusEnum e : values()) {
            if (e.code == code) {
                return e;
            }
        }
        return null;
    }
}

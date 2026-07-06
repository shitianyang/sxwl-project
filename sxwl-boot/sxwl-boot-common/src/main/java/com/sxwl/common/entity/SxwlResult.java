package com.sxwl.common.entity;

import com.sxwl.common.enums.SxwlStatusEnum;

/**
 * 统一返回体
 *
 * <p>所有 Controller 方法的返回值统一使用此类包装，确保前端收到的响应格式一致。</p>
 *
 * <p><b>响应格式：</b></p>
 * <pre>{@code
 * {
 *   "code": 200,
 *   "message": "操作成功",
 *   "data": { ... }
 * }
 * }</pre>
 *
 * <p><b>使用示例：</b></p>
 * <pre>{@code
 * // 成功返回数据
 * return SxwlResult.success(user);
 *
 * // 成功返回自定义消息
 * return SxwlResult.success("登录成功", loginResp);
 *
 * // 成功无数据返回
 * return SxwlResult.success();
 *
 * // 失败返回
 * return SxwlResult.error("用户名已存在");
 * return SxwlResult.error(10002, "密码强度不足");
 * }</pre>
 *
 * @param <T> 数据类型
 * @author shitianyang
 * @date 2026/6/28
 * @since 0.1.0
 */
public class SxwlResult<T> {

    /**
     * 状态码
     */
    private int code;

    /**
     * 提示消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    public SxwlResult() {
    }

    public SxwlResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 成功返回（无数据）
     *
     * @param <T> 数据类型
     * @return code=200, message="操作成功"
     */
    public static <T> SxwlResult<T> success() {
        return new SxwlResult<>(SxwlStatusEnum.SUCCESS.getCode(), SxwlStatusEnum.SUCCESS.getDescription(), null);
    }

    /**
     * 成功返回（带数据）
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return code=200, message="操作成功"
     */
    public static <T> SxwlResult<T> success(T data) {
        return new SxwlResult<>(SxwlStatusEnum.SUCCESS.getCode(), SxwlStatusEnum.SUCCESS.getDescription(), data);
    }

    /**
     * 成功返回（自定义消息 + 数据）
     *
     * @param message 自定义成功消息
     * @param data    响应数据
     * @param <T>     数据类型
     * @return code=200
     */
    public static <T> SxwlResult<T> success(String message, T data) {
        return new SxwlResult<>(SxwlStatusEnum.SUCCESS.getCode(), message, data);
    }

    /**
     * 失败返回（默认业务错误码 10001）
     *
     * @param <T> 数据类型
     * @return code=10001, message="业务校验失败"
     */
    public static <T> SxwlResult<T> error() {
        return new SxwlResult<>(SxwlStatusEnum.FAIL.getCode(), SxwlStatusEnum.FAIL.getDescription(), null);
    }

    /**
     * 失败返回（默认业务错误码 10001）
     *
     * @param message 错误描述
     * @param <T>     数据类型
     * @return code=10001
     */
    public static <T> SxwlResult<T> error(String message) {
        return new SxwlResult<>(SxwlStatusEnum.FAIL.getCode(), message, null);
    }

    /**
     * 失败返回（自定义错误码）
     *
     * @param code    错误码（10002~19999）
     * @param message 错误描述
     * @param <T>     数据类型
     * @return 自定义 code
     */
    public static <T> SxwlResult<T> error(int code, String message) {
        return new SxwlResult<>(code, message, null);
    }

    /**
     * 失败返回（自定义错误码 + 消息 + 数据）
     *
     * @param code    错误码（10002~19999）
     * @param message 错误描述
     * @param data    响应数据（如"部分成功，3 条失败"时返回失败明细）
     * @param <T>     数据类型
     * @return 自定义 code，带 data
     */
    public static <T> SxwlResult<T> error(int code, String message, T data) {
        return new SxwlResult<>(code, message, data);
    }

    /**
     * 401 未认证
     *
     * @param message 错误描述
     * @param <T>     数据类型
     * @return code=401
     */
    public static <T> SxwlResult<T> unauthorized(String message) {
        return new SxwlResult<>(SxwlStatusEnum.UNAUTHORIZED.getCode(), message, null);
    }

    /**
     * 403 无权限
     *
     * @param message 错误描述
     * @param <T>     数据类型
     * @return code=403
     */
    public static <T> SxwlResult<T> forbidden(String message) {
        return new SxwlResult<>(SxwlStatusEnum.FORBIDDEN.getCode(), message, null);
    }
}

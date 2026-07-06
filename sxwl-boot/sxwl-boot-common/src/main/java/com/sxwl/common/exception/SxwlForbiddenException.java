package com.sxwl.common.exception;

import com.sxwl.common.enums.SxwlStatusEnum;

/**
 * 403 无权限异常
 *
 * <p>当已认证用户尝试访问没有权限的资源时抛出此异常。
 * 由全局异常处理器拦截，返回 HTTP 403 状态码。</p>
 *
 * <p><b>触发场景：</b></p>
 * <ul>
 *   <li>用户角色没有对应菜单的权限标识</li>
 *   <li>Spring Security {@code @PreAuthorize} 注解校验失败</li>
 *   <li>用户尝试操作不属于自己数据权限范围的数据</li>
 * </ul>
 *
 * <p><b>与 {@link SxwlUnauthorizedException} 的区别：</b>
 * 401 是"你是谁我不知道"，403 是"我知道你是谁但你没权限"。</p>
 *
 * @author shitianyang
 * @since 0.1.0
 */
public final class SxwlForbiddenException extends SxwlBusinessException {

    /**
     * 使用默认消息创建 403 异常
     */
    public SxwlForbiddenException() {
        super(SxwlStatusEnum.FORBIDDEN.getCode(), "无访问权限");
    }

    /**
     * 使用自定义消息创建 403 异常
     *
     * @param message 错误描述，如 "没有删除用户的权限"
     */
    public SxwlForbiddenException(String message) {
        super(SxwlStatusEnum.FORBIDDEN.getCode(), message);
    }
}

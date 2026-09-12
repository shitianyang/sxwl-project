package com.sxwl.common.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 统一返回体单元测试
 *
 * @author shitianyang
 * @since 0.1.0
 */
@DisplayName("SxwlResult 测试")
class SxwlResultTest {

    @Test
    @DisplayName("success()：返回 code=200，message=操作成功")
    void testSuccess() {
        SxwlResult<String> result = SxwlResult.success();
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("success(data)：返回 code=200 且 data 不为空")
    void testSuccessWithData() {
        SxwlResult<String> result = SxwlResult.success("测试数据");
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals("测试数据", result.getData());
    }

    @Test
    @DisplayName("success(message, data)：自定义消息")
    void testSuccessWithMessage() {
        SxwlResult<Integer> result = SxwlResult.success("登录成功", 100);
        assertEquals(200, result.getCode());
        assertEquals("登录成功", result.getMessage());
        assertEquals(100, result.getData());
    }

    @Test
    @DisplayName("error()：返回 code=10001，message=业务校验失败")
    void testError() {
        SxwlResult<Void> result = SxwlResult.error();
        assertEquals(10001, result.getCode());
        assertEquals("业务校验失败", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("error(message)：自定义错误消息")
    void testErrorWithMessage() {
        SxwlResult<Void> result = SxwlResult.error("用户名已存在");
        assertEquals(10001, result.getCode());
        assertEquals("用户名已存在", result.getMessage());
    }

    @Test
    @DisplayName("error(code, message)：自定义错误码")
    void testErrorWithCode() {
        SxwlResult<Void> result = SxwlResult.error(10002, "密码强度不足");
        assertEquals(10002, result.getCode());
        assertEquals("密码强度不足", result.getMessage());
    }

    @Test
    @DisplayName("error(code, message, data)：自定义错误码+消息+数据")
    void testErrorWithCodeAndData() {
        String[] details = {"第1条失败", "第2条失败"};
        SxwlResult<String[]> result = SxwlResult.error(10003, "部分成功", details);
        assertEquals(10003, result.getCode());
        assertEquals("部分成功", result.getMessage());
        assertArrayEquals(details, result.getData());
    }

    @Test
    @DisplayName("unauthorized()：返回 code=401")
    void testUnauthorized() {
        SxwlResult<Void> result = SxwlResult.unauthorized("Token 已过期");
        assertEquals(401, result.getCode());
        assertEquals("Token 已过期", result.getMessage());
    }

    @Test
    @DisplayName("forbidden()：返回 code=403")
    void testForbidden() {
        SxwlResult<Void> result = SxwlResult.forbidden("无权访问该资源");
        assertEquals(403, result.getCode());
        assertEquals("无权访问该资源", result.getMessage());
    }
}

package com.sxwl.notice.model.params;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SysNoticePageParams} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("SysNoticePageParams 测试")
class SysNoticePageParamsTest {

    @Test
    @DisplayName("默认分页参数应为 1/10")
    void defaults_shouldBe1And10() {
        SysNoticePageParams params = new SysNoticePageParams();
        assertEquals(1, params.getCurrent());
        assertEquals(10, params.getPageSize());
        assertNull(params.getTitle());
        assertNull(params.getNoticeType());
        assertNull(params.getLevel());
        assertNull(params.getStatus());
    }

    @Test
    @DisplayName("getter/setter 应正常读写")
    void getterSetter_shouldWork() {
        SysNoticePageParams params = new SysNoticePageParams();
        params.setCurrent(2);
        params.setPageSize(20);
        params.setTitle("测试");
        params.setNoticeType("notice");
        params.setLevel("important");
        params.setStatus(1);

        assertEquals(2, params.getCurrent());
        assertEquals(20, params.getPageSize());
        assertEquals("测试", params.getTitle());
        assertEquals("notice", params.getNoticeType());
        assertEquals("important", params.getLevel());
        assertEquals(1, params.getStatus());
    }
}

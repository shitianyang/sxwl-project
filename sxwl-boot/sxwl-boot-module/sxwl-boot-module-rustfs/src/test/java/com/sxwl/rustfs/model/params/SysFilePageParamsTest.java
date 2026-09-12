package com.sxwl.rustfs.model.params;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SysFilePageParams} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("SysFilePageParams 测试")
class SysFilePageParamsTest {
    @Test
    void defaults_shouldBe1And10() {
        SysFilePageParams params = new SysFilePageParams();
        assertEquals(1, params.getCurrent());
        assertEquals(10, params.getPageSize());
        assertNull(params.getFileName());
        assertNull(params.getBusinessType());
        assertNull(params.getStartTime());
        assertNull(params.getEndTime());
    }

    @Test
    void getterSetter_shouldWork() {
        SysFilePageParams params = new SysFilePageParams();
        params.setFileName("test.png");
        params.setBusinessType("avatar");
        params.setStartTime("2026-01-01");
        params.setEndTime("2026-12-31");
        assertEquals("test.png", params.getFileName());
        assertEquals("avatar", params.getBusinessType());
        assertEquals("2026-01-01", params.getStartTime());
        assertEquals("2026-12-31", params.getEndTime());
    }
}

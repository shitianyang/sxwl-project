package com.sxwl.notice.model.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SysNoticeInfo} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("SysNoticeInfo 测试")
class SysNoticeInfoTest {

    @Test
    @DisplayName("getter/setter 应正常读写")
    void getterSetter_shouldWork() {
        LocalDateTime now = LocalDateTime.of(2026, 7, 5, 12, 0);
        SysNoticeInfo entity = new SysNoticeInfo();
        entity.setTitle("测试标题");
        entity.setContent("测试内容");
        entity.setNoticeType("notice");
        entity.setLevel("important");
        entity.setStatus(1);
        entity.setPublishTime(now);
        entity.setExpireTime(now);

        assertEquals("测试标题", entity.getTitle());
        assertEquals("测试内容", entity.getContent());
        assertEquals("notice", entity.getNoticeType());
        assertEquals("important", entity.getLevel());
        assertEquals(1, entity.getStatus());
        assertEquals(now, entity.getPublishTime());
        assertEquals(now, entity.getExpireTime());
    }

    @Test
    @DisplayName("默认值应为 null")
    void defaults_shouldBeNull() {
        SysNoticeInfo entity = new SysNoticeInfo();
        assertNull(entity.getTitle());
        assertNull(entity.getContent());
        assertNull(entity.getNoticeType());
        assertNull(entity.getLevel());
        assertNull(entity.getStatus());
        assertNull(entity.getPublishTime());
        assertNull(entity.getExpireTime());
    }
}

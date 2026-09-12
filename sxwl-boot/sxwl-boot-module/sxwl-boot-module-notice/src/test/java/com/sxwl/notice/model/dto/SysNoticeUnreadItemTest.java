package com.sxwl.notice.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SysNoticeUnreadItem} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("SysNoticeUnreadItem 测试")
class SysNoticeUnreadItemTest {

    @Test
    @DisplayName("getter/setter 应正常读写")
    void getterSetter_shouldWork() {
        LocalDateTime now = LocalDateTime.of(2026, 7, 5, 12, 0);
        SysNoticeUnreadItem item = new SysNoticeUnreadItem();
        item.setId(1L);
        item.setTitle("测试标题");
        item.setNoticeType("notice");
        item.setLevel("important");
        item.setPublishTime(now);
        item.setCreateTime("2026-07-05 12:00:00");
        item.setReadFlag(0);

        assertEquals(1L, item.getId());
        assertEquals("测试标题", item.getTitle());
        assertEquals("notice", item.getNoticeType());
        assertEquals("important", item.getLevel());
        assertEquals(now, item.getPublishTime());
        assertEquals("2026-07-05 12:00:00", item.getCreateTime());
        assertEquals(0, item.getReadFlag());
    }

    @Test
    @DisplayName("默认值应为 null")
    void defaults_shouldBeNull() {
        SysNoticeUnreadItem item = new SysNoticeUnreadItem();
        assertNull(item.getId());
        assertNull(item.getTitle());
        assertNull(item.getNoticeType());
        assertNull(item.getLevel());
        assertNull(item.getPublishTime());
        assertNull(item.getCreateTime());
        assertNull(item.getReadFlag());
    }
}

package com.sxwl.notice.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link SysNoticeDTO} 的单元测试
 *
 * @author shitianyang
 * @date 2026/7/24
 * @since 0.1.0
 */
@DisplayName("SysNoticeDTO 测试")
class SysNoticeDTOTest {

    @Test
    @DisplayName("getter/setter 应正常读写")
    void getterSetter_shouldWork() {
        LocalDateTime now = LocalDateTime.of(2026, 7, 5, 12, 0);
        SysNoticeDTO dto = new SysNoticeDTO();
        dto.setId(1L);
        dto.setTitle("测试标题");
        dto.setContent("测试内容");
        dto.setNoticeType("notice");
        dto.setLevel("important");
        dto.setStatus(1);
        dto.setPublishTime(now);
        dto.setExpireTime(now);
        dto.setCreateTime("2026-07-05 12:00:00");

        assertEquals(1L, dto.getId());
        assertEquals("测试标题", dto.getTitle());
        assertEquals("测试内容", dto.getContent());
        assertEquals("notice", dto.getNoticeType());
        assertEquals("important", dto.getLevel());
        assertEquals(1, dto.getStatus());
        assertEquals(now, dto.getPublishTime());
        assertEquals(now, dto.getExpireTime());
        assertEquals("2026-07-05 12:00:00", dto.getCreateTime());
    }

    @Test
    @DisplayName("默认值应为 null")
    void defaults_shouldBeNull() {
        SysNoticeDTO dto = new SysNoticeDTO();
        assertNull(dto.getId());
        assertNull(dto.getTitle());
        assertNull(dto.getContent());
        assertNull(dto.getNoticeType());
        assertNull(dto.getLevel());
        assertNull(dto.getStatus());
        assertNull(dto.getPublishTime());
        assertNull(dto.getExpireTime());
        assertNull(dto.getCreateTime());
    }
}

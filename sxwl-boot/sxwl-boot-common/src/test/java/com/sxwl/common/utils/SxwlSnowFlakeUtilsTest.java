package com.sxwl.common.utils;

import com.sxwl.common.exception.SxwlBusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 雪花算法工具类单元测试
 *
 * @author shitianyang
 * @since 0.1.0
 */
@DisplayName("SxwlSnowFlakeUtils 工具类测试")
class SxwlSnowFlakeUtilsTest {

    @Test
    @DisplayName("并发生成 10000 个 ID：无重复")
    void testUniqueId() {
        Set<Long> idSet = new HashSet<>();
        int count = 10000;
        for (int i = 0; i < count; i++) {
            long id = SxwlSnowFlakeUtils.nextId();
            assertTrue(idSet.add(id), "ID 不应重复，重复ID=" + id);
        }
        assertEquals(count, idSet.size(), "应生成 " + count + " 个不重复的 ID");
    }

    @Test
    @DisplayName("ID 字符串格式与 long 格式一致")
    void testNextIdStr() {
        long id = SxwlSnowFlakeUtils.nextId();
        String idStr = SxwlSnowFlakeUtils.nextIdStr();
        assertNotNull(idStr);
        assertTrue(Long.parseLong(idStr) > 0);
    }

    @Test
    @DisplayName("解析雪花 ID：时间戳 >= 起始时间戳")
    void testParseIdTimestamp() {
        long id = SxwlSnowFlakeUtils.nextId();
        SxwlSnowFlakeUtils.SnowFlakeIdMeta meta = SxwlSnowFlakeUtils.parseId(id);
        assertTrue(meta.getTimestamp() >= SxwlSnowFlakeUtils.DEFAULT_EPOCH,
                "解析出的时间戳应 >= 起始时间戳");
    }

    @Test
    @DisplayName("解析雪花 ID：各字段范围有效")
    void testParseIdFields() {
        long id = SxwlSnowFlakeUtils.nextId();
        SxwlSnowFlakeUtils.SnowFlakeIdMeta meta = SxwlSnowFlakeUtils.parseId(id);
        assertTrue(meta.getDataCenterId() >= 0 && meta.getDataCenterId() <= 31,
                "数据中心 ID 应在 0~31 范围");
        assertTrue(meta.getWorkerId() >= 0 && meta.getWorkerId() <= 31,
                "机器节点 ID 应在 0~31 范围");
        assertTrue(meta.getSequence() >= 0 && meta.getSequence() <= 4095,
                "序列号应在 0~4095 范围");
    }

    @Test
    @DisplayName("自定义生成器：指定的 dataCenterId 和 workerId 正确生效")
    void testCustomGenerator() {
        long dataCenterId = 3;
        long workerId = 7;
        SxwlSnowFlakeUtils.SnowFlakeGenerator generator =
                SxwlSnowFlakeUtils.createGenerator(dataCenterId, workerId);
        assertEquals(dataCenterId, generator.getDataCenterId());
        assertEquals(workerId, generator.getWorkerId());
    }

    @Test
    @DisplayName("自定义生成器：生成的 ID 编码了正确的 dataCenterId 和 workerId")
    void testCustomGeneratorIdFields() {
        long dataCenterId = 5;
        long workerId = 10;
        SxwlSnowFlakeUtils.SnowFlakeGenerator generator =
                SxwlSnowFlakeUtils.createGenerator(dataCenterId, workerId);
        long id = generator.nextId();
        SxwlSnowFlakeUtils.SnowFlakeIdMeta meta = SxwlSnowFlakeUtils.parseId(id);
        assertEquals(dataCenterId, meta.getDataCenterId());
        assertEquals(workerId, meta.getWorkerId());
    }

    @Test
    @DisplayName("dataCenterId 越界：应抛出异常")
    void testInvalidDataCenterId() {
        assertThrows(SxwlBusinessException.class,
                () -> SxwlSnowFlakeUtils.createGenerator(32, 0),
                "dataCenterId > 31 应抛出异常");
        assertThrows(SxwlBusinessException.class,
                () -> SxwlSnowFlakeUtils.createGenerator(-1, 0),
                "dataCenterId < 0 应抛出异常");
    }

    @Test
    @DisplayName("workerId 越界：应抛出异常")
    void testInvalidWorkerId() {
        assertThrows(SxwlBusinessException.class,
                () -> SxwlSnowFlakeUtils.createGenerator(0, 32),
                "workerId > 31 应抛出异常");
    }

    @Test
    @DisplayName("解析错误的 ID（<=0）：应抛出异常")
    void testParseInvalidId() {
        assertThrows(SxwlBusinessException.class,
                () -> SxwlSnowFlakeUtils.parseId(0),
                "ID <= 0 应抛出异常");
        assertThrows(SxwlBusinessException.class,
                () -> SxwlSnowFlakeUtils.parseId(-1L),
                "负数 ID 应抛出异常");
    }

    @Test
    @DisplayName("SnowFlakeIdMeta equals/hashCode 正确实现")
    void testIdMetaEquals() {
        SxwlSnowFlakeUtils.SnowFlakeIdMeta meta1 = SxwlSnowFlakeUtils.parseId(SxwlSnowFlakeUtils.nextId());
        SxwlSnowFlakeUtils.SnowFlakeIdMeta meta2 = SxwlSnowFlakeUtils.parseId(SxwlSnowFlakeUtils.nextId());
        // 不同 ID 解析出的元数据不同
        assertNotEquals(meta1, meta2);
        // 同一 ID 解析两次应相等
        long id = SxwlSnowFlakeUtils.nextId();
        SxwlSnowFlakeUtils.SnowFlakeIdMeta meta3 = SxwlSnowFlakeUtils.parseId(id);
        SxwlSnowFlakeUtils.SnowFlakeIdMeta meta4 = SxwlSnowFlakeUtils.parseId(id);
        assertEquals(meta3, meta4);
        assertEquals(meta3.hashCode(), meta4.hashCode());
    }
}

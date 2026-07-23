package com.sxwl.system.model.dto;

/**
 * 缓存 Key 详情 DTO
 *
 * <p>包含单个 Key 的数据类型、Value 和 TTL。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
public class SysCacheKeyDetailDTO {

    /** 完整 Key 名称 */
    private String key;

    /** 数据类型：string / hash / set / zset / list */
    private String type;

    /** Value（String / Map / Set 等，序列化为 JSON 展示） */
    private Object value;

    /** 剩余有效期（秒），-1 表示永不过期 */
    private Long ttl;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Long getTtl() {
        return ttl;
    }

    public void setTtl(Long ttl) {
        this.ttl = ttl;
    }
}

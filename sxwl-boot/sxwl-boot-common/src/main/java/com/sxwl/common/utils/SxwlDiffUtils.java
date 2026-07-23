package com.sxwl.common.utils;

import com.sxwl.common.annotation.SxwlExcel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 字段级变更差异对比工具
 *
 * <p>对比两个同类型对象的字段值差异，输出结构化的变更列表。
 * 支持 {@link SxwlExcel} 注解的 {@code name()} 作为字段中文名，无注解时回退为字段名。</p>
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * SysUser oldUser = userMapper.getUserById(id);
 * sysUserMapper.updateUser(newUser);
 * String diffJson = SxwlDiffUtils.diff(oldUser, newUser);
 * // diffJson → [{"field":"用户状态","oldValue":"启用","newValue":"禁用"}]
 * }</pre>
 *
 * <h3>变更日志集成</h3>
 * <pre>{@code
 * // 在 Controller 中通过 ThreadLocal 传递
 * SxwlDiffUtils.setContextDiff(diffJson);
 *
 * // SxwlLogAspect 会自动从上下文获取 diff 并设置到事件中
 * }</pre>
 *
 * @author shitianyang
 * @since 0.1.0
 */
public class SxwlDiffUtils {

    private static final Logger log = LoggerFactory.getLogger(SxwlDiffUtils.class);

    /** 当前线程的 diff 上下文（用于 AOP 自动采集） */
    private static final ThreadLocal<String> CONTEXT_DIFF = new ThreadLocal<>();

    private SxwlDiffUtils() {
    }

    // ==================== ThreadLocal 上下文 ====================

    /**
     * 设置当前线程的变更差异 JSON（供操作日志 AOP 自动采集）
     *
     * @param diffJson 变更差异 JSON 字符串
     */
    public static void setContextDiff(String diffJson) {
        CONTEXT_DIFF.set(diffJson);
    }

    /**
     * 获取并清除当前线程的变更差异 JSON
     *
     * @return 变更差异 JSON，无变更时返回 null
     */
    public static String getAndClearContextDiff() {
        String diff = CONTEXT_DIFF.get();
        CONTEXT_DIFF.remove();
        return diff;
    }

    // ==================== 核心 Diff 方法 ====================

    /**
     * 对比两个对象的字段值差异
     *
     * <p>返回 JSON 数组字符串，每个元素包含 field（字段名）、oldValue（旧值）、newValue（新值）。</p>
     *
     * @param oldObj 旧对象（可为 null，表示新建场景）
     * @param newObj 新对象
     * @param <T>    对象类型
     * @return JSON 字符串，如 [{@code {"field":"用户名","oldValue":"zhangsan","newValue":"lisi"}}]，无差异时返回 null
     */
    public static <T> String diff(T oldObj, T newObj) {
        if (newObj == null) return null;
        if (oldObj == null) {
            // 新建场景：把所有非 null 字段视为新增
            return buildCreatedDiff(newObj);
        }

        List<ChangeItem> changes = new ArrayList<>();
        Class<?> clazz = oldObj.getClass();

        // 收集所有字段（包括父类）
        List<Field> allFields = getAllFields(clazz);

        for (Field field : allFields) {
            // 跳过静态字段
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) continue;

            field.setAccessible(true);
            try {
                Object oldValue = field.get(oldObj);
                Object newValue = field.get(newObj);

                if (!Objects.equals(oldValue, newValue)) {
                    String fieldName = getFieldDisplayName(field);
                    changes.add(new ChangeItem(fieldName,
                            formatValue(oldValue),
                            formatValue(newValue)));
                }
            } catch (IllegalAccessException e) {
                log.debug("字段对比跳过: {}.{}", clazz.getSimpleName(), field.getName());
            }
        }

        if (changes.isEmpty()) return null;
        return toJsonArray(changes);
    }

    // ==================== 内部方法 ====================

    /** 新建场景：将所有非 null 字段作为变更输出 */
    private static <T> String buildCreatedDiff(T newObj) {
        List<ChangeItem> changes = new ArrayList<>();
        for (Field field : getAllFields(newObj.getClass())) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) continue;
            field.setAccessible(true);
            try {
                Object value = field.get(newObj);
                if (value != null) {
                    String fieldName = getFieldDisplayName(field);
                    changes.add(new ChangeItem(fieldName, null, formatValue(value)));
                }
            } catch (IllegalAccessException e) {
                // skip
            }
        }
        if (changes.isEmpty()) return null;
        return toJsonArray(changes);
    }

    /** 获取类的所有字段（包括父类） */
    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            Collections.addAll(fields, current.getDeclaredFields());
            current = current.getSuperclass();
        }
        return fields;
    }

    /** 获取字段的中文显示名（优先 @SxwlExcel.name()） */
    private static String getFieldDisplayName(Field field) {
        SxwlExcel annotation = field.getAnnotation(SxwlExcel.class);
        if (annotation != null && !annotation.name().isEmpty()) {
            return annotation.name();
        }
        return field.getName();
    }

    /** 格式化值为字符串 */
    private static String formatValue(Object value) {
        if (value == null) return null;
        if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (value instanceof LocalDate) {
            return ((LocalDate) value).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        if (value instanceof Date) {
            return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value);
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).stripTrailingZeros().toPlainString();
        }
        if (value instanceof Boolean) {
            return Boolean.TRUE.equals(value) ? "是" : "否";
        }
        if (value instanceof Enum) {
            return ((Enum<?>) value).name();
        }
        return value.toString();
    }

    /** 将变更列表转为 JSON 数组字符串 */
    private static String toJsonArray(List<ChangeItem> changes) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < changes.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(changes.get(i).toJson());
        }
        sb.append("]");
        return sb.toString();
    }

    // ==================== 内部类 ====================

    /** 单条字段变更记录 */
    private static class ChangeItem {
        final String field;
        final String oldValue;
        final String newValue;

        ChangeItem(String field, String oldValue, String newValue) {
            this.field = field;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        String toJson() {
            return "{\"field\":\"" + escape(field) + "\""
                    + ",\"oldValue\":" + (oldValue != null ? "\"" + escape(oldValue) + "\"" : "null")
                    + ",\"newValue\":" + (newValue != null ? "\"" + escape(newValue) + "\"" : "null")
                    + "}";
        }

        private String escape(String s) {
            if (s == null) return null;
            return s.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
        }
    }
}

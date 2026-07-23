package com.sxwl.common.annotation;

import java.lang.annotation.*;

/**
 * Excel 导出/导入字段映射注解
 *
 * <p>标注在 DTO/Entity 字段上，指定 Excel 列名称和顺序。
 * 配合 {@link com.sxwl.common.utils.SxwlExcelUtils} 实现通用导入导出。</p>
 *
 * <pre>{@code
 * public class SysUserExportVO {
 *     @SxwlExcel(name = "用户名", order = 1)
 *     private String username;
 *
 *     @SxwlExcel(name = "手机号", order = 2)
 *     private String phone;
 *
 *     @SxwlExcel(name = "邮箱", order = 3)
 *     private String email;
 * }
 * }</pre>
 *
 * @author shitianyang
 * @since 0.1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SxwlExcel {

    /**
     * Excel 列名（表头）
     *
     * @return 列名
     */
    String name();

    /**
     * 列顺序（升序排列），相同 order 按字段名排序
     *
     * @return 顺序值，默认 0
     */
    int order() default 0;

    /**
     * 日期格式（仅对 Date/LocalDateTime 类型生效）
     *
     * @return 日期格式，默认 yyyy-MM-dd HH:mm:ss
     */
    String dateFormat() default "yyyy-MM-dd HH:mm:ss";
}

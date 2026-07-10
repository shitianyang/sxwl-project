package com.sxwl.common.annotation;

import java.lang.annotation.*;

/**
 * 数据权限注解
 *
 * <p>标记在 Mapper 方法上，表示该查询需要受数据权限控制。
 * 被 {@code SxwlDataScopeInterceptor} 识别，自动拼接 {@code create_org IN (...)} 条件。
 * 未标记此注解的 Mapper 方法不受数据权限拦截。</p>
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * // 单表查询，直接用 create_org
 * @SxwlDataScope
 * List<SysUserDTO> getUserPageByParams(SysUserPageParams params);
 *
 * // 多表 JOIN，指定列别名，生成 u.create_org
 * @SxwlDataScope(columnAlias = "u")
 * List<SysUserDTO> getUserPageByParams(SysUserPageParams params);
 * }</pre>
 *
 * @author shitianyang
 * @date 2026/7/6
 * @since 0.1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SxwlDataScope {

    /**
     * 数据权限列别名
     *
     * <p>当 Mapper XML 中使用了表别名（如 {@code u.create_org}）时，
     * 通过此属性指定别名前缀，拦截器会生成 {@code u.create_org IN (...)} 代替 {@code create_org IN (...)}。</p>
     *
     * @return 列别名，为空时直接用 create_org
     */
    String columnAlias() default "";
}

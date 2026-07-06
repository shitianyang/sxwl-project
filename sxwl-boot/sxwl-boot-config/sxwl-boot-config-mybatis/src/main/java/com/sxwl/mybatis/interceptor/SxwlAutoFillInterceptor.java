package com.sxwl.mybatis.interceptor;

import com.sxwl.common.entity.SxwlBasicField;
import com.sxwl.common.utils.SxwlSnowFlakeUtils;
import com.sxwl.common.utils.SxwlPrincipalUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * MyBatis 自动填充拦截器
 *
 * <p>在 INSERT / UPDATE 时自动填充通用审计字段，避免每个 Service 层手动 set。</p>
 *
 * <h3>填充规则</h3>
 * <table>
 *   <tr><th>操作</th><th>填充字段</th><th>值来源</th></tr>
 *   <tr><td>INSERT</td><td>id</td><td>{@link SxwlSnowFlakeUtils#nextId()}（仅当为 null 时）</td></tr>
 *   <tr><td>INSERT</td><td>create_by</td><td>当前登录用户 ID</td></tr>
 *   <tr><td>INSERT</td><td>create_org</td><td>当前用户所属组织 ID</td></tr>
 *   <tr><td>INSERT</td><td>create_time</td><td>LocalDateTime.now()</td></tr>
 *   <tr><td>INSERT</td><td>delete_flag</td><td>0</td></tr>
 *   <tr><td>UPDATE</td><td>update_by</td><td>当前登录用户 ID</td></tr>
 *   <tr><td>UPDATE</td><td>update_time</td><td>LocalDateTime.now()</td></tr>
 * </table>
 *
 * <p>仅拦截参数中包含 {@link SxwlBasicField} 子类的 Mapper 方法，其他参数原样透传。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
public class SxwlAutoFillInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        if (args.length < 2) {
            return invocation.proceed();
        }

        MappedStatement ms = (MappedStatement) args[0];
        Object parameter = args[1];

        if (!(parameter instanceof SxwlBasicField entity)) {
            return invocation.proceed();
        }

        SqlCommandType sqlType = ms.getSqlCommandType();
        switch (sqlType) {
            case INSERT -> fillInsert(entity);
            case UPDATE -> fillUpdate(entity);
            default -> { /* SELECT/DELETE 不处理 */ }
        }

        return invocation.proceed();
    }

    /**
     * INSERT 自动填充
     */
    private void fillInsert(SxwlBasicField entity) {
        LocalDateTime now = LocalDateTime.now();

        if (entity.getId() == null) {
            entity.setId(SxwlSnowFlakeUtils.nextId());
        }
        entity.setCreateTime(now);
        entity.setDeleteFlag(SxwlBasicField.DELETE_FLAG_NORMAL);

        // 从 SecurityContext 获取当前用户信息
        SxwlPrincipalUtils.getCurrentPrincipal().ifPresent(principal -> {
            if (entity.getCreateBy() == null) {
                entity.setCreateBy(principal.getUserId());
            }
            if (entity.getCreateOrg() == null) {
                entity.setCreateOrg(principal.getOrgId());
            }
        });
    }

    /**
     * UPDATE 自动填充
     */
    private void fillUpdate(SxwlBasicField entity) {
        entity.setUpdateTime(LocalDateTime.now());

        SxwlPrincipalUtils.getCurrentPrincipal().ifPresent(principal -> {
            if (entity.getUpdateBy() == null) {
                entity.setUpdateBy(principal.getUserId());
            }
        });
    }
}

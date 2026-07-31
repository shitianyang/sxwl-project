package com.sxwl.common.constant;

/**
 * 权限标识常量
 *
 * <p>定义系统中所有权限编码为 {@code public static final String} 常量，
 * 与 {@code sys_menu_info.perms} 中的记录一一对应。
 * 按模块分内部类组织，避免枚举膨胀，方便 Controller 中 {@code @PreAuthorize} 引用。</p>
 *
 * <h3>使用示例</h3>
 * <pre>{@code
 * // Controller 方法
 * @PreAuthorize("@sxwlPermission.hasPerm('" + SxwlPermConstant.System.User.LIST + "')")
 * // 等价于硬编码 @PreAuthorize("@sxwlPermission.hasPerm('system:user:list')")
 * }</pre>
 *
 * @author shitianyang
 * @date 2026/8/1
 * @since 0.2.0
 */
public final class SxwlPermConstant {

    private SxwlPermConstant() {
        // 工具类，禁止实例化
    }

    // ==================== 系统管理 ====================
    public static final class System {
        private System() {}

        /** 用户管理-列表 */
        public static final String USER_LIST = "system:user:list";
        /** 用户管理-查询 */
        public static final String USER_QUERY = "system:user:query";
        /** 用户管理-新增 */
        public static final String USER_ADD = "system:user:add";
        /** 用户管理-编辑 */
        public static final String USER_EDIT = "system:user:edit";
        /** 用户管理-删除 */
        public static final String USER_DELETE = "system:user:delete";

        /** 角色管理-列表 */
        public static final String ROLE_LIST = "system:role:list";
        /** 角色管理-查询 */
        public static final String ROLE_QUERY = "system:role:query";
        /** 角色管理-新增 */
        public static final String ROLE_ADD = "system:role:add";
        /** 角色管理-编辑 */
        public static final String ROLE_EDIT = "system:role:edit";
        /** 角色管理-删除 */
        public static final String ROLE_DELETE = "system:role:delete";
        /** 角色管理-分配权限 */
        public static final String ROLE_GRANT = "system:role:grant";

        /** 菜单管理-列表 */
        public static final String MENU_LIST = "system:menu:list";
        /** 菜单管理-查询 */
        public static final String MENU_QUERY = "system:menu:query";
        /** 菜单管理-新增 */
        public static final String MENU_ADD = "system:menu:add";
        /** 菜单管理-编辑 */
        public static final String MENU_EDIT = "system:menu:edit";
        /** 菜单管理-删除 */
        public static final String MENU_DELETE = "system:menu:delete";

        /** 组织管理-列表 */
        public static final String ORG_LIST = "system:organization:list";
        /** 组织管理-查询 */
        public static final String ORG_QUERY = "system:organization:query";
        /** 组织管理-新增 */
        public static final String ORG_ADD = "system:organization:add";
        /** 组织管理-编辑 */
        public static final String ORG_EDIT = "system:organization:edit";
        /** 组织管理-删除 */
        public static final String ORG_DELETE = "system:organization:delete";

        /** 岗位管理-列表 */
        public static final String POSITION_LIST = "system:position:list";
        /** 岗位管理-查询 */
        public static final String POSITION_QUERY = "system:position:query";
        /** 岗位管理-新增 */
        public static final String POSITION_ADD = "system:position:add";
        /** 岗位管理-编辑 */
        public static final String POSITION_EDIT = "system:position:edit";
        /** 岗位管理-删除 */
        public static final String POSITION_DELETE = "system:position:delete";

        /** 字典管理-列表 */
        public static final String DICT_LIST = "system:dict:list";
        /** 字典管理-查询 */
        public static final String DICT_QUERY = "system:dict:query";
        /** 字典管理-新增 */
        public static final String DICT_ADD = "system:dict:add";
        /** 字典管理-编辑 */
        public static final String DICT_EDIT = "system:dict:edit";
        /** 字典管理-删除 */
        public static final String DICT_DELETE = "system:dict:delete";

        /** 参数管理-列表 */
        public static final String CONFIG_LIST = "system:config:list";
        /** 参数管理-查询 */
        public static final String CONFIG_QUERY = "system:config:query";
        /** 参数管理-新增 */
        public static final String CONFIG_ADD = "system:config:add";
        /** 参数管理-编辑 */
        public static final String CONFIG_EDIT = "system:config:edit";
        /** 参数管理-删除 */
        public static final String CONFIG_DELETE = "system:config:delete";

        /** 通知公告-列表 */
        public static final String NOTICE_LIST = "system:notice:list";
        /** 通知公告-查询 */
        public static final String NOTICE_QUERY = "system:notice:query";
        /** 通知公告-新增 */
        public static final String NOTICE_ADD = "system:notice:add";
        /** 通知公告-编辑 */
        public static final String NOTICE_EDIT = "system:notice:edit";
        /** 通知公告-删除 */
        public static final String NOTICE_DELETE = "system:notice:delete";
        /** 通知公告-发布 */
        public static final String NOTICE_PUBLISH = "system:notice:publish";
        /** 通知公告-撤回 */
        public static final String NOTICE_REVOKE = "system:notice:revoke";

        /** 操作日志-列表 */
        public static final String LOG_VIEW = "system:log:view";

        /** 登录日志-列表 */
        public static final String LOGIN_LOG_VIEW = "system:loginlog:view";

        /** 文件管理-列表 */
        public static final String FILE_VIEW = "system:file:view";
        /** 文件管理-上传 */
        public static final String FILE_UPLOAD = "system:file:upload";
        /** 文件管理-下载 */
        public static final String FILE_DOWNLOAD = "system:file:download";
        /** 文件管理-删除 */
        public static final String FILE_DELETE = "system:file:delete";
    }

    // ==================== 监控运维 ====================
    public static final class Monitor {
        private Monitor() {}

        /** 系统监控-服务器监控 */
        public static final String SERVER_VIEW = "monitor:server:view";

        /** 在线用户-列表 */
        public static final String ONLINE_USER_VIEW = "monitor:onlineuser:view";
        /** 在线用户-强制下线 */
        public static final String ONLINE_USER_FORCE_LOGOUT = "monitor:onlineuser:forceLogout";

        /** 缓存管理-列表 */
        public static final String CACHE_VIEW = "monitor:cache:view";
        /** 缓存管理-清理 */
        public static final String CACHE_CLEAR = "monitor:cache:clear";

        /** 定时任务-查询 */
        public static final String JOB_QUERY = "monitor:job:query";
        /** 任务日志-查询 */
        public static final String JOB_LOG_QUERY = "monitor:joblog:query";
        /** 任务日志-列表 */
        public static final String JOB_LOG_LIST = "monitor:joblog:list";
        /** 任务日志-删除 */
        public static final String JOB_LOG_DELETE = "monitor:joblog:delete";
        /** 任务日志-清理 */
        public static final String JOB_LOG_CLEAN = "monitor:joblog:clean";
        /** 定时任务-列表 */
        public static final String JOB_LIST = "monitor:job:list";
        /** 定时任务-新增 */
        public static final String JOB_ADD = "monitor:job:add";
        /** 定时任务-编辑 */
        public static final String JOB_EDIT = "monitor:job:edit";
        /** 定时任务-删除 */
        public static final String JOB_DELETE = "monitor:job:delete";
        /** 定时任务-暂停 */
        public static final String JOB_PAUSE = "monitor:job:pause";
        /** 定时任务-恢复 */
        public static final String JOB_RESUME = "monitor:job:resume";
        /** 定时任务-执行一次 */
        public static final String JOB_RUN = "monitor:job:run";
        /** 定时任务-清理日志 */
        public static final String JOB_CLEAN = "monitor:job:clean";

        /** 数据备份-执行备份 */
        public static final String BACKUP_BACKUP = "monitor:backup:backup";
        /** 数据备份-备份列表 */
        public static final String BACKUP_VIEW = "monitor:backup:view";
        /** 数据备份-恢复备份 */
        public static final String BACKUP_RESTORE = "monitor:backup:restore";
        /** 数据备份-删除备份 */
        public static final String BACKUP_DELETE = "monitor:backup:delete";
    }

    // ==================== 代码生成 ====================
    public static final class Codegen {
        private Codegen() {}

        /** 代码生成-列表 */
        public static final String TABLE_LIST = "codegen:table:list";
        /** 代码生成-查询 */
        public static final String TABLE_QUERY = "codegen:table:query";
        /** 代码生成-新增配置 */
        public static final String TABLE_ADD = "codegen:table:add";
        /** 代码生成-编辑配置 */
        public static final String TABLE_EDIT = "codegen:table:edit";
        /** 代码生成-删除配置 */
        public static final String TABLE_DELETE = "codegen:table:delete";
        /** 代码生成-预览 */
        public static final String CODEGEN_PREVIEW = "codegen:codegen:preview";
        /** 代码生成-生成 */
        public static final String CODEGEN_GENERATE = "codegen:codegen:generate";
    }
}

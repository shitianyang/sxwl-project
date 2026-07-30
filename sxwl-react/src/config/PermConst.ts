/**
 * 权限标识常量
 *
 * 与后端 SxwlPermConstant.java 一一对应，配合前端 SxwlPermissionButton 使用。
 * 按模块分组导出，方便页面中权限判断引用。
 *
 * @example
 * ```tsx
 * import { System } from '@/config/PermConst';
 * // 在页面中
 * <SxwlPermissionButton perms={[System.USER_ADD]} type="primary">新增用户</SxwlPermissionButton>
 * ```
 */

// ==================== 系统管理 ====================
export const System = {
  // 用户管理
  USER_LIST: 'system:user:list',
  USER_QUERY: 'system:user:query',
  USER_ADD: 'system:user:add',
  USER_EDIT: 'system:user:edit',
  USER_DELETE: 'system:user:delete',

  // 角色管理
  ROLE_LIST: 'system:role:list',
  ROLE_QUERY: 'system:role:query',
  ROLE_ADD: 'system:role:add',
  ROLE_EDIT: 'system:role:edit',
  ROLE_DELETE: 'system:role:delete',
  ROLE_GRANT: 'system:role:grant',

  // 菜单管理
  MENU_LIST: 'system:menu:list',
  MENU_QUERY: 'system:menu:query',
  MENU_ADD: 'system:menu:add',
  MENU_EDIT: 'system:menu:edit',
  MENU_DELETE: 'system:menu:delete',

  // 组织管理
  ORG_LIST: 'system:organization:list',
  ORG_QUERY: 'system:organization:query',
  ORG_ADD: 'system:organization:add',
  ORG_EDIT: 'system:organization:edit',
  ORG_DELETE: 'system:organization:delete',

  // 岗位管理
  POSITION_LIST: 'system:position:list',
  POSITION_QUERY: 'system:position:query',
  POSITION_ADD: 'system:position:add',
  POSITION_EDIT: 'system:position:edit',
  POSITION_DELETE: 'system:position:delete',

  // 字典管理
  DICT_LIST: 'system:dict:list',
  DICT_QUERY: 'system:dict:query',
  DICT_ADD: 'system:dict:add',
  DICT_EDIT: 'system:dict:edit',
  DICT_DELETE: 'system:dict:delete',

  // 参数管理
  CONFIG_LIST: 'system:config:list',
  CONFIG_QUERY: 'system:config:query',
  CONFIG_ADD: 'system:config:add',
  CONFIG_EDIT: 'system:config:edit',
  CONFIG_DELETE: 'system:config:delete',

  // 通知公告
  NOTICE_LIST: 'system:notice:list',
  NOTICE_QUERY: 'system:notice:query',
  NOTICE_ADD: 'system:notice:add',
  NOTICE_EDIT: 'system:notice:edit',
  NOTICE_DELETE: 'system:notice:delete',
  NOTICE_PUBLISH: 'system:notice:publish',
  NOTICE_REVOKE: 'system:notice:revoke',

  // 操作日志
  LOG_VIEW: 'system:log:view',

  // 登录日志
  LOGIN_LOG_VIEW: 'system:loginlog:view',

  // 文件管理
  FILE_VIEW: 'system:file:view',
  FILE_UPLOAD: 'system:file:upload',
  FILE_DOWNLOAD: 'system:file:download',
  FILE_DELETE: 'system:file:delete',
} as const;

// ==================== 监控运维 ====================
export const Monitor = {
  // 系统监控
  SERVER_VIEW: 'monitor:server:view',

  // 在线用户
  ONLINE_USER_VIEW: 'monitor:onlineuser:view',
  ONLINE_USER_FORCE_LOGOUT: 'monitor:onlineuser:forceLogout',

  // 缓存管理
  CACHE_VIEW: 'monitor:cache:view',
  CACHE_CLEAR: 'monitor:cache:clear',

  // 定时任务
  JOB_QUERY: 'monitor:job:query',
  JOB_LIST: 'monitor:job:list',
  JOB_ADD: 'monitor:job:add',
  JOB_EDIT: 'monitor:job:edit',
  JOB_DELETE: 'monitor:job:delete',
  JOB_PAUSE: 'monitor:job:pause',
  JOB_RESUME: 'monitor:job:resume',
  JOB_RUN: 'monitor:job:run',

  // 任务日志
  JOB_LOG_QUERY: 'monitor:joblog:query',
  JOB_LOG_LIST: 'monitor:joblog:list',
  JOB_LOG_DELETE: 'monitor:joblog:delete',
  JOB_LOG_CLEAN: 'monitor:joblog:clean',

  // 数据备份
  BACKUP_BACKUP: 'monitor:backup:backup',
  BACKUP_VIEW: 'monitor:backup:view',
  BACKUP_RESTORE: 'monitor:backup:restore',
  BACKUP_DELETE: 'monitor:backup:delete',
} as const;

// ==================== 代码生成 ====================
export const Codegen = {
  TABLE_LIST: 'codegen:table:list',
  TABLE_QUERY: 'codegen:table:query',
  TABLE_ADD: 'codegen:table:add',
  TABLE_EDIT: 'codegen:table:edit',
  TABLE_DELETE: 'codegen:table:delete',
  CODEGEN_PREVIEW: 'codegen:codegen:preview',
  CODEGEN_GENERATE: 'codegen:codegen:generate',
} as const;

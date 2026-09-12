// ============================================
// 日志管理 API
// ============================================

import { http } from '@/api/http';

/** 日志列表项 */
export interface LogItem {
  /** 日志 ID */
  id: number;
  /** 日志类型：1=登录 2=操作 3=异常 4=安全 */
  logType: number;
  /** 模块标题，如：用户管理 */
  title: string;
  /** 操作描述，如：删除用户[zhangsan] */
  description: string;
  /** 调用方法，如 SysUserController.delete() */
  method: string;
  /** 请求URL，如 /sxwl-api/sys/user/1 */
  requestUrl: string;
  /** HTTP方法：GET/POST/PUT/DELETE */
  requestMethod: string;
  /** 请求参数（JSON） */
  requestParam: string;
  /** 响应结果（JSON） */
  responseResult: string;
  /** 操作人IP */
  operateIp: string;
  /** 操作地点（IP反查） */
  operateLocation: string;
  /** 操作人ID */
  userId: number;
  /** 操作人账号（冗余） */
  userName: string;
  /** 执行耗时（毫秒） */
  executeTime: number;
  /** 错误信息（异常日志用） */
  errorMsg: string;
  /** 操作状态：0=失败 1=成功 */
  status: number;
  /** 链路追踪ID */
  traceId: string;
  /** 用户代理（原始 User-Agent） */
  userAgent: string;
  /** 操作系统 */
  browser: string;
  /** 浏览器 */
  os: string;
  /** 字段级变更差异 JSON */
  diff: string;
  /** 创建时间（仅列表返回时填充） */
  createTime: string;
}

/** 日志查询参数 */
export interface LogQuery {
  /** 日志类型：1=登录 2=操作 3=异常 4=安全 */
  logType?: number;
  /** 模块标题（模糊匹配） */
  title?: string;
  /** 操作人账号（精确匹配） */
  userName?: string;
  /** 操作状态：0=失败 1=成功 */
  status?: number;
  /** 开始时间（yyyy-MM-dd HH:mm:ss） */
  startTime?: string;
  /** 结束时间（yyyy-MM-dd HH:mm:ss） */
  endTime?: string;
  /** 当前页码 */
  current: number;
  /** 每页条数 */
  pageSize: number;
}

/** 分页响应 */
export interface PageInfo<T> {
  list: T[];
  total: number;
  pageNum: number;
  pageSize: number;
  pages: number;
}

/** 查询日志列表 */
export function getLogPageByParams(params: LogQuery) {
  return http.get<PageInfo<LogItem>>('/system/log/page', params as unknown as Record<string, unknown>);
}

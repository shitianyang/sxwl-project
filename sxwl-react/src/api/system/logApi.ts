// ============================================
// 日志管理 API
// ============================================

import { http } from '@/api/http';

/** PageInfo 分页响应（与后端 com.github.pagehelper.PageInfo 对齐） */
export interface PageInfo<T> {
  list: T[];
  total: number;
  pageNum: number;
  pageSize: number;
  pages: number;
}

/** 日志列表项 */
export interface SysLogItem {
  id: number;
  logType: number;   // 1=登录 2=操作 3=异常 4=安全
  title: string;
  description: string;
  method: string;
  requestUrl: string;
  requestMethod: string;
  requestParam: string;
  responseResult: string;
  operateIp: string;
  operateLocation: string;
  userId: number;
  userName: string;
  executeTime: number;   // 毫秒
  errorMsg: string;
  status: number;        // 0=失败 1=成功
  traceId: string;
  userAgent: string;
  browser: string;
  os: string;
  createTime: string;
}

/** 日志查询参数 */
export interface SysLogPageParams {
  logType: number;       // 必传
  title?: string;
  userName?: string;
  status?: number;
  startTime?: string;
  endTime?: string;
  current: number;
  pageSize: number;
}

/** 分页查询日志列表 */
export function getLogPageByParams(params: SysLogPageParams) {
  return http.get<PageInfo<SysLogItem>>('/system/log/page', params as unknown as Record<string, unknown>);
}

// ============================================
// 定时任务日志 API
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

/** 任务日志 DTO */
export interface SysJobLogItem {
  id: number;
  jobId: number;
  jobName: string;
  jobGroup: string;
  className: string;
  methodName: string;
  methodParams?: string;
  cronExpression: string;
  status: number;
  executeTime?: number;
  errorMsg?: string;
  fireTime?: string;
  createTime: string;
}

/** 任务日志查询参数 */
export interface SysJobLogQuery {
  jobName?: string;
  jobGroup?: string;
  status?: number;
  current: number;
  pageSize: number;
}

/** 查询日志详情 */
export function getLogById(id: number) {
  return http.get<SysJobLogItem>('/monitor/job/log/' + id);
}

/** 分页查询日志列表 */
export function getLogPageByParams(params: SysJobLogQuery) {
  return http.get<PageInfo<SysJobLogItem>>('/monitor/job/log/page', params as unknown as Record<string, unknown>);
}

/** 删除日志 */
export function deleteLogById(id: number) {
  return http.deleteReq<null>('/monitor/job/log/' + id);
}

/** 清理指定天数前的日志 */
export function cleanLogBefore(days: number) {
  return http.deleteReq<null>('/monitor/job/log/clean', { days });
}

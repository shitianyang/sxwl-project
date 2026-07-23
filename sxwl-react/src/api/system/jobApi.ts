// ============================================
// 定时任务 API
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

/** 定时任务 DTO */
export interface SysJobItem {
  id: number;
  jobName: string;
  jobGroup: string;
  className: string;
  methodName: string;
  methodParams?: string;
  cronExpression: string;
  description?: string;
  status: number;
  createTime: string;
}

/** 定时任务查询参数 */
export interface SysJobQuery {
  jobName?: string;
  jobGroup?: string;
  status?: number;
  current: number;
  pageSize: number;
}

/** 定时任务表单 */
export interface SysJobForm {
  id?: number;
  jobName: string;
  jobGroup: string;
  className: string;
  methodName: string;
  methodParams?: string;
  cronExpression: string;
  description?: string;
  status: number;
}

/** 查询任务详情 */
export function getJobById(id: number) {
  return http.get<SysJobItem>('/monitor/job/' + id);
}

/** 分页查询任务列表 */
export function getJobPageByParams(params: SysJobQuery) {
  return http.get<PageInfo<SysJobItem>>('/monitor/job/page', params as unknown as Record<string, unknown>);
}

/** 新增任务 */
export function createJob(data: SysJobForm) {
  return http.post<null>('/monitor/job', data);
}

/** 修改任务 */
export function updateJob(data: SysJobForm) {
  return http.put<null>('/monitor/job', data);
}

/** 删除任务 */
export function deleteJobById(id: number) {
  return http.deleteReq<null>('/monitor/job/' + id);
}

/** 暂停任务 */
export function pauseJob(id: number) {
  return http.put<null>('/monitor/job/pause/' + id);
}

/** 恢复任务 */
export function resumeJob(id: number) {
  return http.put<null>('/monitor/job/resume/' + id);
}

/** 立即执行一次 */
export function runOnceJob(id: number) {
  return http.put<null>('/monitor/job/run/' + id);
}

// ============================================
// 通知公告 API
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

/** 通知公告 DTO */
export interface SysNoticeItem {
  id: number;
  title: string;
  content: string;
  noticeType: string;
  level: string;
  status: number;
  createTime: string;
}

/** 通知公告查询参数 */
export interface SysNoticeQuery {
  title?: string;
  noticeType?: string;
  status?: number;
  current: number;
  pageSize: number;
}

/** 通知公告表单 */
export interface SysNoticeForm {
  id?: number;
  title: string;
  content: string;
  noticeType: string;
  level: string;
  status?: number;
}

/** 查询公告详情 */
export function getNoticeById(id: number) {
  return http.get<SysNoticeItem>('/system/notice/' + id);
}

/** 分页查询公告列表 */
export function getNoticePageByParams(params: SysNoticeQuery) {
  return http.get<PageInfo<SysNoticeItem>>('/system/notice/page', params as unknown as Record<string, unknown>);
}

/** 新增公告 */
export function createNotice(data: SysNoticeForm) {
  return http.post<null>('/system/notice', data);
}

/** 修改公告 */
export function updateNotice(data: SysNoticeForm) {
  return http.put<null>('/system/notice', data);
}

/** 删除公告 */
export function deleteNoticeById(id: number) {
  return http.deleteReq<null>('/system/notice/' + id);
}

/** 发布公告 */
export function publishNotice(id: number) {
  return http.put<null>('/system/notice/publish/' + id);
}

/** 撤回公告 */
export function revokeNotice(id: number) {
  return http.put<null>('/system/notice/revoke/' + id);
}

// ========== 未读/已读接口 ==========

/** 未读公告列表项 */
export interface SysNoticeUnreadItem {
  id: number;
  title: string;
  noticeType: string;
  level: string;
  publishTime: string;
  createTime: string;
  readFlag: number; // 0=未读 1=已读
}

/** 获取未读公告数 */
export function getUnreadCount() {
  return http.get<number>('/system/notice/unread/count');
}

/** 获取最近公告列表（含已读状态） */
export function getUnreadList() {
  return http.get<SysNoticeUnreadItem[]>('/system/notice/unread/list');
}

/** 标记单条公告为已读 */
export function markAsRead(noticeId: number) {
  return http.post<null>('/system/notice/read/' + noticeId);
}

/** 标记全部为已读 */
export function markAllAsRead() {
  return http.post<null>('/system/notice/read/all');
}

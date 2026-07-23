// ============================================
// 数据备份 API
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

/** 备份记录 DTO */
export interface SysBackupItem {
  id: number;
  fileName: string;
  fileSize: number;
  fileSizeDisplay: string;
  fileUrl?: string;
  status: number;
  createTime: string;
}

/** 执行备份 */
export function backup() {
  return http.post<null>('/sys/backup/backup');
}

/** 查询备份列表 */
export function getBackupList(page: number, size: number) {
  return http.get<PageInfo<SysBackupItem>>('/sys/backup/list', { page, size } as unknown as Record<string, unknown>);
}

/** 恢复备份 */
export function restoreBackup(fileId: number) {
  return http.post<null>('/sys/backup/restore/' + fileId);
}

/** 删除备份记录 */
export function deleteBackup(id: number) {
  return http.deleteReq<null>('/sys/backup/' + id);
}

// ============================================
// 缓存管理 API
// ============================================

import { http } from '@/api/http';

/** 缓存分类 DTO */
export interface SysCacheCategoryItem {
  name: string;
  keyPrefix: string;
}

/** 缓存 Key 详情 DTO */
export interface SysCacheKeyDetailItem {
  key: string;
  type: string;
  value: unknown;
  ttl: number;
}

/** 获取所有缓存分类列表 */
export function getCacheCategories() {
  return http.get<SysCacheCategoryItem[]>('/sys/cache/names');
}

/** 获取某分类下的 Key 列表 */
export function getCacheKeys(keyPrefix: string) {
  return http.get<SysCacheKeyDetailItem[]>('/sys/cache/keys', { keyPrefix } as unknown as Record<string, unknown>);
}

/** 获取单个 Key 的详细信息 */
export function getCacheKeyDetail(key: string) {
  return http.get<SysCacheKeyDetailItem>('/sys/cache/value', { key } as unknown as Record<string, unknown>);
}

/** 清空指定分类缓存 */
export function clearCacheByName(keyPrefix: string) {
  return http.deleteReq<null>('/sys/cache/clearName', { keyPrefix } as unknown as Record<string, unknown>);
}

/** 删除单个缓存 Key */
export function clearCacheByKey(key: string) {
  return http.deleteReq<null>('/sys/cache/clearKey', { key } as unknown as Record<string, unknown>);
}

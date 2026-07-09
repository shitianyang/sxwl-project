// ============================================
// localStorage 工具封装
// 所有本地存储操作统一走此模块
// ============================================

/** 存储键名常量 */
export const STORAGE_KEYS = {
  ACCESS_TOKEN: 'accessToken',
  REFRESH_TOKEN: 'refreshToken',
  USERNAME: 'username',
  REMEMBERED_USERNAME: 'rememberedUsername',
} as const;

const PREFIX = 'sxwl_';

function prefixed(key: string) {
  return PREFIX + key;
}

/** 存储字符串值 */
export function setItem(key: string, value: string) {
  localStorage.setItem(prefixed(key), value);
}

/** 获取字符串值 */
export function getItem(key: string): string | null {
  return localStorage.getItem(prefixed(key));
}

/** 移除指定键 */
export function removeItem(key: string) {
  localStorage.removeItem(prefixed(key));
}

/** 存储 JSON 对象 */
export function setJson<T>(key: string, value: T) {
  localStorage.setItem(prefixed(key), JSON.stringify(value));
}

/** 获取并解析 JSON 对象 */
export function getJson<T>(key: string): T | null {
  const raw = localStorage.getItem(prefixed(key));
  if (!raw) return null;
  try {
    return JSON.parse(raw) as T;
  } catch {
    return null;
  }
}

/** 存储布尔值 */
export function setBoolean(key: string, value: boolean) {
  localStorage.setItem(prefixed(key), value ? '1' : '0');
}

/** 获取布尔值 */
export function getBoolean(key: string): boolean {
  return localStorage.getItem(prefixed(key)) === '1';
}

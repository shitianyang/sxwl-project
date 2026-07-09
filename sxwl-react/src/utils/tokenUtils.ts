// ============================================
// JWT Token 解析工具
// ============================================

/** JWT Payload 基础字段 */
export interface JwtPayload {
  sub?: string;
  exp?: number;
  iat?: number;
  jti?: string;
  [key: string]: unknown;
}

/**
 * 解码 JWT 的 payload 部分（不验证签名）
 * @returns 解析后的 payload，解析失败返回 null
 */
export function parseToken(token: string): JwtPayload | null {
  try {
    const payload = token.split('.')[1];
    if (!payload) return null;

    const normalized = payload.replace(/-/g, '+').replace(/_/g, '/');
    const padded = normalized.padEnd(Math.ceil(normalized.length / 4) * 4, '=');
    return JSON.parse(atob(padded)) as JwtPayload;
  } catch {
    return null;
  }
}

/**
 * 判断 Token 是否未过期
 * @param token JWT 字符串
 * @param bufferMs 过期缓冲毫秒（默认 0）
 */
export function isTokenActive(token: string | null, bufferMs = 0): boolean {
  if (!token) return false;

  const payload = parseToken(token);
  if (!payload || typeof payload.exp !== 'number') return false;

  return payload.exp * 1000 > Date.now() + bufferMs;
}

/**
 * 获取 Token 的过期时间戳（毫秒）
 * @returns 过期毫秒时间戳，无法解析返回 null
 */
export function getTokenExp(token: string): number | null {
  const payload = parseToken(token);
  if (!payload || typeof payload.exp !== 'number') return null;
  return payload.exp * 1000;
}

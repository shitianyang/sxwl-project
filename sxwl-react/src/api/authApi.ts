import { http } from './http';

export interface LoginRequest {
  username: string;
  password: string; // SM2 加密后的 Base64 密文
}

export interface TokenPair {
  accessToken: string;
  refreshToken: string;
}

export interface RefreshRequest {
  refreshToken: string;
  deviceId: string;
}

/** SM2 公钥响应结构（含 keyId 和过期时间，支持密钥轮换） */
export interface PublicKeyResult {
  publicKey: string;
  keyId: string;
  expiresAt: number;
}

/** 获取 SM2 公钥 */
export function getPublicKey() {
  return http.get<PublicKeyResult>('/auth/public-key');
}

/** 密码登录 */
export function loginByPassword(data: LoginRequest) {
  return http.post<TokenPair>('/auth/login/password', data);
}

/** 刷新 Token */
export function refreshToken(data: RefreshRequest) {
  return http.post<TokenPair>('/auth/refresh', data);
}

/** 登出 */
export function logout() {
  return http.post<null>('/auth/logout');
}

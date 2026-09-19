import { http } from './http';

export interface LoginRequest {
  username?: string; // 用户名（密码登录时使用）
  password?: string; // SM2 加密后的 Base64 密文（密码登录时使用）
  phone?: string; // 手机号（短信登录时使用）
  smsCode?: string; // 短信验证码（短信登录时使用）
  captchaUuid: string;
  captchaCode: string;
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

/** 验证码响应 */
export interface CaptchaResult {
  uuid: string;
  base64Image: string;
}

/** 获取 SM2 公钥 */
export function getPublicKey() {
  return http.get<PublicKeyResult>('/auth/public-key');
}

/** 获取图形验证码 */
export function getCaptchaImage() {
  return http.get<CaptchaResult>('/captcha/image');
}

/** 密码登录 */
export function loginByPassword(data: LoginRequest) {
  return http.post<TokenPair>('/auth/login/password', data);
}

/** 短信登录 */
export function loginBySms(data: LoginRequest) {
  return http.post<TokenPair>('/auth/login/sms', data);
}

/** 获取短信验证码（服务端对同一手机号 60 秒内拒发，并回剩余秒数） */
export function getSmsCaptcha(phone: string) {
  return http.post<{ sent: string }>('/captcha/sms', { phone });
}

/** 刷新 Token */
export function refreshToken(data: RefreshRequest) {
  return http.post<TokenPair>('/auth/refresh', data);
}

/** 当前用户的权限和角色信息 */
export interface UserPermissionInfo {
  permissions: string[];
  roles: string[];
}

/**
 * 获取当前用户的权限 + 角色列表
 * 登录后调用，结果注入 permissionStore
 */
export function getUserPermissions() {
  return http.get<UserPermissionInfo>('/auth/permissions');
}

/** 登出 */
export function logout() {
  return http.post<null>('/auth/logout');
}

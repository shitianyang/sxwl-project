import { create } from 'zustand';

interface AuthState {
  accessToken: string | null;
  refreshToken: string | null;
  username: string | null;

  /** 登录成功后保存 Token */
  setTokens: (accessToken: string, refreshToken: string, username: string) => void;
  /** 刷新 Token 后保留当前用户信息 */
  setTokenPair: (accessToken: string, refreshToken: string) => void;
  /** 登出 / Token 失效时清空 */
  clearAuth: () => void;
  /** 是否已登录 */
  isLoggedIn: () => boolean;
}

function isJwtActive(token: string | null) {
  if (!token) return false;

  try {
    const payload = token.split('.')[1];
    if (!payload) return false;

    const normalized = payload.replace(/-/g, '+').replace(/_/g, '/');
    const padded = normalized.padEnd(Math.ceil(normalized.length / 4) * 4, '=');
    const decoded = JSON.parse(atob(padded)) as { exp?: number };

    return typeof decoded.exp === 'number' && decoded.exp * 1000 > Date.now();
  } catch {
    return false;
  }
}

export const useAuthStore = create<AuthState>((set, get) => ({
  accessToken: localStorage.getItem('accessToken'),
  refreshToken: localStorage.getItem('refreshToken'),
  username: localStorage.getItem('username'),

  setTokens: (accessToken, refreshToken, username) => {
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    localStorage.setItem('username', username);
    set({ accessToken, refreshToken, username });
  },

  setTokenPair: (accessToken, refreshToken) => {
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    set({ accessToken, refreshToken });
  },

  clearAuth: () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('username');
    set({ accessToken: null, refreshToken: null, username: null });
  },

  isLoggedIn: () => isJwtActive(get().accessToken) || isJwtActive(get().refreshToken),
}));

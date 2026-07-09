import { create } from 'zustand';
import { getItem, setItem, removeItem, STORAGE_KEYS } from '@/utils/storageUtils';
import { isTokenActive } from '@/utils/tokenUtils';

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

export const useAuthStore = create<AuthState>((set, get) => ({
  accessToken: getItem(STORAGE_KEYS.ACCESS_TOKEN),
  refreshToken: getItem(STORAGE_KEYS.REFRESH_TOKEN),
  username: getItem(STORAGE_KEYS.USERNAME),

  setTokens: (accessToken, refreshToken, username) => {
    setItem(STORAGE_KEYS.ACCESS_TOKEN, accessToken);
    setItem(STORAGE_KEYS.REFRESH_TOKEN, refreshToken);
    setItem(STORAGE_KEYS.USERNAME, username);
    set({ accessToken, refreshToken, username });
  },

  setTokenPair: (accessToken, refreshToken) => {
    setItem(STORAGE_KEYS.ACCESS_TOKEN, accessToken);
    setItem(STORAGE_KEYS.REFRESH_TOKEN, refreshToken);
    set({ accessToken, refreshToken });
  },

  clearAuth: () => {
    removeItem(STORAGE_KEYS.ACCESS_TOKEN);
    removeItem(STORAGE_KEYS.REFRESH_TOKEN);
    removeItem(STORAGE_KEYS.USERNAME);
    set({ accessToken: null, refreshToken: null, username: null });
  },

  isLoggedIn: () => isTokenActive(get().accessToken) || isTokenActive(get().refreshToken),
}));

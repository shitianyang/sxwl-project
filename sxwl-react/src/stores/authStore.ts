import { create } from 'zustand';
import { getAuthItem, setAuthItem, removeAuthItem, STORAGE_KEYS } from '@/utils/storageUtils';
import { isTokenActive } from '@/utils/tokenUtils';

interface AuthState {
  accessToken: string | null;
  refreshToken: string | null;
  username: string | null;
  deviceId: string;

  /** 登录成功后保存 Token */
  setTokens: (accessToken: string, refreshToken: string, username: string, deviceId?: string) => void;
  /** 刷新 Token 后保留当前用户信息 */
  setTokenPair: (accessToken: string, refreshToken: string) => void;
  /** 登出 / Token 失效时清空 */
  clearAuth: () => void;
  /** 是否已登录 */
  isLoggedIn: () => boolean;
}

export const useAuthStore = create<AuthState>((set, get) => ({
  accessToken: getAuthItem(STORAGE_KEYS.ACCESS_TOKEN),
  refreshToken: getAuthItem(STORAGE_KEYS.REFRESH_TOKEN),
  username: getAuthItem(STORAGE_KEYS.USERNAME),

  deviceId: getAuthItem(STORAGE_KEYS.DEVICE_ID) || 'web',

  setTokens: (accessToken, refreshToken, username, deviceId) => {
    setAuthItem(STORAGE_KEYS.ACCESS_TOKEN, accessToken);
    setAuthItem(STORAGE_KEYS.REFRESH_TOKEN, refreshToken);
    setAuthItem(STORAGE_KEYS.USERNAME, username);
    setAuthItem(STORAGE_KEYS.DEVICE_ID, deviceId || 'web');
    set({ accessToken, refreshToken, username, deviceId: deviceId || 'web' });
  },

  setTokenPair: (accessToken, refreshToken) => {
    setAuthItem(STORAGE_KEYS.ACCESS_TOKEN, accessToken);
    setAuthItem(STORAGE_KEYS.REFRESH_TOKEN, refreshToken);
    set({ accessToken, refreshToken });
  },

  clearAuth: () => {
    removeAuthItem(STORAGE_KEYS.ACCESS_TOKEN);
    removeAuthItem(STORAGE_KEYS.REFRESH_TOKEN);
    removeAuthItem(STORAGE_KEYS.USERNAME);
    removeAuthItem(STORAGE_KEYS.DEVICE_ID);
    set({ accessToken: null, refreshToken: null, username: null, deviceId: 'web' });
  },

  isLoggedIn: () => isTokenActive(get().accessToken) || isTokenActive(get().refreshToken),
}));

import axios, { type AxiosError, type InternalAxiosRequestConfig } from 'axios';
import { useAuthStore } from '@/stores/authStore';

/** 后端统一响应格式 */
export interface SxwlResult<T = unknown> {
  code: number;
  msg: string;
  data: T;
}

const request = axios.create({
  baseURL: '/sxwl-api',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' },
});

const refreshRequest = axios.create({
  baseURL: '/sxwl-api',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' },
});

// ==================== 请求拦截器：自动带 Token ====================
request.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = localStorage.getItem('accessToken');
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// ==================== 响应拦截器：统一错误处理 + 401 自动刷新 ====================
let isRefreshing = false;
let refreshSubscribers: ((token: string) => void)[] = [];

function subscribeTokenRefresh(cb: (token: string) => void) {
  refreshSubscribers.push(cb);
}

function onTokenRefreshed(newToken: string) {
  refreshSubscribers.forEach((cb) => cb(newToken));
  refreshSubscribers = [];
}

request.interceptors.response.use(
  (response) => {
    const newToken = response.headers['x-new-token'];
    if (typeof newToken === 'string' && newToken) {
      const { refreshToken, setTokenPair } = useAuthStore.getState();
      if (refreshToken) {
        setTokenPair(newToken, refreshToken);
      }
    }
    return response;
  },
  async (error: AxiosError<SxwlResult>) => {
    const { config, response } = error;
    if (!response || !config) return Promise.reject(error);

    // 401 → 尝试刷新 Token
    if (response.status === 401 && !config.url?.includes('/auth/refresh')) {
      const refreshToken = localStorage.getItem('refreshToken');
      if (!refreshToken) {
        // 无 refreshToken，直接踢到登录页
        useAuthStore.getState().clearAuth();
        window.location.href = '/login';
        return Promise.reject(error);
      }

      if (!isRefreshing) {
        isRefreshing = true;
        try {
          const res = await refreshRequest.post<SxwlResult<{ accessToken: string; refreshToken: string }>>(
            '/auth/refresh',
            { refreshToken, deviceId: 'web' },
          );
          const { accessToken, refreshToken: newRefresh } = res.data.data;
          useAuthStore.getState().setTokenPair(accessToken, newRefresh);
          isRefreshing = false;
          onTokenRefreshed(accessToken);
        } catch {
          isRefreshing = false;
          refreshSubscribers = [];
          useAuthStore.getState().clearAuth();
          window.location.href = '/login';
          return Promise.reject(error);
        }
      }

      // 等待刷新完成后重试原请求
      return new Promise((resolve) => {
        subscribeTokenRefresh((newToken: string) => {
          if (config.headers) {
            config.headers.Authorization = `Bearer ${newToken}`;
          }
          resolve(request(config));
        });
      });
    }

    return Promise.reject(error);
  },
);

export default request;

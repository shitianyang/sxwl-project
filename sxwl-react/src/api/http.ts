// ============================================
// HTTP 客户端 — axios 实例 + 封装请求方法
// ============================================

import axios, {
  type AxiosError,
  type AxiosResponse,
  type InternalAxiosRequestConfig,
} from 'axios';
import { useAuthStore } from '@/stores/authStore';
import { getItem, STORAGE_KEYS } from '@/utils/storageUtils';

// ==================== 类型定义 ====================

/** 后端统一响应格式 */
export interface SxwlResult<T = unknown> {
  code: number;
  msg: string;
  data: T;
}

/** 分页响应 */
export interface PageResult<T> {
  rows: T[];
  total: number;
  page: number;
  pageSize: number;
}

// ==================== axios 实例 ====================

const instance = axios.create({
  baseURL: '/sxwl-api',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' },
});

const refreshInstance = axios.create({
  baseURL: '/sxwl-api',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' },
});

// ==================== 请求拦截器 ====================

instance.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = getItem(STORAGE_KEYS.ACCESS_TOKEN);
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// ==================== 响应拦截器：统一错误 + 401 自动刷新 ====================

let isRefreshing = false;
let refreshSubscribers: ((token: string) => void)[] = [];

function subscribeTokenRefresh(cb: (token: string) => void) {
  refreshSubscribers.push(cb);
}

function onTokenRefreshed(newToken: string) {
  refreshSubscribers.forEach((cb) => cb(newToken));
  refreshSubscribers = [];
}

instance.interceptors.response.use(
  (response) => {
    // X-New-Token 头处理：Token 自动续期
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

    // 401 → 尝试刷新 Token（排除刷新接口自身）
    if (response.status === 401 && !config.url?.includes('/auth/refresh')) {
      const refreshToken = getItem(STORAGE_KEYS.REFRESH_TOKEN);
      if (!refreshToken) {
        useAuthStore.getState().clearAuth();
        window.location.href = '/login';
        return Promise.reject(error);
      }

      if (!isRefreshing) {
        isRefreshing = true;
        try {
          const res = await refreshInstance.post<
            SxwlResult<{ accessToken: string; refreshToken: string }>
          >('/auth/refresh', { refreshToken, deviceId: 'web' });
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
      return new Promise<AxiosResponse>((resolve) => {
        subscribeTokenRefresh((newToken: string) => {
          if (config.headers) {
            config.headers.Authorization = `Bearer ${newToken}`;
          }
          resolve(instance(config));
        });
      });
    }

    return Promise.reject(error);
  },
);

// ==================== 封装请求方法 ====================

export const http = {
  get<T = unknown>(url: string, params?: Record<string, unknown>) {
    return instance.get<SxwlResult<T>>(url, { params });
  },

  post<T = unknown>(url: string, data?: unknown) {
    return instance.post<SxwlResult<T>>(url, data);
  },

  put<T = unknown>(url: string, data?: unknown) {
    return instance.put<SxwlResult<T>>(url, data);
  },

  deleteReq<T = unknown>(url: string, params?: Record<string, unknown>) {
    return instance.delete<SxwlResult<T>>(url, { params });
  },

  /**
   * 文件上传（multipart/form-data）
   * @param url 上传地址
   * @param formData FormData 对象（含文件）
   */
  upload<T = unknown>(url: string, formData: FormData) {
    return instance.post<SxwlResult<T>>(url, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },

  /**
   * 文件下载
   * @param url 下载地址
   * @param params 查询参数
   * @param filename 保存文件名（若不传则从响应头解析）
   */
  async download(url: string, params?: Record<string, unknown>, filename?: string) {
    const res = await instance.get(url, { params, responseType: 'blob' });
    const blob = new Blob([res.data]);
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download =
      filename ||
      res.headers['content-disposition']?.split('filename=')[1]?.replace(/['"]/g, '') ||
      'download';
    link.click();
    URL.revokeObjectURL(link.href);
  },
};



import { create } from 'zustand';
import { getUserPermissions } from '@/api/authApi';

interface PermissionState {
  /** 当前用户的权限标识列表，如 ['system:user:list', 'system:user:add'] */
  permissions: string[];
  /** 是否已从后端加载完成 */
  loaded: boolean;
  /** 是否正在加载中 */
  loading: boolean;
  /** 设置权限列表（通常在获取用户信息后调用） */
  setPermissions: (perms: string[]) => void;
  /** 从后端获取当前用户的权限列表 */
  fetchPermissions: () => Promise<void>;
  /** 检查是否拥有指定权限 */
  hasPermission: (permission: string | string[], mode?: 'and' | 'or') => boolean;
  /** 清空权限 */
  clearPermissions: () => void;
}

export const usePermissionStore = create<PermissionState>((set, get) => ({
  permissions: [],
  loaded: false,
  loading: false,

  setPermissions: (perms) => {
    set({ permissions: perms, loaded: true });
  },

  fetchPermissions: async () => {
    // 已加载或正在加载中则跳过
    if (get().loaded || get().loading) return;

    set({ loading: true });
    try {
      const res = await getUserPermissions();
      const perms = res.data.data?.permissions || [];
      set({ permissions: perms, loaded: true });
    } catch {
      // 加载失败时设为已加载（空权限），避免阻塞页面
      set({ permissions: [], loaded: true });
    } finally {
      set({ loading: false });
    }
  },

  hasPermission: (permission, mode = 'or') => {
    const { permissions } = get();
    if (!permission) return true;
    const perms = Array.isArray(permission) ? permission : [permission];
    if (perms.length === 0) return true;

    // 超级管理员 *:*:* 放行
    if (permissions.includes('*:*:*')) return true;

    if (mode === 'and') {
      return perms.every((p) => permissions.includes(p));
    }
    return perms.some((p) => permissions.includes(p));
  },

  clearPermissions: () => {
    set({ permissions: [], loading: false, loaded: false });
  },
}));

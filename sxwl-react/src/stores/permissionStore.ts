import { create } from 'zustand';

interface PermissionState {
  /** 当前用户的权限标识列表，如 ['system:user:list', 'system:user:add'] */
  permissions: string[];
  /** 是否已从后端加载完成 */
  loaded: boolean;
  /** 设置权限列表（通常在获取用户信息后调用） */
  setPermissions: (perms: string[]) => void;
  /** 检查是否拥有指定权限 */
  hasPermission: (permission: string | string[], mode?: 'and' | 'or') => boolean;
  /** 清空权限 */
  clearPermissions: () => void;
}

export const usePermissionStore = create<PermissionState>((set, get) => ({
  permissions: [],
  loaded: false,

  setPermissions: (perms) => {
    set({ permissions: perms, loaded: true });
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
    set({ permissions: [], loaded: false });
  },
}));

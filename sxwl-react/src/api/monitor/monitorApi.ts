// ============================================
// 系统监控 API
// ============================================

import { http } from '@/api/http';

/** 服务器信息 VO */
export interface ServerInfoVO {
  cpuCores: number;
  cpuLoad: number;
  memTotal: number;
  memUsed: number;
  diskTotal: number;
  diskUsed: number;
}

/** GC 信息 VO */
export interface GcInfoVO {
  gcName: string;
  gcCount: number;
  gcTime: number;
}

/** JVM 信息 VO */
export interface JvmInfoVO {
  heapMax: number;
  heapUsed: number;
  heapCommitted: number;
  threadCount: number;
  peakThreadCount: number;
  classLoadedCount: number;
  gcInfos: GcInfoVO[];
}

/** Redis 信息 DTO */
export interface SysRedisInfoDTO {
  connectedClients: number;
  usedMemory: number;
  hitRate: number;
  totalKeys: number;
}

/** 数据库连接信息 DTO */
export interface SysDbInfoDTO {
  activeConnections: number;
}

/** 获取服务器信息（CPU/内存/磁盘） */
export function getServerInfo() {
  return http.get<ServerInfoVO>('/sys/monitor/server');
}

/** 获取 JVM 信息 */
export function getJvmInfo() {
  return http.get<JvmInfoVO>('/sys/monitor/jvm');
}

/** 获取 Redis 信息 */
export function getRedisInfo() {
  return http.get<SysRedisInfoDTO>('/sys/monitor/redis');
}

/** 获取数据库连接信息 */
export function getDbInfo() {
  return http.get<SysDbInfoDTO>('/sys/monitor/db');
}

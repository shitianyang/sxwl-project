import { useState, useEffect, useCallback } from 'react';
import { useSSE } from './useSSE';

/** 监控 SSE 推送的数据结构（与后端 SysMonitorDataDTO 对应） */
export interface SysMonitorDataDTO {
  timestamp: string;
  server: {
    cpuCores: number;
    cpuLoad: number;
    memTotal: number;
    memUsed: number;
    diskTotal: number;
    diskUsed: number;
  } | null;
  jvm: {
    heapMax: number;
    heapUsed: number;
    heapCommitted: number;
    threadCount: number;
    peakThreadCount: number;
    classLoadedCount: number;
    gcInfos: { name: string; count: number; totalTimeMs: number }[];
  } | null;
  redis: {
    connectedClients: number;
    usedMemory: number;
    hitRate: number;
    totalKeys: number;
  } | null;
  db: {
    activeConnections: number;
  } | null;
}

/** 历史数据点数上限（200 条 ≈ 16 分钟） */
const MAX_HISTORY = 200;

/** 按维度聚合的历史数据 */
export interface MonitorHistory {
  server: { time: string; cpuLoad: number; memUsed: number; memTotal: number; diskUsed: number; diskTotal: number }[];
  jvm: { time: string; heapUsed: number; heapMax: number; heapCommitted: number; threadCount: number; peakThreadCount: number; classLoadedCount: number }[];
  redis: { time: string; connectedClients: number; usedMemory: number; hitRate: number; totalKeys: number }[];
  db: { time: string; activeConnections: number }[];
}

const emptyHistory = (): MonitorHistory => ({
  server: [],
  jvm: [],
  redis: [],
  db: [],
});

export interface UseMonitorSSEOptions {
  /** 是否启用 SSE 连接，默认 true */
  enabled?: boolean;
}

/**
 * 实时监控数据 SSE Hook
 *
 * 通过通用 useSSE 连接统一 SSE 端点，
 * 接收服务端每 5 秒推送的监控数据，并自动累积历史趋势。
 */
export function useMonitorSSE(options: UseMonitorSSEOptions = {}) {
  const { enabled = true } = options;
  const [data, setData] = useState<SysMonitorDataDTO | null>(null);
  const [history, setHistory] = useState<MonitorHistory>(emptyHistory);

  /** 将 SSE 数据点追加到历史（不可变更新，每次生成新数组，避免直接 mutate ref 内部数组） */
  const appendToHistory = useCallback((d: SysMonitorDataDTO) => {
    const time = d.timestamp;
    setHistory((prev) => {
      const next: MonitorHistory = { ...prev };
      if (d.server) {
        const arr = [...prev.server, { time, ...d.server }];
        next.server = arr.length > MAX_HISTORY ? arr.slice(arr.length - MAX_HISTORY) : arr;
      }
      if (d.jvm) {
        const arr = [...prev.jvm, { time, ...d.jvm }];
        next.jvm = arr.length > MAX_HISTORY ? arr.slice(arr.length - MAX_HISTORY) : arr;
      }
      if (d.redis) {
        const arr = [...prev.redis, { time, ...d.redis }];
        next.redis = arr.length > MAX_HISTORY ? arr.slice(arr.length - MAX_HISTORY) : arr;
      }
      if (d.db) {
        const arr = [...prev.db, { time, ...d.db }];
        next.db = arr.length > MAX_HISTORY ? arr.slice(arr.length - MAX_HISTORY) : arr;
      }
      return next;
    });
  }, []);

  /** 处理 monitor-data 事件 */
  const handleMonitorData = useCallback((event: MessageEvent) => {
    try {
      const parsed = JSON.parse(event.data) as SysMonitorDataDTO;
      setData(parsed);
      appendToHistory(parsed);
    } catch (e) {
      console.warn('监控 SSE 数据解析失败:', e);
    }
  }, [appendToHistory]);

  const { status } = useSSE('/sse/connect', {
    enabled,
    events: {
      'monitor-data': handleMonitorData,
    },
  });

  const connected = status === 'connected';

  // 连接断开时显示错误
  const [error, setError] = useState<string | null>(null);
  useEffect(() => {
    if (status === 'disconnected' && enabled) {
      setError('连接断开，尝试重连...');
    } else {
      setError(null);
    }
  }, [status, enabled]);

  return { data, connected, error, history };
}

import { useEffect, useRef, useState } from 'react';
import { createConnectionTicket } from '@/api/sse';
import { useAuthStore } from '@/stores/authStore';

/** SSE 连接状态 */
export type SSEStatus = 'connecting' | 'connected' | 'disconnected';

export interface SSEOptions {
  /** 是否启用连接，默认 true */
  enabled?: boolean;
  /** 自定义事件监听 key=事件名, value=处理函数 */
  events?: Record<string, (event: MessageEvent) => void>;
  /** 连接成功回调 */
  onOpen?: () => void;
  /** 重连标识，变化时强制断开重连 */
  reconnectKey?: number;
}

/** 通用 SSE Hook */
export function useSSE(path: string, options: SSEOptions = {}): { status: SSEStatus } {
  const { enabled = true, events, onOpen, reconnectKey } = options;
  const [status, setStatus] = useState<SSEStatus>('disconnected');

  const accessToken = useAuthStore((s) => s.accessToken);
  const esRef = useRef<EventSource | null>(null);
  const mountedRef = useRef(true);

  // 用 ref 保持回调最新，避免 effect 重复执行
  const onOpenRef = useRef(onOpen);
  onOpenRef.current = onOpen;
  const eventsRef = useRef(events);
  eventsRef.current = events;

  useEffect(() => {
    mountedRef.current = true;
    return () => {
      mountedRef.current = false;
    };
  }, []);

  useEffect(() => {
    if (!enabled || !accessToken) {
      setStatus('disconnected');
      return;
    }

    setStatus('connecting');

    let cancelled = false;
    let reconnectTimer: number | undefined;
    const connect = async () => {
      try {
        const result = await createConnectionTicket();
        if (cancelled) return;
        if (!result.data.data) throw new Error('SSE ticket response is invalid');

        const es = new EventSource(`/sxwl-api${path}?ticket=${encodeURIComponent(result.data.data)}`);
        esRef.current = es;
        es.onopen = () => {
          if (!mountedRef.current) return;
          setStatus('connected');
          onOpenRef.current?.();
        };
        const currentEvents = eventsRef.current;
        if (currentEvents) {
          Object.entries(currentEvents).forEach(([name, handler]) => es.addEventListener(name, handler));
        }
        es.onerror = () => {
          es.close();
          if (!mountedRef.current || cancelled) return;
          setStatus('disconnected');
          reconnectTimer = window.setTimeout(() => void connect(), 1000);
        };
      } catch {
        if (!mountedRef.current || cancelled) return;
        setStatus('disconnected');
        reconnectTimer = window.setTimeout(() => void connect(), 1000);
      }
    };
    void connect();

    return () => {
      cancelled = true;
      if (reconnectTimer !== undefined) window.clearTimeout(reconnectTimer);
      esRef.current?.close();
      esRef.current = null;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [enabled, accessToken, path, reconnectKey]);

  return { status };
}

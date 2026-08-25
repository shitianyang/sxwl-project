import { http } from '@/api/http';

/** 获取一次性 SSE / WebSocket 连接票据。 */
export function createConnectionTicket() {
  return http.post<string>('/sse/ticket');
}

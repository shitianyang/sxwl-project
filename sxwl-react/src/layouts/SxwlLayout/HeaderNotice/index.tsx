import { useEffect, useRef, useState, useCallback } from 'react';
import { Badge, Popover, List, Tag, Empty, Spin, Typography, Space } from 'antd';
import { SxwlIcon, SxwlMarkdown, SxwlButton, SxwlTooltip, SxwlModal } from '@/components';
import { useAuthStore } from '@/stores/authStore';
import { useSSE } from '@/hooks/useSSE';
import {
  getUnreadCount, getUnreadList, getNoticeById,
  markAsRead, markAllAsRead,
} from '@/api/system/noticeApi';
import type { SysNoticeUnreadItem, SysNoticeItem } from '@/api/system/noticeApi';
import './index.scss';

const { Text } = Typography;

type SseState = 'connecting' | 'connected' | 'disconnected';

const LEVEL_COLOR: Record<string, string> = {
  info: 'blue',
  important: 'orange',
  urgent: 'red',
};

const LEVEL_LABEL: Record<string, string> = {
  info: '普通',
  important: '重要',
  urgent: '紧急',
};

/**
 * Header 通知铃铛组件
 *
 * 未读 Badge + Popover 列表 + 点击预览（Markdown 渲染）+ SSE 状态指示。
 */
export default function HeaderNotice() {
  const [open, setOpen] = useState(false);
  const [unreadCount, setUnreadCount] = useState(0);
  const [noticeList, setNoticeList] = useState<SysNoticeUnreadItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [readAllLoading, setReadAllLoading] = useState(false);

  // SSE 连接状态（来自 useSSE）
  const [sseReconnectKey, setSseReconnectKey] = useState(0);
  const heartbeatTsRef = useRef<number>(Date.now());

  // 预览弹窗
  const [previewOpen, setPreviewOpen] = useState(false);
  const [previewLoading, setPreviewLoading] = useState(false);
  const [previewNotice, setPreviewNotice] = useState<SysNoticeItem | null>(null);

  const accessToken = useAuthStore((s) => s.accessToken);

  // ===== 获取未读计数 =====
  const fetchUnreadCount = useCallback(async () => {
    try {
      const res = await getUnreadCount();
      setUnreadCount(res.data.data);
    } catch {
      // 静默失败
    }
  }, []);

  // ===== 获取公告列表 =====
  const fetchNoticeList = useCallback(async () => {
    setLoading(true);
    try {
      const res = await getUnreadList();
      setNoticeList(res.data.data);
    } catch {
      // 静默失败
    } finally {
      setLoading(false);
    }
  }, []);

  // ===== 弹出时刷新列表 =====
  const handleOpenChange = useCallback(
    (newOpen: boolean) => {
      setOpen(newOpen);
      if (newOpen) fetchNoticeList();
    },
    [fetchNoticeList],
  );

  // ===== 标记单条已读 =====
  const handleMarkRead = useCallback(
    async (noticeId: number) => {
      try {
        await markAsRead(noticeId);
        setNoticeList((prev) =>
          prev.map((item) => (item.id === noticeId ? { ...item, readFlag: 1 } : item)),
        );
        fetchUnreadCount();
      } catch {
        // 静默失败
      }
    },
    [fetchUnreadCount],
  );

  // ===== 标记全部已读 =====
  const handleMarkAllRead = useCallback(async () => {
    setReadAllLoading(true);
    try {
      await markAllAsRead();
      setNoticeList((prev) => prev.map((item) => ({ ...item, readFlag: 1 })));
      fetchUnreadCount();
    } catch {
      // 静默失败
    } finally {
      setReadAllLoading(false);
    }
  }, [fetchUnreadCount]);

  // ===== 打开预览弹窗 =====
  const handlePreview = useCallback(async (item: SysNoticeUnreadItem) => {
    // 自动标记已读
    if (item.readFlag === 0) {
      handleMarkRead(item.id);
    }

    setPreviewLoading(true);
    setPreviewOpen(true);
    try {
      const res = await getNoticeById(item.id);
      setPreviewNotice(res.data.data);
    } catch {
      setPreviewNotice(null);
    } finally {
      setPreviewLoading(false);
    }
  }, [handleMarkRead]);

  // ===== 关闭预览弹窗 =====
  const handleClosePreview = useCallback(() => {
    setPreviewOpen(false);
    setPreviewNotice(null);
  }, []);

  // ===== 建立 SSE 连接（通过通用 useSSE） =====
  const onNewNotice = useCallback(() => {
    fetchUnreadCount();
  }, [fetchUnreadCount]);

  const onHeartbeat = useCallback(() => {
    heartbeatTsRef.current = Date.now();
  }, []);

  const { status: sseState } = useSSE('/sse/connect', {
    enabled: !!accessToken,
    reconnectKey: sseReconnectKey,
    events: {
      'new-notice': onNewNotice,
      heartbeat: onHeartbeat,
    },
  });

  // 心跳超时检测：每 30 秒检查一次，90 秒无心跳则强制重连
  useEffect(() => {
    if (sseState !== 'connected') return;

    const interval = setInterval(() => {
      if (Date.now() - heartbeatTsRef.current > 90_000) {
        console.warn('SSE 心跳超时，触发重连');
        setSseReconnectKey((k) => k + 1);
      }
    }, 30_000);

    return () => clearInterval(interval);
  }, [sseState]);

  // 轮询回退：每 30 秒拉取未读计数
  useEffect(() => {
    if (!accessToken) return;

    fetchUnreadCount();
    const interval = setInterval(fetchUnreadCount, 30_000);

    return () => clearInterval(interval);
  }, [accessToken, fetchUnreadCount]);

  // ===== 弹窗内容 =====
  const popoverContent = (
    <div className="sxwl-header-notice-popover">
      <div className="sxwl-header-notice-header">
        <Space size={8}>
          <Text strong>消息通知</Text>
          <SxwlTooltip title={sseState === 'connected' ? '实时连接中' : sseState === 'connecting' ? '连接中...' : '连接断开，轮询中'}>
            <span className={`sxwl-header-notice-sse-dot ${sseState}`} />
          </SxwlTooltip>
        </Space>
        {unreadCount > 0 && (
          <SxwlButton type="link" size="small" loading={readAllLoading} onClick={handleMarkAllRead}>
            全部已读
          </SxwlButton>
        )}
      </div>
      {loading ? (
        <div className="sxwl-header-notice-loading">
          <Spin size="small" />
        </div>
      ) : noticeList.length === 0 ? (
        <Empty description="暂无通知" image={Empty.PRESENTED_IMAGE_SIMPLE} />
      ) : (
        <List
          className="sxwl-header-notice-list"
          dataSource={noticeList}
          renderItem={(item) => (
            <List.Item
              className={`sxwl-header-notice-item ${item.readFlag === 0 ? 'unread' : ''}`}
              onClick={() => handlePreview(item)}
            >
              <List.Item.Meta
                title={
                  <Space size={4}>
                    {item.readFlag === 0 && <span className="sxwl-header-notice-dot" />}
                    <Text
                      strong={item.readFlag === 0}
                      ellipsis={{ tooltip: item.title }}
                      style={{ maxWidth: 220 }}
                    >
                      {item.title}
                    </Text>
                  </Space>
                }
                description={
                  <Space size={8}>
                    <Tag color={LEVEL_COLOR[item.level] || 'blue'} style={{ fontSize: 11, lineHeight: '18px' }}>
                      {LEVEL_LABEL[item.level] || item.level}
                    </Tag>
                    <Text type="secondary" style={{ fontSize: 12 }}>
                      {item.createTime}
                    </Text>
                  </Space>
                }
              />
            </List.Item>
          )}
        />
      )}
    </div>
  );

  return (
    <>
      {/* SSE 状态指示器（独立于 Popover，点击只显示连接状态） */}
      <span className="sxwl-header-notice-sse-wrapper">
        <SxwlTooltip title={sseState === 'connected' ? '实时连接中' : sseState === 'connecting' ? '连接中...' : '连接断开，轮询中'}>
          <span className={`sxwl-header-notice-sse-dot ${sseState}`} />
        </SxwlTooltip>
      </span>
      <Popover
        content={popoverContent}
        trigger="click"
        placement="bottomRight"
        open={open}
        onOpenChange={handleOpenChange}
        classNames={{ root: 'sxwl-header-notice-overlay' }}
      >
        <div className="sxwl-header-notice-trigger">
          <Badge count={unreadCount} showZero={false} size="small" offset={[-2, 2]} className="sxwl-header-notice-badge">
            <SxwlTooltip title="消息通知">
              <SxwlButton type="text" className="sxwl-header-notice-btn" icon={<SxwlIcon name="BellOutlined" />} />
            </SxwlTooltip>
          </Badge>
        </div>
      </Popover>

      {/* 公告预览弹窗 */}
      <SxwlModal
        title={previewNotice?.title || '公告详情'}
        open={previewOpen}
        onCancel={handleClosePreview}
        footer={null}
        width={720}
        destroyOnHidden
      >
        {previewLoading ? (
          <div style={{ textAlign: 'center', padding: 40 }}>
            <Spin />
          </div>
        ) : previewNotice ? (
          <div>
            <Space size={8} style={{ marginBottom: 16 }}>
              <Tag color={LEVEL_COLOR[previewNotice.level] || 'blue'}>
                {LEVEL_LABEL[previewNotice.level] || previewNotice.level}
              </Tag>
              <Text type="secondary">{previewNotice.createTime}</Text>
            </Space>
            <SxwlMarkdown content={previewNotice.content} />
          </div>
        ) : (
          <Empty description="加载失败" image={Empty.PRESENTED_IMAGE_SIMPLE} />
        )}
      </SxwlModal>
    </>
  );
}

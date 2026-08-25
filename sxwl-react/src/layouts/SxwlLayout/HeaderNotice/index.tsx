import { useEffect, useRef, useState, useCallback } from 'react';
import { SxwlIcon, SxwlRichTextViewer, SxwlButton, SxwlTooltip, SxwlModal, SxwlTag, SxwlSpace, SxwlText, SxwlBadge, SxwlPopover, SxwlEmpty, SxwlSpin } from '@/components';
import { useAuthStore } from '@/stores/authStore';
import { useSSE } from '@/hooks/useSSE';
import {
  getUnreadCount, getUnreadList, getNoticeById,
  markAsRead, markAllAsRead,
} from '@/api/system/noticeApi';
import type { SysNoticeUnreadItem, SysNoticeItem } from '@/api/system/noticeApi';
import './index.scss';



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
    <div className="sxwl-notice-popover">
      <div className="sxwl-notice-popover-header">
        <SxwlSpace size={8}>
          <SxwlText strong>消息通知</SxwlText>
          <SxwlTooltip title={sseState === 'connected' ? '实时连接中' : sseState === 'connecting' ? '连接中...' : '连接断开，轮询中'}>
            <span className={`sxwl-notice-sse-dot ${sseState}`} />
          </SxwlTooltip>
        </SxwlSpace>
        {unreadCount > 0 && (
          <SxwlButton type="link" size="small" loading={readAllLoading} onClick={handleMarkAllRead}>
            全部已读
          </SxwlButton>
        )}
      </div>
      {loading ? (
        <div className="sxwl-notice-popover-loading">
          <SxwlSpin size="small" />
        </div>
      ) : noticeList.length === 0 ? (
        <SxwlEmpty description="暂无通知" image={SxwlEmpty.PRESENTED_IMAGE_SIMPLE} />
      ) : (
        <div className="sxwl-notice-list">
          {noticeList.map((item) => (
              <div
                key={item.id}
                className={`sxwl-notice-list-item${item.readFlag === 0 ? ' unread' : ''}`}
                onClick={() => handlePreview(item)}
              >
                <div className="sxwl-notice-list-item-title">
                {item.readFlag === 0 && <span className="sxwl-notice-dot" />}
                <SxwlText
                  strong={item.readFlag === 0}
                  ellipsis={{ tooltip: item.title }}
                  style={{ maxWidth: 220 }}
                >
                  {item.title}
                </SxwlText>
              </div>
              <div className="sxwl-notice-list-item-desc">
                <SxwlTag color={LEVEL_COLOR[item.level] || 'blue'} style={{ fontSize: 11, lineHeight: '18px' }}>
                  {LEVEL_LABEL[item.level] || item.level}
                </SxwlTag>
                <SxwlText type="secondary" style={{ fontSize: 12 }}>
                  {item.createTime}
                </SxwlText>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );

  return (
    <>
      {/* SSE 状态指示器（独立于 Popover，点击只显示连接状态） */}
      <span className="sxwl-notice-sse-wrapper">
        <SxwlTooltip title={sseState === 'connected' ? '实时连接中' : sseState === 'connecting' ? '连接中...' : '连接断开，轮询中'}>
          <span className={`sxwl-notice-sse-dot ${sseState}`} />
        </SxwlTooltip>
      </span>
      <SxwlPopover
        content={popoverContent}
        trigger="click"
        placement="bottomRight"
        open={open}
        onOpenChange={handleOpenChange}
        classNames={{ root: 'sxwl-notice-overlay' }}
      >
        <div className="sxwl-notice-trigger">
          <SxwlBadge count={unreadCount} showZero={false} size="small" offset={[-2, 2]} className="sxwl-notice-badge">
            <SxwlTooltip title="消息通知">
              <SxwlButton type="text" className="sxwl-notice-btn" icon={<SxwlIcon name="BellOutlined" />} />
            </SxwlTooltip>
          </SxwlBadge>
        </div>
      </SxwlPopover>

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
            <SxwlSpin />
          </div>
        ) : previewNotice ? (
          <div>
            <SxwlSpace size={8} style={{ marginBottom: 16 }}>
              <SxwlTag color={LEVEL_COLOR[previewNotice.level] || 'blue'}>
                {LEVEL_LABEL[previewNotice.level] || previewNotice.level}
              </SxwlTag>
              <SxwlText type="secondary">{previewNotice.createTime}</SxwlText>
            </SxwlSpace>
            <SxwlRichTextViewer content={previewNotice.content} />
          </div>
        ) : (
          <SxwlEmpty description="加载失败" image={SxwlEmpty.PRESENTED_IMAGE_SIMPLE} />
        )}
      </SxwlModal>
    </>
  );
}

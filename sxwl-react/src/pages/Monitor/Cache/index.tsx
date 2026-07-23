import { useState, useEffect, useCallback } from 'react';
import type { ColumnsType } from 'antd/es/table';
import {
  SxwlButton, SxwlIcon, SxwlTag, SxwlTabs,
  SxwlSpace, SxwlPopconfirm, SxwlMessage, SxwlModal,
  SxwlPage,
  type ToolbarButtonConfig,
} from '@/components';
import type { SysCacheCategoryItem, SysCacheKeyDetailItem } from '@/api/monitor/cacheApi';
import { getCacheCategories, getCacheKeys, clearCacheByName, clearCacheByKey, getCacheKeyDetail } from '@/api/monitor/cacheApi';

export default function CachePage() {
  const [categories, setCategories] = useState<SysCacheCategoryItem[]>([]);
  const [activeCategory, setActiveCategory] = useState<string>('');
  const [keys, setKeys] = useState<SysCacheKeyDetailItem[]>([]);
  const [keysLoading, setKeysLoading] = useState(false);
  const [detailModal, setDetailModal] = useState<{ visible: boolean; detail: SysCacheKeyDetailItem | null }>({ visible: false, detail: null });

  // 加载缓存分类
  const loadCategories = useCallback(async () => {
    try {
      const res = await getCacheCategories();
      const list = res.data.data || [];
      setCategories(list);
      if (list.length > 0 && !activeCategory) {
        setActiveCategory(list[0].keyPrefix);
      }
    } catch {
      SxwlMessage.error('获取缓存分类失败');
    }
  }, [activeCategory]);

  useEffect(() => { loadCategories(); }, []);

  // 加载当前分类下的 Key 列表
  const loadKeys = useCallback(async () => {
    if (!activeCategory) return;
    setKeysLoading(true);
    try {
      const res = await getCacheKeys(activeCategory);
      setKeys(res.data.data || []);
    } catch {
      setKeys([]);
    } finally {
      setKeysLoading(false);
    }
  }, [activeCategory]);

  useEffect(() => { loadKeys(); }, [loadKeys]);

  // 清空分类
  const handleClearCategory = async () => {
    const cat = categories.find(c => c.keyPrefix === activeCategory);
    try {
      await clearCacheByName(activeCategory);
      SxwlMessage.success(`已清空「${cat?.name || activeCategory}」缓存`);
      loadKeys();
    } catch {
      SxwlMessage.error('清空缓存失败');
    }
  };

  // 删除单个 Key
  const handleClearKey = async (key: string) => {
    try {
      await clearCacheByKey(key);
      SxwlMessage.success('已删除该缓存 Key');
      loadKeys();
    } catch {
      SxwlMessage.error('删除失败');
    }
  };

  // 查看 Key 详情
  const handleViewDetail = async (key: string) => {
    try {
      const res = await getCacheKeyDetail(key);
      setDetailModal({ visible: true, detail: res.data.data });
    } catch {
      SxwlMessage.error('获取 Key 详情失败');
    }
  };

  const columns: ColumnsType<SysCacheKeyDetailItem> = [
    { title: 'Key 名称', dataIndex: 'key', key: 'key', width: 360, ellipsis: true },
    {
      title: '类型', dataIndex: 'type', key: 'type', width: 100,
      render: (type: string) => {
        const colorMap: Record<string, string> = { string: 'blue', hash: 'orange', set: 'green', zset: 'purple' };
        return <SxwlTag color={colorMap[type] || 'default'}>{type}</SxwlTag>;
      },
    },
    { title: 'TTL（秒）', dataIndex: 'ttl', key: 'ttl', width: 100 },
    {
      title: '操作', key: 'action', width: 180,
      render: (_, record) => (
        <SxwlSpace>
          <SxwlButton type="link" size="small" icon={<SxwlIcon name="EyeOutlined" />} onClick={() => handleViewDetail(record.key)}>
            详情
          </SxwlButton>
          <SxwlPopconfirm title="确定删除该缓存 Key 吗？" onConfirm={() => handleClearKey(record.key)}>
            <SxwlButton type="link" size="small" danger icon={<SxwlIcon name="DeleteOutlined" />}>
              删除
            </SxwlButton>
          </SxwlPopconfirm>
        </SxwlSpace>
      ),
    },
  ];

  const handleClearCategoryClick = () => {
    const cat = categories.find(c => c.keyPrefix === activeCategory);
    SxwlModal.confirm({
      title: '确定清空该分类下的所有缓存吗？',
      content: `分类：${cat?.name || activeCategory}`,
      onOk: handleClearCategory,
    });
  };

  const toolbarButtons: ToolbarButtonConfig[] = [
    { label: '清空此分类', icon: 'DeleteOutlined', danger: true, onClick: handleClearCategoryClick },
  ];

  const tabItems = categories.map(cat => ({
    key: cat.keyPrefix,
    label: cat.name,
  }));

  return (
    <>
      <div style={{ padding: '16px 24px 0' }}>
        <SxwlTabs
          activeKey={activeCategory}
          items={tabItems}
          onChange={(key) => setActiveCategory(key)}
        />
      </div>

      <SxwlPage
        mode="table"
        paginated={false}
        rowKey="key"
        columns={columns}
        dataSource={keys}
        loading={keysLoading}
        breadcrumb={['监控运维', '缓存管理']}
        scroll={{ x: 800 }}
        toolbarButtons={toolbarButtons}
      />

      <SxwlModal
        title="缓存 Key 详情"
        open={detailModal.visible}
        onCancel={() => setDetailModal({ visible: false, detail: null })}
        footer={null}
        width={640}
      >
        {detailModal.detail && (
          <div>
            <p><strong>Key：</strong>{detailModal.detail.key}</p>
            <p><strong>类型：</strong><SxwlTag>{detailModal.detail.type}</SxwlTag></p>
            <p><strong>TTL：</strong>{detailModal.detail.ttl} 秒</p>
            <p><strong>Value：</strong></p>
            <pre style={{
              background: '#f5f5f5',
              padding: 12,
              borderRadius: 4,
              maxHeight: 300,
              overflow: 'auto',
              fontSize: 13,
            }}>
              {JSON.stringify(detailModal.detail.value, null, 2)}
            </pre>
          </div>
        )}
      </SxwlModal>
    </>
  );
}

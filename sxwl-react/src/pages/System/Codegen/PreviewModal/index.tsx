import { useState, useEffect, useCallback } from 'react';
import { SxwlModal, SxwlTable, SxwlMessage, SxwlMarkdown } from '@/components';
import type { CodegenPreviewItem } from '@/api/codegen/codegenApi';
import { previewCodegen } from '@/api/codegen/codegenApi';

// ==================== Types

export interface PreviewModalProps {
  /** 表配置 ID */
  tableId: number;
  /** 表名（展示用） */
  tableName: string;
  /** 是否打开 */
  open: boolean;
  /** 关闭回调 */
  onCancel: () => void;
}

// ==================== Component

function PreviewModal({ tableId, tableName, open, onCancel }: PreviewModalProps) {
  const [previewList, setPreviewList] = useState<CodegenPreviewItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [selectedFile, setSelectedFile] = useState<string>('');

  const loadPreview = useCallback(async () => {
    setLoading(true);
    try {
      const res = await previewCodegen(tableId);
      setPreviewList(res.data.data);
      if (res.data.data.length > 0) {
        setSelectedFile(res.data.data[0].filePath);
      }
    } catch {
      SxwlMessage.error('获取预览失败');
    } finally {
      setLoading(false);
    }
  }, [tableId]);

  useEffect(() => {
    if (open) {
      loadPreview();
    }
  }, [open, loadPreview]);

  const selectedContent = previewList.find((f) => f.filePath === selectedFile)?.content || '';

  const columns = [
    {
      title: '文件路径',
      dataIndex: 'filePath',
      key: 'filePath',
      render: (path: string) => (
        <a
          onClick={() => setSelectedFile(path)}
          style={{ fontWeight: selectedFile === path ? 'bold' : 'normal' }}
        >
          {path}
        </a>
      ),
    },
    {
      title: '内容预览',
      dataIndex: 'content',
      key: 'content',
      ellipsis: true,
      width: 200,
    },
  ];

  return (
    <SxwlModal
      title={`代码预览 - ${tableName}`}
      open={open}
      onCancel={onCancel}
      footer={null}
      width="90%"
      style={{ maxWidth: 1200 }}
      destroyOnHidden
    >
      {previewList.length > 0 ? (
        <div style={{ display: 'flex', gap: 16 }}>
          <div style={{ width: 300, flexShrink: 0 }}>
            <SxwlTable
              rowKey="filePath"
              columns={columns}
              dataSource={previewList}
              loading={loading}
              pagination={false}
              size="small"
              scroll={{ y: 500 }}
            />
          </div>
          <div style={{ flex: 1, overflow: 'auto', maxHeight: 600 }}>
            <SxwlMarkdown content={"```\n" + selectedContent + "\n```"} maxHeight={580} />
          </div>
        </div>
      ) : (
        !loading && <div style={{ textAlign: 'center', padding: 40, color: '#999' }}>暂无预览数据</div>
      )}
    </SxwlModal>
  );
}

export default PreviewModal;

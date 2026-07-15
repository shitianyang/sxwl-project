// ============================================
// SxwlFilePreview — 文件预览弹窗
//
// 根据文件后缀自动选择预览方式：
// - 图片：使用 antd Image 组件
// - PDF：使用 iframe 浏览器原生 PDF 查看器
// - Office / 文本 / 其他：展示元信息 + 下载按钮
// ============================================

import { useState, useEffect } from 'react';
import { Modal, Button, Skeleton, Typography } from 'antd';
import type { SysFileDTO } from '@/api/system/fileApi';
import { getPresignedUrl, downloadFile } from '@/api/system/fileApi';
import SxwlIcon from '../SxwlIcon';

const { Text, Title } = Typography;

/** 判断是否为图片类型 */
function isImage(suffix: string): boolean {
  return ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp', 'svg', 'ico'].includes(suffix.toLowerCase());
}

/** 判断是否为 PDF */
function isPdf(suffix: string): boolean {
  return suffix.toLowerCase() === 'pdf';
}

/** 判断是否为 Office 文档 */
function isOffice(suffix: string): boolean {
  const officeSuffixes = ['doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx'];
  return officeSuffixes.includes(suffix.toLowerCase());
}

/** 判断是否为文本文件 */
function isText(suffix: string): boolean {
  return ['txt', 'md', 'json', 'xml', 'yaml', 'yml', 'csv', 'log', 'sql', 'js', 'ts', 'java', 'py', 'html', 'css'].includes(
    suffix.toLowerCase(),
  );
}

/** 格式化文件大小 */
function formatFileSize(bytes: number): string {
  if (bytes < 1024) return `${bytes} B`;
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
  if (bytes < 1024 * 1024 * 1024) return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
  return `${(bytes / (1024 * 1024 * 1024)).toFixed(1)} GB`;
}

export interface SxwlFilePreviewProps {
  /** 是否显示弹窗 */
  open: boolean;
  /** 关闭回调 */
  onClose: () => void;
  /** 文件信息（从 SysFileDTO 获取） */
  file: SysFileDTO | null;
}

const SxwlFilePreview: React.FC<SxwlFilePreviewProps> = ({ open, onClose, file }) => {
  const [loading, setLoading] = useState(false);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);

  useEffect(() => {
    if (open && file) {
      if (file.presignedUrl) {
        setPreviewUrl(file.presignedUrl);
      } else {
        loadPreviewUrl();
      }
    } else {
      setPreviewUrl(null);
    }
  }, [open, file]);

  async function loadPreviewUrl() {
    if (!file) return;
    setLoading(true);
    try {
      const res = await getPresignedUrl(file.id);
      setPreviewUrl(res.data.data);
    } catch {
      setPreviewUrl(null);
    } finally {
      setLoading(false);
    }
  }

  const suffix = file?.fileSuffix ?? '';
  const fileName = file?.fileName ?? '';
  const fileSize = file?.fileSize ?? 0;

  /** 渲染元信息 */
  const renderMeta = () => (
    <div style={{ textAlign: 'center', padding: '40px 0' }}>
      <SxwlIcon name="FileOutlined" size={64} style={{ color: '#999' }} />
      <Title level={5} style={{ marginTop: 16 }}>{fileName}</Title>
      <Text type="secondary">
        {suffix.toUpperCase()} 文件 · {formatFileSize(fileSize)}
      </Text>
      <br />
      <Button
        type="primary"
        icon={<SxwlIcon name="DownloadOutlined" />}
        style={{ marginTop: 16 }}
        onClick={() => file && downloadFile(file.id)}
      >
        下载文件
      </Button>
    </div>
  );

  /** 渲染器选择 */
  const renderPreview = () => {
    if (!file || !previewUrl) return renderMeta();

    if (isImage(suffix)) {
      return (
        <div style={{ textAlign: 'center', padding: 16 }}>
          <img
            src={previewUrl}
            alt={fileName}
            style={{ maxWidth: '100%', maxHeight: '70vh', objectFit: 'contain' }}
          />
        </div>
      );
    }

    if (isPdf(suffix)) {
      return (
        <iframe
          src={previewUrl}
          style={{ width: '100%', height: '70vh', border: 'none' }}
          title={fileName}
        />
      );
    }

    if (isOffice(suffix)) {
      // Office 文件：尝试 Office Online Viewer，失败则降级为元信息
      return (
        <div>
          <iframe
            src={`https://view.officeapps.live.com/op/embed.aspx?src=${encodeURIComponent(previewUrl)}`}
            style={{ width: '100%', height: '70vh', border: 'none' }}
            title={fileName}
            onError={() => setPreviewUrl(null)}
          />
        </div>
      );
    }

    if (isText(suffix)) {
      return (
        <iframe
          src={previewUrl}
          style={{ width: '100%', height: '70vh', border: 'none' }}
          title={fileName}
        />
      );
    }

    // 其他类型：展示元信息 + 下载
    return renderMeta();
  };

  return (
    <Modal
      title={
        <span>
          <SxwlIcon name="EyeOutlined" style={{ marginRight: 8 }} />
          {fileName || '文件预览'}
        </span>
      }
      open={open}
      onCancel={onClose}
      footer={
        file ? (
          <Button icon={<SxwlIcon name="DownloadOutlined" />} onClick={() => downloadFile(file.id)}>
            下载文件
          </Button>
        ) : null
      }
      width={800}
      destroyOnClose
    >
      {loading ? <Skeleton active /> : renderPreview()}
    </Modal>
  );
};

export default SxwlFilePreview;

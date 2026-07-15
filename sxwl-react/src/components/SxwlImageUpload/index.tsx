// ============================================
// SxwlImageUpload — 图片上传组件
//
// 继承 SxwlUpload，预设图片上传相关属性：
// - accept="image/*" + listType="picture-card" 画廊模式
// - 默认最大 10MB
// - 上传后自动通过 antd Image 支持放大预览
// ============================================

import { useState, useCallback } from 'react';
import { Image } from 'antd';
import type { SxwlResult } from '@/api/http';
import SxwlUpload from '@/components/SxwlUpload';
import type { SxwlUploadProps } from '@/components/SxwlUpload';

export interface SxwlImageUploadProps extends SxwlUploadProps {
  /** 图片最大大小（MB），默认 10 */
  maxSize?: number;
}

const SxwlImageUpload: React.FC<SxwlImageUploadProps> = ({
  maxSize = 10,
  listType = 'picture-card',
  accept = 'image/*',
  onChange,
  ...rest
}) => {
  const [previewOpen, setPreviewOpen] = useState(false);
  const [previewUrl, setPreviewUrl] = useState('');

  const handleChange: SxwlUploadProps['onChange'] = useCallback(
    (info) => {
      // 上传成功后记录预览 URL
      if (info.file.status === 'done') {
        const response = info.file.response as SxwlResult<{ presignedUrl: string }> | undefined;
        if (response?.data?.presignedUrl) {
          info.file.url = response.data.presignedUrl;
        }
      }
      onChange?.(info);
    },
    [onChange],
  );

  const handlePreview = useCallback(async (file: any) => {
    if (file.url || file.thumbUrl) {
      setPreviewUrl(file.url || file.thumbUrl);
      setPreviewOpen(true);
    }
  }, []);

  return (
    <>
      <SxwlUpload
        listType={listType}
        accept={accept}
        maxSize={maxSize}
        onChange={handleChange}
        onPreview={handlePreview}
        {...rest}
      />
      {previewUrl && (
        <Image
          style={{ display: 'none' }}
          src={previewUrl}
          preview={{
            visible: previewOpen,
            src: previewUrl,
            onVisibleChange: (vis) => setPreviewOpen(vis),
          }}
        />
      )}
    </>
  );
};

export default SxwlImageUpload;

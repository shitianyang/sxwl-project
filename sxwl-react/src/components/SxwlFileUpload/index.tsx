// ============================================
// SxwlFileUpload — 文件上传组件（Excel/Word/PDF）
//
// 继承 SxwlUpload，预设文档上传相关属性：
// - accept 限定办公文档 + PDF
// - listType="text" 列表模式，展示文件名/大小/后缀
// - 内置预览按钮，点击打开 SxwlFilePreview
// ============================================

import { useState, useCallback } from 'react';
import SxwlUpload from '@/components/SxwlUpload';
import type { SxwlUploadProps } from '@/components/SxwlUpload';
import type { SxwlResult } from '@/api/http';
import SxwlFilePreview from '@/components/SxwlFilePreview';

export interface SxwlFileUploadProps extends SxwlUploadProps {
  /** 文件最大大小（MB），默认 100 */
  maxSize?: number;
}

/** 从 antd UploadFile 的 response 中提取 SysFileDTO-like 对象 */
function extractDto(file: any): {
  id: number;
  fileName: string;
  fileSuffix: string;
  fileSize: number;
  presignedUrl: string;
} | null {
  const response = file?.response as SxwlResult<{
    id: number;
    fileName: string;
    fileSuffix: string;
    fileSize: number;
    presignedUrl: string;
  }> | undefined;
  return response?.data ?? null;
}

const SxwlFileUpload: React.FC<SxwlFileUploadProps> = ({
  maxSize = 100,
  accept = '.xlsx,.xls,.doc,.docx,.pdf,.ppt,.pptx,.txt,.csv',
  listType = 'text',
  onChange,
  ...rest
}) => {
  const [previewOpen, setPreviewOpen] = useState(false);
  const [previewFile, setPreviewFile] = useState<any>(null);

  const handleChange: SxwlUploadProps['onChange'] = useCallback(
    (info) => {
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
    setPreviewFile(file);
    setPreviewOpen(true);
  }, []);

  const dto = extractDto(previewFile);

  return (
    <>
      <SxwlUpload
        accept={accept}
        listType={listType}
        maxSize={maxSize}
        onChange={handleChange}
        onPreview={handlePreview}
        {...rest}
      />
      <SxwlFilePreview
        open={previewOpen}
        onClose={() => setPreviewOpen(false)}
        file={
          dto
            ? {
                id: dto.id,
                fileName: dto.fileName,
                fileSuffix: dto.fileSuffix,
                fileSize: dto.fileSize,
                presignedUrl: dto.presignedUrl,
                fileUrl: '',
                fileType: '',
                createTime: '',
              }
            : null
        }
      />
    </>
  );
};

export default SxwlFileUpload;

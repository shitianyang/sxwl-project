// ============================================
// SxwlUpload — 通用上传组件
//
// 基于 antd Upload 二次封装，连接 RustFS 文件存储后端。
// 支持文件大小校验，自动处理 SxwlResult 响应格式，
// 上传成功后自动将 presignedUrl 设为 file.url 用于预览。
// ============================================

import { Upload, message } from 'antd';
import type { UploadProps, UploadFile } from 'antd';
import type { SxwlResult } from '@/api/http';

/** 上传文件列表项，携带 SysFileDTO 的 response 类型 */
export type SxwlUploadFile = UploadFile<SxwlResult<{
  id: number;
  fileName: string;
  fileUrl: string;
  fileSize: number;
  fileType: string;
  fileSuffix: string;
  createTime: string;
  presignedUrl: string;
}>>;

export interface SxwlUploadProps extends UploadProps {
  /** 最大文件大小（MB），默认 100 */
  maxSize?: number;
}

const DEFAULT_MAX_SIZE = 100;

const SxwlUpload: React.FC<SxwlUploadProps> = ({
  maxSize = DEFAULT_MAX_SIZE,
  action = '/sxwl-api/rustfs/file/simple',
  name = 'file',
  onChange,
  beforeUpload,
  ...rest
}) => {
  const handleBeforeUpload: UploadProps['beforeUpload'] = (file, fileList) => {
    if (file.size > maxSize * 1024 * 1024) {
      message.error(`文件大小不能超过 ${maxSize}MB`);
      return Upload.LIST_IGNORE;
    }
    return beforeUpload?.(file, fileList) ?? true;
  };

  const handleChange: UploadProps['onChange'] = (info) => {
    const { file } = info;

    if (file.status === 'done') {
      const response = file.response as SxwlResult<{ presignedUrl: string }> | undefined;
      if (response?.data?.presignedUrl) {
        file.url = response.data.presignedUrl;
      }
    }

    onChange?.(info);
  };

  return (
    <Upload
      action={action}
      name={name}
      onChange={handleChange}
      beforeUpload={handleBeforeUpload}
      {...rest}
    />
  );
};

export default SxwlUpload;

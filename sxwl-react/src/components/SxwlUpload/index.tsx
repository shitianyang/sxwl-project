// ============================================
// SxwlUpload — 通用上传组件
//
// 基于 antd Upload 二次封装，连接 RustFS 文件存储后端。
// 支持文件大小校验，自动处理 SxwlResult 响应格式，
// 上传成功后自动将 presignedUrl 设为 file.url 用于预览。
//
// 大文件自动走分片上传（MD5 秒传/断点续传/并发控制）。
// ============================================

import { useState, useCallback, useRef, useEffect } from 'react';
import { Upload, message } from 'antd';
import type { UploadProps, UploadFile } from 'antd';
import type { SxwlResult } from '@/api/http';
import { useChunkedUpload } from '@/hooks/useChunkedUpload';

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
  /** 分片上传阈值（MB），超过此大小的文件自动分片上传，默认 100（设为 0 禁用分片） */
  chunkThreshold?: number;
}

const DEFAULT_MAX_SIZE = 100;
const DEFAULT_CHUNK_THRESHOLD = 100;

const SxwlUpload: React.FC<SxwlUploadProps> = ({
  maxSize = DEFAULT_MAX_SIZE,
  chunkThreshold = DEFAULT_CHUNK_THRESHOLD,
  action = '/sxwl-api/rustfs/file/simple',
  name = 'file',
  fileList: controlledFileList,
  onChange,
  beforeUpload,
  ...rest
}) => {
  const [internalFileList, setInternalFileList] = useState<UploadFile[]>([]);
  const fileList = controlledFileList ?? internalFileList;
  const { start, progress, status } = useChunkedUpload();
  const progressFileRef = useRef<UploadFile | null>(null);

  // 通过 useEffect 同步分片上传进度到 fileList
  useEffect(() => {
    const pf = progressFileRef.current;
    if (!pf) return;
    const newStatus = status === 'success' ? 'done' : (status === 'error' || status === 'canceled') ? 'error' : 'uploading';
    if (pf.percent === Math.round(progress) && pf.status === newStatus) return;
    pf.percent = Math.round(progress);
    pf.status = newStatus;
    setInternalFileList((prev) =>
      prev.map((f) => (f.uid === pf.uid ? { ...pf } : f))
    );
  }, [progress, status]);

  const startChunkedUpload = useCallback(async (file: File) => {
    const uploadFile: UploadFile = {
      uid: `chunked-${file.name}-${Date.now()}`,
      name: file.name,
      size: file.size,
      type: file.type,
      status: 'uploading',
      percent: 0,
    };

    progressFileRef.current = uploadFile;
    setInternalFileList((prev) => [...prev, uploadFile]);

    await start(file, {
      // 分片上传内部的进度回调
      onProgress: (pct: number) => {
        if (progressFileRef.current) {
          progressFileRef.current.percent = Math.round(pct);
          progressFileRef.current.status = 'uploading';
          setInternalFileList((prev) =>
            prev.map((f) =>
              f.uid === progressFileRef.current!.uid
                ? { ...progressFileRef.current! }
                : f
            )
          );
        }
      },
      onSuccess: (result) => {
        const pf = progressFileRef.current;
        if (!pf) return;
        pf.status = 'done';
        pf.percent = 100;
        pf.response = {
          data: {
            id: result.id,
            fileName: result.fileName,
            fileUrl: result.fileUrl,
            fileSize: result.fileSize,
            fileType: result.fileType,
            fileSuffix: result.fileSuffix,
            createTime: result.createTime,
            presignedUrl: result.presignedUrl,
          },
        } as any;
        pf.url = result.presignedUrl;
        progressFileRef.current = null;
        setInternalFileList((prev) =>
          prev.map((f) => (f.uid === pf.uid ? { ...pf } : f))
        );
        onChange?.({
          file: pf,
          fileList: [],
        } as any);
      },
      onError: (err) => {
        const pf = progressFileRef.current;
        if (!pf) return;
        pf.status = 'error';
        pf.percent = 0;
        pf.response = { message: err.message } as any;
        progressFileRef.current = null;
        setInternalFileList((prev) =>
          prev.map((f) => (f.uid === pf.uid ? { ...pf } : f))
        );
        message.error(`上传失败: ${err.message}`);
      },
    });
  }, [start, onChange]);

  const handleBeforeUpload: UploadProps['beforeUpload'] = (file, files) => {
    // 大小校验
    if (file.size > maxSize * 1024 * 1024) {
      message.error(`文件大小不能超过 ${maxSize}MB`);
      return Upload.LIST_IGNORE;
    }

    // 大文件走分片上传
    if (chunkThreshold > 0 && file.size > chunkThreshold * 1024 * 1024) {
      startChunkedUpload(file);
      return Upload.LIST_IGNORE;
    }

    return beforeUpload?.(file, files) ?? true;
  };

  const handleChange: UploadProps['onChange'] = (info) => {
    const { file } = info;

    if (file.status === 'done') {
      const response = file.response as SxwlResult<{ presignedUrl: string }> | undefined;
      if (response?.data?.presignedUrl) {
        file.url = response.data.presignedUrl;
      }
    }

    if (!controlledFileList) {
      setInternalFileList(info.fileList);
    }
    onChange?.(info);
  };

  return (
    <Upload
      action={action}
      name={name}
      fileList={fileList}
      onChange={handleChange}
      beforeUpload={handleBeforeUpload}
      {...rest}
    />
  );
};

export default SxwlUpload;

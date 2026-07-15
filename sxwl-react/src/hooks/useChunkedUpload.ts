// ============================================
// useChunkedUpload — 分片上传 Hook
//
// 职责：
// 1. 文件 MD5 计算（增量读取，支持大文件）
// 2. 秒传检查（checkMd5 去重）
// 3. 断点续传（查询已上传分片，只传缺失的）
// 4. 并发分片上传（控制并发数）
// 5. 进度回调
// ============================================

import { useState, useCallback, useRef } from 'react';
import SparkMD5 from 'spark-md5';
import type { SysFileDTO } from '@/api/system/fileApi';
import {
  simpleUpload,
  initUpload,
  uploadChunk,
  getUploadedChunks,
  completeUpload,
  checkMd5,
} from '@/api/system/fileApi';

/** 单个分片大小：5MB */
const CHUNK_SIZE = 5 * 1024 * 1024;
/** 最大并发数 */
const CONCURRENCY = 3;

/** 上传状态 */
export type UploadStatus = 'idle' | 'calculating' | 'uploading' | 'success' | 'error' | 'canceled';

/** 任务选项 */
export interface UploadTaskOptions {
  onProgress?: (percent: number) => void;
  onSuccess?: (result: SysFileDTO) => void;
  onError?: (error: Error) => void;
}

/**
 * 增量计算文件 MD5
 */
function computeFileMd5(file: File): Promise<string> {
  return new Promise((resolve, reject) => {
    const spark = new SparkMD5.ArrayBuffer();
    const reader = new FileReader();
    const sliceSize = 2 * 1024 * 1024; // 每次读取 2MB
    let offset = 0;

    reader.onerror = () => reject(new Error('文件读取失败'));
    reader.onload = (e) => {
      spark.append(e.target!.result as ArrayBuffer);
      offset += sliceSize;
      if (offset < file.size) {
        readNext();
      } else {
        resolve(spark.end());
      }
    };

    const readNext = () => {
      const slice = file.slice(offset, Math.min(offset + sliceSize, file.size));
      reader.readAsArrayBuffer(slice);
    };

    readNext();
  });
}

/**
 * 计算 Blob 的 MD5
 */
function computeBlobMd5(blob: Blob): Promise<string> {
  return new Promise((resolve, reject) => {
    const spark = new SparkMD5.ArrayBuffer();
    const reader = new FileReader();
    reader.onload = (e) => {
      spark.append(e.target!.result as ArrayBuffer);
      resolve(spark.end());
    };
    reader.onerror = () => reject(new Error('分片读取失败'));
    reader.readAsArrayBuffer(blob);
  });
}

export function useChunkedUpload() {
  const [progress, setProgress] = useState(0);
  const [status, setStatus] = useState<UploadStatus>('idle');
  const cancelRef = useRef(false);
  const progressRef = useRef(0);

  const updateProgress = useCallback((pct: number) => {
    progressRef.current = pct;
    setProgress(pct);
  }, []);

  /** 开始上传 */
  const start = useCallback(async (file: File, options?: UploadTaskOptions) => {
    cancelRef.current = false;
    setStatus('calculating');
    updateProgress(0);

    // ----- 1. 计算文件 MD5 -----
    let fileMd5: string;
    try {
      fileMd5 = await computeFileMd5(file);
    } catch (err) {
      setStatus('error');
      options?.onError?.(err as Error);
      return;
    }

    if (cancelRef.current) { setStatus('canceled'); return; }

    // ----- 2. 小文件走简单上传 -----
    if (file.size < CHUNK_SIZE) {
      setStatus('uploading');
      try {
        const res = await simpleUpload(file);
        updateProgress(100);
        setStatus('success');
        options?.onProgress?.(100);
        options?.onSuccess?.(res.data.data);
        return;
      } catch (err) {
        setStatus('error');
        options?.onError?.(err as Error);
        return;
      }
    }

    // ----- 3. 秒传检查（MD5 去重） -----
    try {
      const dedupRes = await checkMd5(fileMd5);
      if (dedupRes.data.data) {
        updateProgress(100);
        setStatus('success');
        options?.onProgress?.(100);
        options?.onSuccess?.(dedupRes.data.data);
        return;
      }
    } catch {
      // 查询失败不影响后续上传
    }

    if (cancelRef.current) { setStatus('canceled'); return; }

    // ----- 4. 断点续传检查 -----
    const totalChunks = Math.ceil(file.size / CHUNK_SIZE);
    let uploadId: number | undefined;
    let uploadedChunks: number[] = [];

    try {
      const resumeRes = await getUploadedChunks(fileMd5);
      if (resumeRes.data.data) {
        uploadId = resumeRes.data.data.uploadId;
        uploadedChunks = resumeRes.data.data.uploadedChunks;
      }
    } catch {
      // 无现有会话，后续会新建
    }

    if (cancelRef.current) { setStatus('canceled'); return; }

    // ----- 5. 初始化上传会话 -----
    if (uploadId === undefined) {
      try {
        const chunkSize = CHUNK_SIZE;
        const initRes = await initUpload({
          fileMd5,
          originalName: file.name,
          fileSize: file.size,
          contentType: file.type || 'application/octet-stream',
          totalChunks,
          chunkSize,
        });
        uploadId = initRes.data.data;
      } catch (err) {
        setStatus('error');
        options?.onError?.(err as Error);
        return;
      }
    }

    if (cancelRef.current) { setStatus('canceled'); return; }

    // ----- 6. 上传缺失分片（并发控制） -----
    setStatus('uploading');
    const uploadedSet = new Set(uploadedChunks);
    const pendingIndices = Array.from({ length: totalChunks }, (_, i) => i)
      .filter(i => !uploadedSet.has(i));

    try {
      await concurrentUpload(uploadId!, file, pendingIndices);

      if (cancelRef.current) { setStatus('canceled'); return; }

      // ----- 7. 合并分片 -----
      const completeRes = await completeUpload(uploadId!, fileMd5);
      updateProgress(100);
      setStatus('success');
      options?.onProgress?.(100);
      options?.onSuccess?.(completeRes.data.data);
    } catch (err) {
      if (cancelRef.current) {
        setStatus('canceled');
      } else {
        setStatus('error');
        options?.onError?.(err as Error);
      }
    }
  }, [updateProgress]);

  /** 取消上传 */
  const cancel = useCallback(() => {
    cancelRef.current = true;
    setStatus('canceled');
  }, []);

  /** 重置状态 */
  const reset = useCallback(() => {
    cancelRef.current = false;
    progressRef.current = 0;
    setProgress(0);
    setStatus('idle');
  }, []);

  return { start, cancel, reset, progress, status };
}

/**
 * 并发上传分片
 */
async function concurrentUpload(
  uploadId: number,
  file: File,
  indices: number[],
) {
  const queue = [...indices];
  let completedCount = 0;

  const worker = async () => {
    while (queue.length > 0) {
      const idx = queue.shift()!;
      const start = idx * CHUNK_SIZE;
      const end = Math.min(start + CHUNK_SIZE, file.size);
      const blob = file.slice(start, end);

      const chunkMd5 = await computeBlobMd5(blob);
      await uploadChunk(uploadId, idx, chunkMd5, blob);

      completedCount++;
    }
  };

  const workers = Array.from({ length: CONCURRENCY }, () => worker());
  await Promise.all(workers);
}

// ============================================
// RustFS 文件存储 API
// ============================================

import { http } from '@/api/http';

/** 系统文件信息响应 DTO */
export interface SysFileDTO {
  id: number;
  fileName: string;
  fileUrl: string;
  fileSize: number;
  fileType: string;
  fileSuffix: string;
  createTime: string;
  presignedUrl: string;
}

/** PageInfo 分页响应（与后端 com.github.pagehelper.PageInfo 对齐） */
export interface PageInfo<T> {
  list: T[];
  total: number;
  pageNum: number;
  pageSize: number;
  pages: number;
}

/** 分页查询参数 */
export interface SysFilePageParams {
  fileName?: string;
  businessType?: string;
  startTime?: string;
  endTime?: string;
  current: number;
  pageSize: number;
}

/** 初始化分片上传请求 */
export interface UploadInitDTO {
  fileMd5: string;
  originalName: string;
  fileSize: number;
  contentType: string;
  totalChunks: number;
  chunkSize: number;
}

/** 分片上传响应 */
export interface UploadChunkDTO {
  chunkIndex: number;
}

/** 续传查询响应 */
export interface ChunkCheckDTO {
  uploadId: number;
  uploadedChunks: number[];
}

/** 完成上传请求 */
export interface UploadCompleteDTO {
  uploadId: number;
  fileMd5: string;
}

/**
 * 简单上传（小文件不分片）
 */
export function simpleUpload(file: File) {
  const formData = new FormData();
  formData.append('file', file);
  return http.upload<SysFileDTO>('/rustfs/file/simple', formData);
}

/**
 * 初始化分片上传
 */
export function initUpload(data: UploadInitDTO) {
  return http.post<number>('/rustfs/file/upload/init', data);
}

/**
 * 上传分片
 */
export function uploadChunk(uploadId: number, chunkIndex: number, chunkMd5: string, blob: Blob) {
  const formData = new FormData();
  formData.append('uploadId', String(uploadId));
  formData.append('chunkIndex', String(chunkIndex));
  formData.append('chunkMd5', chunkMd5);
  formData.append('file', blob);
  return http.upload<UploadChunkDTO>('/rustfs/file/upload/chunk', formData);
}

/**
 * 查询已上传分片（断点续传）
 */
export function getUploadedChunks(md5: string) {
  return http.get<ChunkCheckDTO>(`/rustfs/file/upload/${md5}/chunks`);
}

/**
 * 合并分片完成上传
 */
export function completeUpload(uploadId: number, fileMd5: string) {
  return http.post<SysFileDTO>('/rustfs/file/upload/complete', { uploadId, fileMd5 });
}

/**
 * 秒传检查
 */
export function checkMd5(md5: string) {
  return http.get<SysFileDTO | null>('/rustfs/file/check-md5', { md5 });
}

/**
 * 获取预签名 URL
 */
export function getPresignedUrl(id: number) {
  return http.get<string>(`/rustfs/file/presigned-url/${id}`);
}

/**
 * 下载文件
 */
export function downloadFile(id: number) {
  return http.download(`/rustfs/file/download/${id}`);
}

/**
 * 分页查询文件列表
 */
export function getFilePageByParams(params: SysFilePageParams) {
  return http.get<PageInfo<SysFileDTO>>('/rustfs/file/page', params as unknown as Record<string, unknown>);
}

/**
 * 删除文件
 */
export function deleteFile(id: number) {
  return http.deleteReq(`/rustfs/file/${id}`);
}

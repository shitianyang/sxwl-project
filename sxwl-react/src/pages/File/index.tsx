// ============================================
// FilePage — 文件管理列表页
//
// 一级菜单，与系统管理、日志管理同级。
// 纯展示型列表页，仅提供查询、预览、下载、删除操作。
// 使用 SxwlPage 声明式布局，与其余列表页保持统一。
// ============================================

import { useState, useEffect, useCallback, useRef } from 'react';
import type { ColumnsType } from 'antd/es/table';
import {
  SxwlButton,
  SxwlIcon,
  SxwlTag,
  SxwlSpace,
  SxwlPage,
  SxwlPopconfirm,
  SxwlMessage,
  SxwlFilePreview,
  SxwlPermissionButton,
  type SearchFieldConfig,
} from '@/components';
import type { SysFileDTO } from '@/api/system/fileApi';
import { getFilePageByParams, downloadFile, deleteFile } from '@/api/system/fileApi';

/** 格式化文件大小 */
function formatFileSize(bytes: number): string {
  if (!bytes) return '-';
  if (bytes < 1024) return `${bytes} B`;
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
  if (bytes < 1024 * 1024 * 1024) return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
  return `${(bytes / (1024 * 1024 * 1024)).toFixed(1)} GB`;
}

const columns: ColumnsType<SysFileDTO> = [
  { title: '文件名', dataIndex: 'fileName', key: 'fileName', width: 300, ellipsis: true },
  {
    title: '类型',
    dataIndex: 'fileSuffix',
    key: 'fileSuffix',
    width: 80,
    render: (val: string) =>
      val ? <SxwlTag color="blue">{val.toUpperCase()}</SxwlTag> : '-',
  },
  {
    title: '文件大小',
    dataIndex: 'fileSize',
    key: 'fileSize',
    width: 100,
    render: (val: number) => formatFileSize(val),
  },
  { title: '上传时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
];

const searchFields: SearchFieldConfig[] = [
  { name: 'fileName', label: '文件名', type: 'input', placeholder: '请输入文件名' },
  {
    name: 'dateRange',
    label: '上传时间',
    type: 'dateRange',
    dateRangeStartKey: 'startTime',
    dateRangeEndKey: 'endTime',
  },
];

export default function FilePage() {
  const [data, setData] = useState<SysFileDTO[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [previewFile, setPreviewFile] = useState<SysFileDTO | null>(null);
  const [previewOpen, setPreviewOpen] = useState(false);

  const searchRef = useRef<Record<string, any>>({});

  const loadData = useCallback(
    async (queryPage?: number) => {
      setLoading(true);
      try {
        const res = await getFilePageByParams({
          ...searchRef.current,
          current: queryPage ?? page,
          pageSize,
        });
        setData(res.data.data.list);
        setTotal(res.data.data.total);
      } catch {
        SxwlMessage.error('查询文件列表失败');
      } finally {
        setLoading(false);
      }
    },
    [page, pageSize],
  );

  useEffect(() => {
    loadData();
  }, [loadData]);

  const handleSearch = (values: Record<string, any>) => {
    // dateRange 转换由 SxwlSearchForm 自动处理，无需手动映射
    searchRef.current = values;
    setPage(1);
    loadData(1);
  };

  const handleReset = () => {
    searchRef.current = {};
    setPage(1);
    loadData(1);
  };

  const handlePageChange = (newPage: number, newPageSize: number) => {
    setPage(newPage);
    setPageSize(newPageSize);
  };

  const handlePreview = (record: SysFileDTO) => {
    setPreviewFile(record);
    setPreviewOpen(true);
  };

  const handleDelete = async (id: number) => {
    try {
      await deleteFile(id);
      SxwlMessage.success('删除成功');
      loadData();
    } catch {
      SxwlMessage.error('删除失败');
    }
  };

  const actionColumn: ColumnsType<SysFileDTO>[number] = {
    title: '操作',
    key: 'action',
    width: 200,
    fixed: 'right',
    render: (_: unknown, record: SysFileDTO) => (
      <SxwlSpace>
        <SxwlButton
          type="link"
          size="small"
          icon={<SxwlIcon name="EyeOutlined" />}
          onClick={() => handlePreview(record)}
        >
          预览
        </SxwlButton>
        <SxwlButton
          type="link"
          size="small"
          icon={<SxwlIcon name="DownloadOutlined" />}
          onClick={() => downloadFile(record.id)}
        >
          下载
        </SxwlButton>
        <SxwlPopconfirm
          title="确认删除"
          description="删除后数据不可恢复，确定要删除该文件吗？"
          onConfirm={() => handleDelete(record.id)}
          okText="确定"
          cancelText="取消"
        >
          <SxwlPermissionButton type="link" size="small" danger icon={<SxwlIcon name="DeleteOutlined" />} permission="system:file:delete">
            删除
          </SxwlPermissionButton>
        </SxwlPopconfirm>
      </SxwlSpace>
    ),
  };

  return (
    <>
      <SxwlPage
        mode="table"
        paginated
        rowKey="id"
        columns={[...columns, actionColumn]}
        dataSource={data}
        loading={loading}
        total={total}
        page={page}
        pageSize={pageSize}
        breadcrumb={['文件管理']}
        searchFields={searchFields}
        scroll={{ x: 900 }}
        onSearch={handleSearch}
        onReset={handleReset}
        onPageChange={handlePageChange}
      />

      <SxwlFilePreview
        open={previewOpen}
        onClose={() => setPreviewOpen(false)}
        file={previewFile}
      />
    </>
  );
}

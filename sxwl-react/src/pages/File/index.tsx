// ============================================
// FilePage — 文件管理列表页
//
// 一级菜单，与系统管理、日志管理同级。
// 纯展示型列表页，仅提供查询、预览、下载、删除操作。
// ============================================

import { useState, useEffect, useCallback, useRef } from 'react';
import type { Dayjs } from 'dayjs';
import type { ColumnsType } from 'antd/es/table';
import {
  SxwlButton,
  SxwlIcon,
  SxwlTag,
  SxwlSpace,
  SxwlCard,
  SxwlForm,
  SxwlTable,
  SxwlInput,
  SxwlMessage,
  SxwlPopconfirm,
  SxwlRangePicker,
  SxwlFilePreview,
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
    searchRef.current = values;
    setPage(1);
    loadData(1);
  };

  const handleReset = () => {
    searchRef.current = {};
    setPage(1);
    loadData(1);
  };

  const [form] = SxwlForm.useForm();

  const onSearchClick = () => {
    const values = form.getFieldsValue();
    const dateRange = values.dateRange as [Dayjs, Dayjs] | undefined;
    if (dateRange && dateRange[0] && dateRange[1]) {
      values.startTime = dateRange[0].format('YYYY-MM-DD HH:mm:ss');
      values.endTime = dateRange[1].format('YYYY-MM-DD HH:mm:ss');
    } else {
      values.startTime = undefined;
      values.endTime = undefined;
    }
    delete values.dateRange;
    handleSearch(values);
  };

  const onResetClick = () => {
    form.resetFields();
    handleReset();
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
          <SxwlButton type="link" size="small" danger icon={<SxwlIcon name="DeleteOutlined" />}>
            删除
          </SxwlButton>
        </SxwlPopconfirm>
      </SxwlSpace>
    ),
  };

  return (
    <div className="sxwl-page">
      <div className="sxwl-page-breadcrumb">
        <span className="sxwl-page-breadcrumb-item is-current">文件管理</span>
      </div>

      <SxwlCard className="sxwl-search-form">
        <SxwlForm form={form} layout="inline" className="sxwl-search-form__inner">
          <SxwlForm.Item name="fileName" label="文件名">
            <SxwlInput placeholder="请输入" allowClear maxLength={128} />
          </SxwlForm.Item>
          <SxwlForm.Item name="dateRange" label="上传时间">
            <SxwlRangePicker style={{ width: 360 }} />
          </SxwlForm.Item>
          <SxwlForm.Item>
            <SxwlSpace>
              <SxwlButton
                type="primary"
                icon={<SxwlIcon name="SearchOutlined" />}
                onClick={onSearchClick}
              >
                查询
              </SxwlButton>
              <SxwlButton
                icon={<SxwlIcon name="ReloadOutlined" />}
                onClick={onResetClick}
              >
                重置
              </SxwlButton>
            </SxwlSpace>
          </SxwlForm.Item>
        </SxwlForm>
      </SxwlCard>

      <SxwlCard className="sxwl-page-table-card">
        <SxwlTable
          rowKey="id"
          columns={[...columns, actionColumn]}
          dataSource={data}
          loading={loading}
          pagination={{
            current: page,
            pageSize,
            total,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (t: number) => `共 ${t} 条`,
            onChange: handlePageChange,
          }}
          scroll={{ x: 900 }}
        />
      </SxwlCard>

      <SxwlFilePreview
        open={previewOpen}
        onClose={() => setPreviewOpen(false)}
        file={previewFile}
      />
    </div>
  );
}

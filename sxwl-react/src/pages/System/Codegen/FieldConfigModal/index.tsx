import { useState, useEffect, useCallback } from 'react';
// Switch, InputNumber 已通过 SxwlSwitch, SxwlInputNumber 封装
import type { ColumnsType } from 'antd/es/table';
import {
  SxwlModal, SxwlTable, SxwlMessage, SxwlTag,
  SxwlInput, SxwlSelect, SxwlSwitch, SxwlInputNumber,
} from '@/components';
import type { CodegenFieldItem } from '@/api/codegen/codegenApi';
import { getCodegenTableById, saveCodegenFields } from '@/api/codegen/codegenApi';

// ==================== Types

export interface FieldConfigModalProps {
  /** 表配置 ID */
  tableId: number;
  /** 表名（展示用） */
  tableName: string;
  /** 是否打开 */
  open: boolean;
  /** 关闭回调 */
  onCancel: () => void;
}

// ==================== Options

const javaTypeOptions = [
  { value: 'String', label: 'String' },
  { value: 'Integer', label: 'Integer' },
  { value: 'Long', label: 'Long' },
  { value: 'BigDecimal', label: 'BigDecimal' },
  { value: 'LocalDate', label: 'LocalDate' },
  { value: 'LocalDateTime', label: 'LocalDateTime' },
  { value: 'Boolean', label: 'Boolean' },
  { value: 'Double', label: 'Double' },
];

const formTypeOptions = [
  { value: 'input', label: '文本框' },
  { value: 'textarea', label: '文本域' },
  { value: 'select', label: '下拉框' },
  { value: 'radio', label: '单选框' },
  { value: 'date', label: '日期' },
  { value: 'datetime', label: '日期时间' },
  { value: 'imageUpload', label: '图片上传' },
  { value: 'fileUpload', label: '文件上传' },
];

const queryTypeOptions = [
  { value: '=', label: '=' },
  { value: 'LIKE', label: 'LIKE' },
  { value: '>=', label: '>=' },
  { value: '<=', label: '<=' },
];

// ==================== Component

function FieldConfigModal({ tableId, tableName, open, onCancel }: FieldConfigModalProps) {
  const [fields, setFields] = useState<CodegenFieldItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);

  const loadFields = useCallback(async () => {
    setLoading(true);
    try {
      const res = await getCodegenTableById(tableId);
      setFields(res.data.data.fields || []);
    } catch {
      SxwlMessage.error('获取字段配置失败');
    } finally {
      setLoading(false);
    }
  }, [tableId]);

  useEffect(() => {
    if (open) {
      loadFields();
    }
  }, [open, loadFields]);

  const updateField = (index: number, key: keyof CodegenFieldItem, value: any) => {
    setFields((prev) => {
      const next = [...prev];
      next[index] = { ...next[index], [key]: value };
      return next;
    });
  };

  const handleSave = async () => {
    setSaving(true);
    try {
      await saveCodegenFields(tableId, fields);
      SxwlMessage.success('字段配置保存成功');
      onCancel();
    } catch {
      SxwlMessage.error('保存字段配置失败');
    } finally {
      setSaving(false);
    }
  };

  const columns: ColumnsType<CodegenFieldItem> = [
    {
      title: '列名', dataIndex: 'columnName', key: 'columnName', width: 120,
      render: (val: string, record) => (
        <span>{val}{record.isPk === 1 ? <SxwlTag color="blue">PK</SxwlTag> : ''}</span>
      ),
    },
    { title: '列类型', dataIndex: 'columnType', key: 'columnType', width: 100 },
    { title: '列注释', dataIndex: 'columnComment', key: 'columnComment', width: 140, ellipsis: true },
    {
      title: 'Java字段名', dataIndex: 'javaFieldName', key: 'javaFieldName', width: 130,
      render: (val: string, _: any, i: number) =>
        <SxwlInput size="small" value={val} onChange={(e) => updateField(i, 'javaFieldName', e.target.value)} style={{ width: 120 }} />,
    },
    {
      title: 'Java类型', dataIndex: 'javaType', key: 'javaType', width: 120,
      render: (val: string, _: any, i: number) =>
        <SxwlSelect size="small" value={val} onChange={(v) => updateField(i, 'javaType', v)} options={javaTypeOptions} style={{ width: 110 }} />,
    },
    {
      title: '表单组件', dataIndex: 'formType', key: 'formType', width: 110,
      render: (val: string, _: any, i: number) =>
        <SxwlSelect size="small" value={val} onChange={(v) => updateField(i, 'formType', v)} options={formTypeOptions} style={{ width: 100 }} />,
    },
    {
      title: '查询方式', dataIndex: 'queryType', key: 'queryType', width: 90,
      render: (val: string, _: any, i: number) =>
        <SxwlSelect size="small" value={val} onChange={(v) => updateField(i, 'queryType', v)} options={queryTypeOptions} style={{ width: 80 }} />,
    },
    {
      title: '新增', dataIndex: 'isInsert', key: 'isInsert', width: 60,
      render: (val: number, _: any, i: number) =>
        <SxwlSwitch size="small" checked={val === 1} onChange={(v) => updateField(i, 'isInsert', v ? 1 : 0)} />,
    },
    {
      title: '编辑', dataIndex: 'isEdit', key: 'isEdit', width: 60,
      render: (val: number, _: any, i: number) =>
        <SxwlSwitch size="small" checked={val === 1} onChange={(v) => updateField(i, 'isEdit', v ? 1 : 0)} />,
    },
    {
      title: '列表', dataIndex: 'isList', key: 'isList', width: 60,
      render: (val: number, _: any, i: number) =>
        <SxwlSwitch size="small" checked={val === 1} onChange={(v) => updateField(i, 'isList', v ? 1 : 0)} />,
    },
    {
      title: '查询', dataIndex: 'isQuery', key: 'isQuery', width: 60,
      render: (val: number, _: any, i: number) =>
        <SxwlSwitch size="small" checked={val === 1} onChange={(v) => updateField(i, 'isQuery', v ? 1 : 0)} />,
    },
    {
      title: '必填', dataIndex: 'isRequired', key: 'isRequired', width: 60,
      render: (val: number, _: any, i: number) =>
        <SxwlSwitch size="small" checked={val === 1} onChange={(v) => updateField(i, 'isRequired', v ? 1 : 0)} />,
    },
    {
      title: '排序', dataIndex: 'sort', key: 'sort', width: 60,
      render: (val: number, _: any, i: number) =>
        <SxwlInputNumber size="small" value={val} onChange={(v) => updateField(i, 'sort', v ?? 0)} style={{ width: 55 }} />,
    },
  ];

  return (
    <SxwlModal
      title={`字段配置 - ${tableName}`}
      open={open}
      onOk={handleSave}
      onCancel={onCancel}
      width="95%"
      style={{ maxWidth: 1400 }}
      confirmLoading={saving}
      destroyOnHidden
    >
      <SxwlTable
        rowKey="columnName"
        columns={columns}
        dataSource={fields}
        loading={loading}
        pagination={false}
        scroll={{ x: 1500 }}
        size="small"
      />
    </SxwlModal>
  );
}

export default FieldConfigModal;

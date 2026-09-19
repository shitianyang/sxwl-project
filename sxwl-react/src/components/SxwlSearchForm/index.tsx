import { type JSX } from 'react';
import type { Dayjs } from 'dayjs';
import {
  SxwlInput, SxwlButton, SxwlSelect, SxwlIcon,
  SxwlForm, SxwlRangePicker,
} from '@/components';
import type { FormFieldConfig } from '@/types/FormFieldConfig';
import './index.scss';

// ==================== Types

export interface SxwlSearchFormProps {
  /** 搜索字段配置 */
  fields: FormFieldConfig[];
  /** 外层类名（SxwlPage 用它把筛选条挂进面板） */
  className?: string;
  /** 点击查询 */
  onSearch?: (values: Record<string, any>) => void;
  /** 点击重置 */
  onReset?: () => void;
}

// ==================== Inner Component

function SxwlSearchFormInner({ fields, className, onSearch, onReset }: SxwlSearchFormProps): JSX.Element {
  const [form] = SxwlForm.useForm();

  const handleFinish = (raw: Record<string, any>) => {
    const values = { ...raw };

    // 自动转换 dateRange 字段为自定义或默认参数名
    for (const field of fields) {
      if (field.type === 'dateRange') {
        const range = values[field.name] as [Dayjs, Dayjs] | undefined;
        if (range && range[0] && range[1]) {
          const startKey = (field as any).dateRangeStartKey || `${field.name}Start`;
          const endKey = (field as any).dateRangeEndKey || `${field.name}End`;
          values[startKey] = range[0].format('YYYY-MM-DD HH:mm:ss');
          values[endKey] = range[1].format('YYYY-MM-DD HH:mm:ss');
        }
        delete values[field.name];
      }
    }

    onSearch?.(values);
  };

  const handleReset = () => {
    form.resetFields();
    onReset?.();
  };

  // 筛选条不显示 label：字段名进 placeholder，无障碍名交给 aria-label
  return (
    <SxwlForm
      form={form}
      className={`sxwl-filters${className ? ` ${className}` : ''}`}
      onFinish={handleFinish}
    >
      {fields.map((field) => {
        const text = field.placeholder ?? field.label ?? field.name;
        return (
          <SxwlForm.Item key={field.name} name={field.name} className="sxwl-filters__field">
            {field.type === 'select' ? (
              <SxwlSelect
                aria-label={field.label ?? text}
                placeholder={text}
                allowClear
                suffixIcon={<SxwlIcon name="CaretDownOutlined" />}
                options={field.options}
              />
            ) : field.type === 'dateRange' ? (
              <SxwlRangePicker aria-label={field.label ?? text} className="sxwl-filters__range" />
            ) : (
              <SxwlInput
                aria-label={field.label ?? text}
                placeholder={text}
                maxLength={field.maxLength}
                prefix={<SxwlIcon name="SearchOutlined" />}
              />
            )}
          </SxwlForm.Item>
        );
      })}
      <div className="sxwl-filters__spacer" />
      <SxwlForm.Item className="sxwl-filters__actions">
        <SxwlButton type="primary" htmlType="submit">查询</SxwlButton>
        <SxwlButton onClick={handleReset}>重置</SxwlButton>
      </SxwlForm.Item>
    </SxwlForm>
  );
}

// ==================== Outer Guard

function SxwlSearchForm(props: SxwlSearchFormProps): JSX.Element | null {
  if (!props.fields?.length) return null;
  return <SxwlSearchFormInner {...props} />;
}

export default SxwlSearchForm;

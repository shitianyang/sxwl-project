import { type JSX } from 'react';
import {
  SxwlInput, SxwlButton, SxwlSelect, SxwlIcon,
  SxwlCard, SxwlSpace, SxwlForm,
} from '@/components';
import type { FormFieldConfig } from '@/components/FormFieldConfig';
import './index.scss';

// ==================== Types

export interface SxwlSearchFormProps {
  /** 搜索字段配置 */
  fields: FormFieldConfig[];
  /** 点击查询 */
  onSearch?: (values: Record<string, any>) => void;
  /** 点击重置 */
  onReset?: () => void;
}

// ==================== Component

function SxwlSearchForm({ fields, onSearch, onReset }: SxwlSearchFormProps): JSX.Element {
  const [form] = SxwlForm.useForm();

  const handleSearch = () => {
    const values = form.getFieldsValue();
    onSearch?.(values);
  };

  const handleReset = () => {
    form.resetFields();
    onReset?.();
  };

  if (!fields?.length) return <></>;

  return (
    <SxwlCard className="sxwl-search-form">
      <SxwlForm form={form} layout="inline" className="sxwl-search-form__inner">
        {fields.map((field) => (
          <SxwlForm.Item key={field.name} name={field.name} label={field.label}>
            {field.type === 'select' ? (
              <SxwlSelect
                placeholder={field.placeholder ?? '请选择'}
                allowClear
                style={{ width: 160 }}
                options={field.options}
              />
            ) : (
              <SxwlInput
                placeholder={field.placeholder ?? '请输入'}
                allowClear
                maxLength={field.maxLength}
              />
            )}
          </SxwlForm.Item>
        ))}
        <SxwlForm.Item>
          <SxwlSpace>
            <SxwlButton type="primary" icon={<SxwlIcon name="SearchOutlined" />} onClick={handleSearch}>
              查询
            </SxwlButton>
            <SxwlButton icon={<SxwlIcon name="ReloadOutlined" />} onClick={handleReset}>
              重置
            </SxwlButton>
          </SxwlSpace>
        </SxwlForm.Item>
      </SxwlForm>
    </SxwlCard>
  );
}

export default SxwlSearchForm;

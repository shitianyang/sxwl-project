import { useState, useEffect, useRef } from 'react';
import type { SelectProps } from 'antd';
import type { DefaultOptionType } from 'antd/es/select';
import SxwlSelect from '@/components/SxwlSelect';
import { getDetailListByDictId, getDictPageByParams, type DictDetailItem } from '@/api/system/dictApi';

export interface SxwlDictSelectProps extends SelectProps {
  /** 字典类型编码，如 'sys_user_sex' */
  dictType: string;
  /** 是否在组件挂载时自动加载，默认 true */
  autoLoad?: boolean;
}

/** 字典编码 → 字典 ID 的缓存（避免重复查询） */
const dictCodeToIdCache = new Map<string, number>();

/**
 * SxwlDictSelect — 字典下拉选择器
 *
 * 根据字典类型编码自动加载字典明细，渲染为 SxwlSelect 下拉框。
 * 内部缓存字典编码 → 明细列表，相同 dictType 只请求一次。
 *
 * ```tsx
 * <SxwlDictSelect dictType="sys_user_sex" placeholder="请选择性别" />
 * ```
 */
const SxwlDictSelect: React.FC<SxwlDictSelectProps> = ({
  dictType,
  autoLoad = true,
  placeholder = '请选择',
  ...rest
}) => {
  const [options, setOptions] = useState<DefaultOptionType[]>([]);
  const [loading, setLoading] = useState(false);
  const mountedRef = useRef(true);

  useEffect(() => {
    mountedRef.current = true;
    if (autoLoad) {
      loadOptions();
    }
    return () => {
      mountedRef.current = false;
    };
  }, [dictType]);

  const loadOptions = async () => {
    setLoading(true);
    try {
      // 1. 通过字典 code 查找字典 ID
      let dictId = dictCodeToIdCache.get(dictType);
      if (!dictId) {
        const pageRes = await getDictPageByParams({ dictCode: dictType, current: 1, pageSize: 1 });
        const dictItem = pageRes.data.data?.list?.[0];
        if (!dictItem) {
          setOptions([]);
          return;
        }
        dictId = dictItem.id;
        dictCodeToIdCache.set(dictType, dictId);
      }

      // 2. 加载字典明细
      const detailRes = await getDetailListByDictId(dictId);
      const details: DictDetailItem[] = detailRes.data.data ?? [];
      if (!mountedRef.current) return;

      setOptions(
        details
          .filter((d) => d.status === 1)
          .sort((a, b) => a.sort - b.sort)
          .map((d) => ({
            value: d.detailValue,
            label: d.detailLabel,
          }))
      );
    } catch {
      // 静默失败，options 保持为空
    } finally {
      if (mountedRef.current) {
        setLoading(false);
      }
    }
  };

  return (
    <SxwlSelect
      placeholder={placeholder}
      loading={loading}
      options={options}
      {...rest}
    />
  );
};

export default SxwlDictSelect;

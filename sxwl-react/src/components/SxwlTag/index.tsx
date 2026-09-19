import { type JSX } from 'react';
import { Tag } from 'antd';
import type { TagProps } from 'antd';
import './index.scss';

export type SxwlTagTone = 'success' | 'warning' | 'danger' | 'info' | 'neutral';

export interface SxwlTagProps extends TagProps {
  /**
   * 语义色：浅底 + 同族 -text 文字色，不带描边。
   * 不传 tone 时退回 antd 原生 color 写法（旧页面兼容）。
   */
  tone?: SxwlTagTone;
}

/**
 * SxwlTag — 状态/分类标签
 *
 * ```tsx
 * <SxwlTag tone="success">启用</SxwlTag>
 * <SxwlTag tone="neutral">禁用</SxwlTag>
 * ```
 */
const SxwlTag = ({ tone, className, ...rest }: SxwlTagProps): JSX.Element => (
  <Tag
    className={tone ? `sxwl-tag sxwl-tag--${tone}${className ? ` ${className}` : ''}` : className}
    variant={tone ? 'filled' : rest.variant}
    {...rest}
  />
);

export default SxwlTag;

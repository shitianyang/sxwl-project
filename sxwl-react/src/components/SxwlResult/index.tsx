import { Result } from 'antd';
import type { ResultProps } from 'antd';

export type SxwlResultProps = ResultProps;

/**
 * SxwlResult — 基于 antd Result 的二次封装
 *
 * 用法：
 * ```tsx
 * <SxwlResult status="404" title="404" subTitle="页面不存在" />
 * ```
 */
const SxwlResult = (props: SxwlResultProps) => <Result {...props} />;

export default SxwlResult;

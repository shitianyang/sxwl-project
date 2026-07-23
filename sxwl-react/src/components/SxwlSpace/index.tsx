import { Space } from 'antd';
import type { SpaceProps } from 'antd';

export type SxwlSpaceProps = SpaceProps;

/**
 * SxwlSpace — 基于 antd Space 的二次封装
 *
 * 用法：
 * ```tsx
 * <SxwlSpace>
 *   <SxwlButton>按钮1</SxwlButton>
 *   <SxwlButton>按钮2</SxwlButton>
 * </SxwlSpace>
 * ```
 */
const SxwlSpace = (props: SxwlSpaceProps) => <Space {...props} />;

export default SxwlSpace;

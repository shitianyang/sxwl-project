import { Typography } from 'antd';
import type { TitleProps } from 'antd/es/typography/Title';

const { Title } = Typography;

export type SxwlTitleProps = TitleProps;

/**
 * SxwlTitle — 基于 antd Typography.Title 的二次封装
 *
 * 用法：
 * ```tsx
 * <SxwlTitle level={4}>标题</SxwlTitle>
 * ```
 */
const SxwlTitle = (props: SxwlTitleProps) => <Title {...props} />;

export default SxwlTitle;

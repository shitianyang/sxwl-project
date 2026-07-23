import { Typography } from 'antd';
import type { TextProps } from 'antd/es/typography/Text';

const { Text } = Typography;

export type SxwlTextProps = TextProps;

/**
 * SxwlText — 基于 antd Typography.Text 的二次封装
 *
 * 用法：
 * ```tsx
 * <SxwlText>文本内容</SxwlText>
 * <SxwlText type="secondary">次要文本</SxwlText>
 * ```
 */
const SxwlText = (props: SxwlTextProps) => <Text {...props} />;

export default SxwlText;

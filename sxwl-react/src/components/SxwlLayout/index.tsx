import { Layout } from 'antd';
import type { LayoutProps } from 'antd';

export type SxwlLayoutProps = LayoutProps;

/**
 * SxwlLayout — 基于 antd Layout 的二次封装
 *
 * 子组件：SxwlLayout.Header / SxwlLayout.Sider / SxwlLayout.Content
 *
 * 用法：
 * ```tsx
 * <SxwlLayout>
 *   <SxwlLayout.Sider>侧边栏</SxwlLayout.Sider>
 *   <SxwlLayout>
 *     <SxwlLayout.Header>头部</SxwlLayout.Header>
 *     <SxwlLayout.Content>内容</SxwlLayout.Content>
 *   </SxwlLayout>
 * </SxwlLayout>
 * ```
 */
const SxwlLayoutComponent = (props: SxwlLayoutProps) => <Layout {...props} />;

const SxwlLayout = Object.assign(SxwlLayoutComponent, {
  Header: Layout.Header,
  Sider: Layout.Sider,
  Content: Layout.Content,
});

export default SxwlLayout;

import type { CSSProperties, SVGAttributes } from 'react';
import { createElement } from 'react';

export interface SxwlIconProps extends Omit<SVGAttributes<SVGSVGElement>, 'name'> {
  /** 图标名称（PascalCase，如 'UserOutlined'、'EditOutlined'） */
  name: string;
  /** 自定义样式 */
  style?: CSSProperties;
  /** 自定义类名 */
  className?: string;
  /** SVG 尺寸 */
  size?: number;
}

/**
 * 只注册项目实际使用的 SVG 图标。
 *
 * 不再 glob 全量 assets/icons，避免 Vite 为几百个 SVG 生成独立 chunk。
 * 新增图标时，把文件路径追加到这里即可。
 */
const iconModules = import.meta.glob(
  [
    '@/assets/icons/ant-design--apartment-outlined.svg',
    '@/assets/icons/ant-design--bell-outlined.svg',
    '@/assets/icons/ant-design--cloud-server-outlined.svg',
    '@/assets/icons/ant-design--cloud-upload-outlined.svg',
    '@/assets/icons/ant-design--dashboard-outlined.svg',
    '@/assets/icons/ant-design--database-outlined.svg',
    '@/assets/icons/ant-design--delete-outlined.svg',
    '@/assets/icons/ant-design--desktop-outlined.svg',
    '@/assets/icons/ant-design--download-outlined.svg',
    '@/assets/icons/ant-design--edit-outlined.svg',
    '@/assets/icons/ant-design--eye-outlined.svg',
    '@/assets/icons/ant-design--file-outlined.svg',
    '@/assets/icons/ant-design--file-search-outlined.svg',
    '@/assets/icons/ant-design--file-text-outlined.svg',
    '@/assets/icons/ant-design--idcard-outlined.svg',
    '@/assets/icons/ant-design--login-outlined.svg',
    '@/assets/icons/ant-design--logout-outlined.svg',
    '@/assets/icons/ant-design--menu-fold-outlined.svg',
    '@/assets/icons/ant-design--menu-unfold-outlined.svg',
    '@/assets/icons/ant-design--monitor-outlined.svg',
    '@/assets/icons/ant-design--ordered-list-outlined.svg',
    '@/assets/icons/ant-design--plus-outlined.svg',
    '@/assets/icons/ant-design--read-outlined.svg',
    '@/assets/icons/ant-design--reload-outlined.svg',
    '@/assets/icons/ant-design--safety-certificate-outlined.svg',
    '@/assets/icons/ant-design--safety-outlined.svg',
    '@/assets/icons/ant-design--schedule-outlined.svg',
    '@/assets/icons/ant-design--search-outlined.svg',
    '@/assets/icons/ant-design--setting-outlined.svg',
    '@/assets/icons/ant-design--snippets-outlined.svg',
    '@/assets/icons/ant-design--team-outlined.svg',
    '@/assets/icons/ant-design--tool-outlined.svg',
    '@/assets/icons/ant-design--user-outlined.svg',
  ],
  {
    eager: true,
    query: '?react',
    import: 'default',
  },
) as Record<string, React.ComponentType<SVGAttributes<SVGSVGElement>>>;

const iconMap: Record<string, React.ComponentType<SVGAttributes<SVGSVGElement>>> = {};

for (const [filePath, IconComponent] of Object.entries(iconModules)) {
  const fileName = filePath.split('/').pop()!;
  const match = fileName.match(/^ant-design--(.+)\.svg$/);
  if (!match) continue;

  const iconName = match[1]
    .split('-')
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join('');
  iconMap[iconName] = IconComponent;
}

const SxwlIcon = ({ name, className, style, size, ...rest }: SxwlIconProps) => {
  const IconComponent = iconMap[name];

  if (!IconComponent) {
    console.warn(`[SxwlIcon] 未注册图标 "${name}"`);
    return null;
  }

  return createElement(IconComponent, {
    ...rest,
    className,
    style: { width: size || '1em', height: size || '1em', ...style },
    width: size,
    height: size,
  });
};

export default SxwlIcon;

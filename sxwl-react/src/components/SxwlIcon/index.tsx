import type { CSSProperties, SVGAttributes } from 'react';
import { createElement, useEffect, useState } from 'react';

export interface SxwlIconProps {
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
 * 图标懒加载器映射 — name → () => Promise<Component>
 *
 * import.meta.glob 的 eager: false 让 Vite 为每个 SVG 生成独立 chunk，
 * 图标只在首次使用时才加载。
 */
const iconLoaders = import.meta.glob('@/assets/icons/*.svg', {
  eager: false,
  query: '?react',
  import: 'default',
}) as Record<string, () => Promise<React.ComponentType<SVGAttributes<SVGSVGElement>>>>;

const iconLoaderMap: Record<
  string,
  () => Promise<React.ComponentType<SVGAttributes<SVGSVGElement>>>
> = {};
for (const filePath of Object.keys(iconLoaders)) {
  const fileName = filePath.split('/').pop()!;
  const match = fileName.match(/^ant-design--(.+)\.svg$/);
  if (match) {
    // kebab-case → PascalCase：'user-outlined' → 'UserOutlined'
    const iconName = match[1]
      .split('-')
      .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
      .join('');
    iconLoaderMap[iconName] = iconLoaders[filePath];
  }
}

/** 已加载组件的运行时缓存 */
const iconCache = new Map<string, React.ComponentType<SVGAttributes<SVGSVGElement>>>();

/**
 * SxwlIcon — 统一图标组件（按需加载）
 *
 * 用法：
 * ```tsx
 * <SxwlIcon name="UserOutlined" />
 * <SxwlIcon name="DashboardOutlined" size={20} style={{ color: '#DE5F0E' }} />
 * ```
 *
 * 图标文件位于 src/assets/icons/，命名规则 ant-design--{kebab-name}.svg。
 * 首次使用指定名称时才会加载对应的 SVG chunk，加载完成后缓存。
 */
const SxwlIcon = ({ name, className, style, size }: SxwlIconProps) => {
  const [Component, setComponent] =
    useState<React.ComponentType<SVGAttributes<SVGSVGElement>> | null>(() => iconCache.get(name) ?? null);

  useEffect(() => {
    // 立即清空旧图标，避免 name 变化时短暂闪烁旧图标
    setComponent(null);

    // 缓存命中
    if (iconCache.has(name)) {
      setComponent(() => iconCache.get(name)!);
      return;
    }

    const loader = iconLoaderMap[name];
    if (!loader) {
      console.warn(`[SxwlIcon] 未找到图标 "${name}"`);
      return;
    }

    let cancelled = false;
    loader()
      .then((Comp) => {
        if (!cancelled) {
          iconCache.set(name, Comp);
          setComponent(() => Comp);
        }
      })
      .catch((err) => {
        console.error(`[SxwlIcon] 加载图标 "${name}" 失败:`, err);
      });

    return () => {
      cancelled = true;
    };
  }, [name]);

  if (!Component) return null;

  return createElement(Component, {
    className,
    style: { width: size || '1em', height: size || '1em', ...style },
    width: size,
    height: size,
  });
};

export default SxwlIcon;

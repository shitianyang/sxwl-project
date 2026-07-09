// ============================================
// 格式化工具
// ============================================

/**
 * 日期格式化
 * @param date 日期对象、时间戳或日期字符串
 * @param template 格式模板，默认 'YYYY-MM-DD HH:mm:ss'
 *
 * 模板占位符：
 * - YYYY / MM / DD / HH / mm / ss
 *
 * @example formatDate(new Date(), 'YYYY-MM-DD') // "2026-07-05"
 */
export function formatDate(
  date: Date | number | string,
  template = 'YYYY-MM-DD HH:mm:ss',
): string {
  const d = date instanceof Date ? date : new Date(date);
  if (isNaN(d.getTime())) return '';

  const pad = (n: number) => String(n).padStart(2, '0');

  const map: Record<string, string> = {
    YYYY: String(d.getFullYear()),
    MM: pad(d.getMonth() + 1),
    DD: pad(d.getDate()),
    HH: pad(d.getHours()),
    mm: pad(d.getMinutes()),
    ss: pad(d.getSeconds()),
  };

  return template.replace(/YYYY|MM|DD|HH|mm|ss/g, (k) => map[k] ?? k);
}

/**
 * 相对时间（如 "3 分钟前"、"2 小时前"）
 * @param date 日期对象、时间戳或日期字符串
 * @param now 参照时间，默认当前时间
 */
export function formatRelativeTime(date: Date | number | string, now = Date.now()): string {
  const d = date instanceof Date ? date : new Date(date);
  if (isNaN(d.getTime())) return '';

  const diff = now - d.getTime();

  const units: [number, string][] = [
    [365 * 24 * 60 * 60 * 1000, '年'],
    [30 * 24 * 60 * 60 * 1000, '个月'],
    [24 * 60 * 60 * 1000, '天'],
    [60 * 60 * 1000, '小时'],
    [60 * 1000, '分钟'],
  ];

  for (const [ms, label] of units) {
    const value = Math.floor(diff / ms);
    if (value >= 1) return `${value} ${label}前`;
  }

  return '刚刚';
}

/**
 * 数字千分位格式化
 * @example formatNumber(1234567.89) // "1,234,567.89"
 */
export function formatNumber(n: number): string {
  const [int, dec] = n.toString().split('.');
  const formatted = int.replace(/\B(?=(\d{3})+(?!\d))/g, ',');
  return dec ? `${formatted}.${dec}` : formatted;
}

/**
 * 文件体积格式化
 * @example formatFileSize(1234567) // "1.18 MB"
 */
export function formatFileSize(bytes: number): string {
  if (bytes === 0) return '0 B';

  const units = ['B', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(1024));
  const size = bytes / Math.pow(1024, i);

  return `${size.toFixed(i === 0 ? 0 : 2)} ${units[i]}`;
}

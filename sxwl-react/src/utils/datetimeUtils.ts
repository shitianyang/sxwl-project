/**
 * 时间日期工具类
 *
 * 基于原生 Intl 与 Date API，不依赖 dayjs 等第三方库。
 *
 * @example
 *   datetime.formatNow()          // "2026-07-05 14:30:25 星期日"
 *   datetime.formatDate(new Date()) // "2026-07-05"
 *   datetime.formatTime(new Date()) // "14:30:25"
 *   datetime.getWeekdayStr()       // "星期日"
 */

const WEEKDAYS = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];

/** 中文星期格式化器 */
const WEEKDAY_FMT = new Intl.DateTimeFormat('zh-CN', { weekday: 'long' });

/** 日期部分格式化器 */
const DATE_FMT: Intl.DateTimeFormatOptions = {
  year: 'numeric',
  month: '2-digit',
  day: '2-digit',
};

/** 时间部分格式化器 */
const TIME_FMT: Intl.DateTimeFormatOptions = {
  hour: '2-digit',
  minute: '2-digit',
  second: '2-digit',
  hour12: false,
};

export const datetime = {
  /**
   * 格式化当前时间为完整格式
   * 例：2026-07-05 14:30:25 星期日
   */
  formatNow(): string {
    const now = new Date();
    const date = this.formatDate(now);
    const time = this.formatTime(now);
    const weekday = this.getWeekdayStr(now);
    return `${date} ${time} ${weekday}`;
  },

  /**
   * 格式化日期部分
   * 例：2026-07-05
   */
  formatDate(date: Date = new Date()): string {
    const parts = new Intl.DateTimeFormat('zh-CN', DATE_FMT).formatToParts(date);
    const map = Object.fromEntries(parts.filter((p) => p.type !== 'literal').map((p) => [p.type, p.value]));
    return `${map.year}-${map.month}-${map.day}`;
  },

  /**
   * 格式化时间部分
   * 例：14:30:25
   */
  formatTime(date: Date = new Date()): string {
    return new Intl.DateTimeFormat('zh-CN', TIME_FMT).format(date);
  },

  /**
   * 获取中文星期
   * 例：星期日
   */
  getWeekdayStr(date: Date = new Date()): string {
    return WEEKDAY_FMT.format(date);
  },

  /**
   * 通过数字索引获取中文星期（0 = 星期日）
   */
  getWeekdayByIndex(index: number): string {
    return WEEKDAYS[index] ?? '';
  },
};

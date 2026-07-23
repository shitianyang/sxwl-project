import { useEffect, useState } from 'react';
import { datetime } from '@/utils/datetimeUtils';

/**
 * 实时时钟组件
 *
 * 每秒更新，显示当前日期时间 + 星期。
 * 用于 Header 右侧区域。
 */
export default function SxwlClock() {
  const [timeStr, setTimeStr] = useState(datetime.formatNow());

  useEffect(() => {
    const timer = setInterval(() => {
      setTimeStr(datetime.formatNow());
    }, 1000);
    return () => clearInterval(timer);
  }, []);

  return <span className="sxwl-header-clock">{timeStr}</span>;
}

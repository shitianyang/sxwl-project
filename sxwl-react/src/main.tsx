import { createRoot } from 'react-dom/client'
import dayjs from 'dayjs'
import 'dayjs/locale/zh-cn'
import App from './App'
import '@/styles/global.css'

// 日期组件（DatePicker 等）日历面板由 dayjs 渲染，需与 antd ConfigProvider zhCN 配套
dayjs.locale('zh-cn')

createRoot(document.getElementById('root')!).render(<App />)

import { SxwlButton, SxwlResult } from '@/components';
import { useNavigate } from 'react-router-dom';

export default function NotFound() {
  const navigate = useNavigate();

  return (
    <SxwlResult
      status="404"
      title="404"
      subTitle="抱歉，您访问的页面不存在。"
      extra={
        <SxwlButton type="primary" onClick={() => navigate('/')}>
          返回首页
        </SxwlButton>
      }
    />
  );
}

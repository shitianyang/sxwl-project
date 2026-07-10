import { SxwlButton, SxwlResult } from '@/components';
import { useNavigate } from 'react-router-dom';

export default function Forbidden() {
  const navigate = useNavigate();

  return (
    <SxwlResult
      status="403"
      title="403"
      subTitle="抱歉，您没有权限访问该页面。"
      extra={
        <SxwlButton type="primary" onClick={() => navigate('/')}>
          返回首页
        </SxwlButton>
      }
    />
  );
}

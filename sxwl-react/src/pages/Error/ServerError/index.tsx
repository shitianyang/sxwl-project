import { SxwlButton, SxwlResult } from '@/components';
import { useNavigate } from 'react-router-dom';

export default function ServerError() {
  const navigate = useNavigate();

  return (
    <SxwlResult
      status="500"
      title="500"
      subTitle="抱歉，服务器出错了，请稍后再试。"
      extra={
        <SxwlButton type="primary" onClick={() => navigate('/')}>
          返回首页
        </SxwlButton>
      }
    />
  );
}

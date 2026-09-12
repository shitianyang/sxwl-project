/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.listener;

import com.sxwl.common.event.SxwlOperationLogEvent;
import com.sxwl.system.mapper.SysLogMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SxwlLogEventListener 测试")
class SxwlLogEventListenerTest {
    private SxwlLogEventListener listener;
    @Mock private SysLogMapper sysLogMapper;

    @BeforeEach
    void setUp() { listener = new SxwlLogEventListener(sysLogMapper); }

    @Test
    void testHandleOperationLog() {
        SxwlOperationLogEvent event = new SxwlOperationLogEvent()
                .title("用户管理").logType(2).description("删除用户[id=1]")
                .method("SysUserController.delete()").requestUrl("/sys/user/1").requestMethod("DELETE")
                .requestParam("{\"id\":1}").responseResult("{\"code\":200}")
                .operateIp("127.0.0.1").userId(100L).userName("admin")
                .executeTime(50L).errorMsg(null).status(1).traceId("trace-001")
                .userAgent("Chrome").browser("Chrome").os("Windows")
                .operateLocation("内网IP").diff("diff-json");

        listener.handleOperationLog(event);
        verify(sysLogMapper).insertLog(any());
    }

    @Test
    void testHandleOperationLog_whenMapperThrows_shouldCatchException() {
        SxwlOperationLogEvent event = new SxwlOperationLogEvent().title("测试").logType(2);
        doThrow(new RuntimeException("db error")).when(sysLogMapper).insertLog(any());
        listener.handleOperationLog(event);
        verify(sysLogMapper).insertLog(any());
    }
}

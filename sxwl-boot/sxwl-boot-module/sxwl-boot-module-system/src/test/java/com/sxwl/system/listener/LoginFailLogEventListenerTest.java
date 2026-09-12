/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.listener;

import com.sxwl.security.event.SxwlLoginFailureEvent;
import com.sxwl.system.mapper.SysLogMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginFailLogEventListener 测试")
class LoginFailLogEventListenerTest {
    private LoginFailLogEventListener listener;
    @Mock private SysLogMapper sysLogMapper;

    @BeforeEach
    void setUp() { listener = new LoginFailLogEventListener(sysLogMapper); }

    @Test
    void testHandleLoginFailure() {
        SxwlLoginFailureEvent event = new SxwlLoginFailureEvent();
        event.setTargetAccount("admin"); event.setIp("127.0.0.1"); event.setFailReason("密码错误");
        event.setUserAgent("Chrome"); event.setBrowser("Chrome"); event.setOs("Windows");
        event.setOperateLocation("内网IP");

        listener.handleLoginFailure(event);
        verify(sysLogMapper).insertLog(any());
    }

    @Test
    void testHandleLoginFailure_whenMapperThrows_shouldCatchException() {
        SxwlLoginFailureEvent event = new SxwlLoginFailureEvent();
        event.setTargetAccount("admin");
        doThrow(new RuntimeException("db error")).when(sysLogMapper).insertLog(any());
        listener.handleLoginFailure(event);
        verify(sysLogMapper).insertLog(any());
    }
}

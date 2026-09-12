/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.listener;

import com.sxwl.security.event.SxwlLoginSuccessEvent;
import com.sxwl.system.mapper.SysLogMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginLogEventListener 测试")
class LoginLogEventListenerTest {
    private LoginLogEventListener listener;
    @Mock private SysLogMapper sysLogMapper;

    @BeforeEach
    void setUp() { listener = new LoginLogEventListener(sysLogMapper); }

    @Test
    void testHandleLoginSuccess() {
        SxwlLoginSuccessEvent event = new SxwlLoginSuccessEvent();
        event.setUserId(1L); event.setUsername("admin"); event.setIp("127.0.0.1");
        event.setUserAgent("Chrome"); event.setBrowser("Chrome"); event.setOs("Windows");
        event.setOperateLocation("内网IP"); event.setRequestUrl("/sxwl-api/auth/login"); event.setRequestMethod("POST");

        listener.handleLoginSuccess(event);
        verify(sysLogMapper).insertLog(any());
    }

    @Test
    void testHandleLoginSuccess_whenMapperThrows_shouldCatchException() {
        SxwlLoginSuccessEvent event = new SxwlLoginSuccessEvent();
        event.setUserId(1L); event.setUsername("admin");
        doThrow(new RuntimeException("db error")).when(sysLogMapper).insertLog(any());
        listener.handleLoginSuccess(event);
        verify(sysLogMapper).insertLog(any());
    }
}

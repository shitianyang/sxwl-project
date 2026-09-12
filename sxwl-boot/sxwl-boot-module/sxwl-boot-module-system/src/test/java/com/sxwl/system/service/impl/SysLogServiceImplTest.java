/*
 * Copyright (c) 2026 Sxwl Technologies, Inc. All rights reserved.
 */

package com.sxwl.system.service.impl;

import com.github.pagehelper.PageInfo;
import com.sxwl.system.mapper.SysLogMapper;
import com.sxwl.system.model.dto.SysLogDTO;
import com.sxwl.system.model.params.SysLogPageParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SysLogServiceImpl 测试")
class SysLogServiceImplTest {
    private SysLogServiceImpl service;
    @Mock private SysLogMapper sysLogMapper;

    @BeforeEach
    void setUp() { service = new SysLogServiceImpl(sysLogMapper); }

    @Test
    void testGetLogPageByParams() {
        SysLogPageParams params = new SysLogPageParams();
        List<SysLogDTO> rows = List.of(new SysLogDTO());
        when(sysLogMapper.getLogPageByParams(params)).thenReturn(rows);
        PageInfo<SysLogDTO> result = service.getLogPageByParams(params);
        assertEquals(1, result.getList().size());
        verify(sysLogMapper).getLogPageByParams(params);
    }
}

package com.sxwl.system.service.impl;

import com.github.pagehelper.PageInfo;
import com.sxwl.system.mapper.SysLogMapper;
import com.sxwl.system.model.dto.SysLogDTO;
import com.sxwl.system.model.params.SysLogPageParams;
import com.sxwl.system.service.SysLogService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统日志 Service 实现
 *
 * @author shitianyang
 * @since 0.1.0
 */
@Service
public class SysLogServiceImpl implements SysLogService {

    /** SysLog Mapper */
    private final SysLogMapper sysLogMapper;

    public SysLogServiceImpl(SysLogMapper sysLogMapper) {
        this.sysLogMapper = sysLogMapper;
    }

    /**
     * 分页查询日志列表
     *
     * @param params 分页 + 筛选参数（logType、title、userName、status、startTime、endTime）
     * @return 分页结果
     */
    @Override
    public PageInfo<SysLogDTO> getLogPageByParams(SysLogPageParams params) {
        List<SysLogDTO> rows = sysLogMapper.getLogPageByParams(params);
        return new PageInfo<>(rows);
    }
}

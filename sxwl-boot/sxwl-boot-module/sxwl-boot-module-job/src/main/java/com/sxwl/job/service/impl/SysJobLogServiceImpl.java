package com.sxwl.job.service.impl;

import com.github.pagehelper.PageInfo;
import com.sxwl.common.exception.SxwlBusinessException;
import com.sxwl.job.mapper.SysJobLogMapper;
import com.sxwl.job.model.dto.SysJobLogDTO;
import com.sxwl.job.model.params.SysJobLogPageParams;
import com.sxwl.job.service.SysJobLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 定时任务日志 Service 实现
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@Service
public class SysJobLogServiceImpl implements SysJobLogService {

    private static final Logger log = LoggerFactory.getLogger(SysJobLogServiceImpl.class);

    private final SysJobLogMapper sysJobLogMapper;

    public SysJobLogServiceImpl(SysJobLogMapper sysJobLogMapper) {
        this.sysJobLogMapper = sysJobLogMapper;
    }

    @Override
    public SysJobLogDTO getLogById(Long id) {
        SysJobLogDTO dto = sysJobLogMapper.getLogById(id);
        if (dto == null) {
            throw new SxwlBusinessException(10004, "任务日志不存在");
        }
        return dto;
    }

    @Override
    public PageInfo<SysJobLogDTO> getLogPageByParams(SysJobLogPageParams params) {
        List<SysJobLogDTO> rows = sysJobLogMapper.getLogPageByParams(params);
        return new PageInfo<>(rows);
    }

    @Override
    public int deleteLogById(Long id) {
        int affected = sysJobLogMapper.deleteLogById(id);
        if (affected == 0) {
            throw new SxwlBusinessException(10004, "任务日志不存在");
        }
        log.info("删除任务日志成功: id={}", id);
        return affected;
    }

    @Override
    public int cleanLogBefore(int days) {
        int affected = sysJobLogMapper.cleanLogBefore(days);
        log.info("清理任务日志成功: days={}, affected={}", days, affected);
        return affected;
    }
}

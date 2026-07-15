package com.sxwl.system.service;

import com.github.pagehelper.PageInfo;
import com.sxwl.system.model.dto.SysLogDTO;
import com.sxwl.system.model.params.SysLogPageParams;

/**
 * 系统日志 Service 接口
 *
 * @author shitianyang
 * @since 0.1.0
 */
public interface SysLogService {

    /**
     * 分页查询日志列表
     *
     * @param params 分页查询参数（logType、title、userName、status、时间范围）
     * @return 分页日志列表
     */
    PageInfo<SysLogDTO> getLogPageByParams(SysLogPageParams params);
}

package com.sxwl.system.mapper;

import com.sxwl.common.annotation.SxwlDataScope;
import com.sxwl.system.model.dto.SysLogDTO;
import com.sxwl.system.model.entity.SysLog;
import com.sxwl.system.model.params.SysLogPageParams;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 系统日志 Mapper
 *
 * <p>所有 SQL 定义在 {@code mapper/SysLogMapper.xml} 中。</p>
 *
 * @author shitianyang
 * @since 0.1.0
 */
@Mapper
public interface SysLogMapper {

    /**
     * 分页查询日志列表
     *
     * @param params 分页查询参数（logType、title、userName、status、startTime、endTime）
     * @return 日志 DTO 列表
     */
    @SxwlDataScope
    List<SysLogDTO> getLogPageByParams(SysLogPageParams params);

    /**
     * 新增日志
     *
     * @param entity 日志实体
     * @return 影响行数
     */
    int insertLog(SysLog entity);
}

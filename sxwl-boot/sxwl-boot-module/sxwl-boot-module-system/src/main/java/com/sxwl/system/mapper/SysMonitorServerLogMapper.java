package com.sxwl.system.mapper;

import com.sxwl.system.model.entity.SysMonitorServerLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统监控-服务器指标日志 Mapper
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@Mapper
public interface SysMonitorServerLogMapper {

    /** 写入一条记录 */
    int insert(SysMonitorServerLog entity);

    /** 查询最近 N 条记录（按 create_time DESC） */
    List<SysMonitorServerLog> selectRecent(@Param("limit") int limit);
}

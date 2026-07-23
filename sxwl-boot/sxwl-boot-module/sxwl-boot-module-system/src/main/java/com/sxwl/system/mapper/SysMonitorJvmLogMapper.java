package com.sxwl.system.mapper;

import com.sxwl.system.model.entity.SysMonitorJvmLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统监控-JVM 指标日志 Mapper
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.1.0
 */
@Mapper
public interface SysMonitorJvmLogMapper {

    /** 写入一条记录 */
    int insert(SysMonitorJvmLog entity);

    /** 查询最近 N 条记录（按 create_time DESC） */
    List<SysMonitorJvmLog> selectRecent(@Param("limit") int limit);
}

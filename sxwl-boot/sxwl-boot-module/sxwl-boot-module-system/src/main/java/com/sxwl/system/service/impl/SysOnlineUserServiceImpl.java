package com.sxwl.system.service.impl;

import com.github.pagehelper.PageInfo;
import com.sxwl.common.constants.SxwlRedisKeyConstants;
import com.sxwl.common.utils.SxwlRedisKeyUtils;
import com.sxwl.redis.helper.SxwlRedisHelper;
import com.sxwl.system.model.dto.SysOnlineUserDTO;
import com.sxwl.system.service.SysOnlineUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 在线用户 Service 实现
 *
 * <p>数据完全来自 Redis，无 DB 操作。
 * <br>通过 SMEMBERS online:users 获取在线用户列表，HGETALL 获取详情。</p>
 *
 * @author shitianyang
 * @date 2026/7/5
 * @since 0.0.0
 */
@Service
public class SysOnlineUserServiceImpl implements SysOnlineUserService {

    private static final Logger log = LoggerFactory.getLogger(SysOnlineUserServiceImpl.class);

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final SxwlRedisHelper redisHelper;

    public SysOnlineUserServiceImpl(SxwlRedisHelper redisHelper) {
        this.redisHelper = redisHelper;
    }

    @Override
    public PageInfo<SysOnlineUserDTO> list(int pageNum, int pageSize) {
        // 1. 获取所有在线用户 ID
        Set<String> userIds = redisHelper.smembers(SxwlRedisKeyConstants.ONLINE_USERS_KEY);
        if (userIds == null || userIds.isEmpty()) {
            return new PageInfo<>(Collections.emptyList());
        }

        List<String> userIdList = new ArrayList<>(userIds);

        // 2. 内存分页
        int start = (pageNum - 1) * pageSize;
        if (start >= userIdList.size()) {
            return new PageInfo<>(Collections.emptyList());
        }
        int end = Math.min(start + pageSize, userIdList.size());
        List<String> pageUserIds = userIdList.subList(start, end);

        // 3. 遍历当前页用户，获取设备级在线详情
        List<SysOnlineUserDTO> list = new ArrayList<>();
        for (String uidStr : pageUserIds) {
            Long userId = Long.parseLong(uidStr);
            Set<String> deviceIds = redisHelper.smembers(
                    SxwlRedisKeyUtils.onlineDevicesSetKey(userId));
            if (deviceIds == null || deviceIds.isEmpty()) {
                continue;
            }
            for (String deviceId : deviceIds) {
                SysOnlineUserDTO dto = buildDto(userId, deviceId);
                if (dto != null) {
                    list.add(dto);
                }
            }
        }

        // 4. 构造分页结果
        PageInfo<SysOnlineUserDTO> pageInfo = new PageInfo<>(list);
        pageInfo.setTotal(userIds.size());
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        pageInfo.setPages(calcPages(userIds.size(), pageSize));
        return pageInfo;
    }

    @Override
    public long count() {
        Set<String> userIds = redisHelper.smembers(SxwlRedisKeyConstants.ONLINE_USERS_KEY);
        return userIds != null ? userIds.size() : 0L;
    }

    @Override
    public void forceLogout(Long userId) {
        // 1. 删除 Token 白名单依赖：用户信息缓存（Filter 读不到 → 视为未认证）
        redisHelper.delete(SxwlRedisKeyUtils.tokenInfoKey(userId));

        // 2. 删除 Token 辅助索引（admin + front）
        redisHelper.delete(SxwlRedisKeyUtils.tokenUserSetKey("admin", userId));
        redisHelper.delete(SxwlRedisKeyUtils.tokenUserSetKey("front", userId));

        // 3. 获取所有设备 ID
        Set<String> deviceIds = redisHelper.smembers(
                SxwlRedisKeyUtils.onlineDevicesSetKey(userId));

        if (deviceIds != null && !deviceIds.isEmpty()) {
            // 4. 逐设备删除在线信息
            for (String deviceId : deviceIds) {
                String key = SxwlRedisKeyUtils.onlineUserDeviceKey(userId, deviceId);
                redisHelper.delete(key);
                log.info("强制踢人: 清除在线信息 userId={}, deviceId={}", userId, deviceId);
            }
            // 5. 删除设备索引
            redisHelper.delete(SxwlRedisKeyUtils.onlineDevicesSetKey(userId));
        }

        // 6. 从在线用户集合中移除
        redisHelper.srem(SxwlRedisKeyConstants.ONLINE_USERS_KEY, String.valueOf(userId));
        log.info("强制踢人完成: userId={}", userId);
    }

    // ==================== 私有方法 ====================

    /**
     * 从 Redis Hash 构建在线用户 DTO
     */
    private SysOnlineUserDTO buildDto(Long userId, String deviceId) {
        String key = SxwlRedisKeyUtils.onlineUserDeviceKey(userId, deviceId);
        Map<String, String> data = redisHelper.hgetAll(key);
        if (data == null || data.isEmpty()) {
            return null;
        }

        SysOnlineUserDTO dto = new SysOnlineUserDTO();
        dto.setUserId(userId);
        dto.setUsername(data.get("username"));
        dto.setIp(data.get("ip"));
        dto.setBrowser(data.get("browser"));
        dto.setOs(data.get("os"));
        dto.setDeviceId(deviceId);
        String loginTimeStr = data.get("loginTime");
        if (loginTimeStr != null) {
            dto.setLoginTime(LocalDateTime.parse(loginTimeStr, DTF));
        }
        return dto;
    }

    /**
     * 计算总页数
     */
    private int calcPages(long total, int pageSize) {
        return pageSize > 0 ? (int) Math.ceil((double) total / pageSize) : 0;
    }
}

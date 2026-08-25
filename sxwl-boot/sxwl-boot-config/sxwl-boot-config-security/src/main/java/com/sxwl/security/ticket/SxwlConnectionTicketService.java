package com.sxwl.security.ticket;

import com.sxwl.common.utils.SxwlRedisKeyUtils;
import com.sxwl.redis.helper.SxwlRedisHelper;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

/** 为无法发送 Authorization 请求头的流式连接签发短期票据。 */
@Component
public class SxwlConnectionTicketService {

    private static final Duration TICKET_TTL = Duration.ofSeconds(60);

    private final SxwlRedisHelper redisHelper;

    public SxwlConnectionTicketService(SxwlRedisHelper redisHelper) {
        this.redisHelper = redisHelper;
    }

    public String issue(Long userId) {
        String ticket = UUID.randomUUID().toString();
        redisHelper.set(SxwlRedisKeyUtils.connectionTicketKey(ticket), String.valueOf(userId), TICKET_TTL);
        return ticket;
    }

    public Optional<Long> consume(String ticket) {
        if (ticket == null || ticket.isBlank()) {
            return Optional.empty();
        }
        Optional<String> storedTicket = redisHelper.getAndDelete(
                SxwlRedisKeyUtils.connectionTicketKey(ticket));
        return storedTicket
                .flatMap(value -> {
                    try {
                        return Optional.of(Long.parseLong(value));
                    } catch (NumberFormatException ignored) {
                        return Optional.empty();
                    }
                });
    }
}

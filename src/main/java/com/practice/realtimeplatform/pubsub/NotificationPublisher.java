package com.practice.realtimeplatform.pubsub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationPublisher {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void publish(Long postId, NotificationEvent event) {
        String channel = "channel:post:" + postId;
        try {
            String message = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(channel, message);
            log.info("[Pub/Sub 발행] channel={}, type={}", channel, event.getType());
        } catch (JacksonException e) {
            log.error("[Pub/Sub 발행 실패] postId={}", postId, e);
        }
    }
}

package com.practice.realtimeplatform.pubsub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationSubscriber {

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public void onMessage(String message, String channel) {
        try {
            NotificationEvent event = objectMapper.readValue(message, NotificationEvent.class);
            log.info("[Pub/Sub 수신] channel={} | type={} | postId={} | user={}",
                    channel, event.getType(), event.getPostId(), event.getUserId());

            messagingTemplate.convertAndSend("/topic/post/" + event.getPostId(), event);
            log.info("[WebSocket 전송] /topic/post/{}", event.getPostId());

        } catch (JacksonException e) {
            log.error("[Pub/Sub 수신 실패] channel={}, raw={}", channel, message);
        }
    }
}

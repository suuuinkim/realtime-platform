package com.practice.realtimeplatform.kafka;

import com.practice.realtimeplatform.pubsub.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationKafkaConsumer {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "like-events", groupId = "notification-group")
    public void consume(String message) {
        try {
            NotificationEvent event = objectMapper.readValue(message, NotificationEvent.class);
            log.info("[Kafka 수신] type={}, postId={}, user={}", event.getType(), event.getPostId(), event.getUserId());

            messagingTemplate.convertAndSend("/topic/post/" + event.getPostId(), event);
            log.info("[WebSocket 전송] /topic/post/{}", event.getPostId());

        } catch (JacksonException e) {
            log.error("[Kafka 수신 역직렬화 실패] raw={}", message, e);
        }
    }
}

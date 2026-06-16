package com.practice.realtimeplatform.global.kafka;

import com.practice.realtimeplatform.application.event.ApplicationEvent;
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
public class ApplicationEventConsumer {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "application-events", groupId = "notification-group")
    public void consume(String message) {
        try {
            ApplicationEvent event = objectMapper.readValue(message, ApplicationEvent.class);
            log.info("[Kafka consumed] type={}, courseId={}, userId={}, status={}",
                    event.eventType(), event.courseId(), event.userId(), event.status());

            messagingTemplate.convertAndSend("/topic/courses/" + event.courseId(), event);
            messagingTemplate.convertAndSend("/topic/users/" + event.userId() + "/applications", event);
        } catch (JacksonException e) {
            log.error("[Kafka consume failed] raw={}", message, e);
        }
    }
}

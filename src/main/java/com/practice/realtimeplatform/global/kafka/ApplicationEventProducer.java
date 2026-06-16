package com.practice.realtimeplatform.global.kafka;

import com.practice.realtimeplatform.application.event.ApplicationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationEventProducer {

    private static final String TOPIC = "application-events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(ApplicationEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, event.courseId(), message)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("[Kafka publish failed] topic={}, courseId={}, eventType={}",
                                    TOPIC, event.courseId(), event.eventType(), ex);
                            return;
                        }

                        log.info("[Kafka published] topic={}, partition={}, offset={}, courseId={}, eventType={}",
                                TOPIC,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset(),
                                event.courseId(),
                                event.eventType());
                    });
        } catch (JacksonException e) {
            log.error("[Kafka serialization failed] courseId={}, eventType={}",
                    event.courseId(), event.eventType(), e);
        }
    }
}

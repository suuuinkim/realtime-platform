package com.practice.realtimeplatform.global.kafka;

import com.practice.realtimeplatform.global.notification.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationKafkaProducer {

    private static final String TOPIC = "like-events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(NotificationEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, String.valueOf(event.getPostId()), message)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("[Kafka 諛쒗뻾 ?ㅽ뙣] topic={}, postId={}", TOPIC, event.getPostId(), ex);
                        } else {
                            log.info("[Kafka 諛쒗뻾] topic={}, partition={}, offset={}",
                                    TOPIC,
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        }
                    });
        } catch (JacksonException e) {
            log.error("[Kafka 諛쒗뻾 吏곷젹???ㅽ뙣] postId={}", event.getPostId(), e);
        }
    }
}

package com.practice.realtimeplatform.global.config;

import com.practice.realtimeplatform.application.listener.HoldExpiryListener;
import com.practice.realtimeplatform.global.notification.NotificationSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    public RedisMessageListenerContainer listenerContainer(
            RedisConnectionFactory factory,
            MessageListenerAdapter notificationAdapter,
            MessageListenerAdapter holdExpiryAdapter
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        // 게시글 Pub/Sub 알림 구독
        container.addMessageListener(notificationAdapter, new PatternTopic("channel:post:*"));
        // 홀드 TTL 만료 이벤트 구독
        container.addMessageListener(holdExpiryAdapter, new ChannelTopic("__keyevent@0__:expired"));
        return container;
    }

    @Bean
    public MessageListenerAdapter notificationAdapter(NotificationSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "onMessage");
    }

    @Bean
    public MessageListenerAdapter holdExpiryAdapter(HoldExpiryListener listener) {
        return new MessageListenerAdapter(listener, "onExpired");
    }
}

package com.practice.realtimeplatform.global.config;

import com.practice.realtimeplatform.global.notification.NotificationSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
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
            MessageListenerAdapter listenerAdapter
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        // channel:post:* ?⑦꽩??紐⑤뱺 梨꾨꼸 援щ룆
        container.addMessageListener(listenerAdapter, new PatternTopic("channel:post:*"));
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(NotificationSubscriber subscriber) {
        // NotificationSubscriber.onMessage() 硫붿꽌?쒕줈 ?꾩엫
        return new MessageListenerAdapter(subscriber, "onMessage");
    }
}

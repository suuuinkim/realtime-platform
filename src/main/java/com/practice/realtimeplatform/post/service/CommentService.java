package com.practice.realtimeplatform.post.service;

import com.practice.realtimeplatform.global.notification.NotificationEvent;
import com.practice.realtimeplatform.global.notification.NotificationPublisher;
import com.practice.realtimeplatform.global.redis.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private static final String COMMENT_COUNT_KEY = "post:comments:";

    private final RedisService redisService;
    private final NotificationPublisher publisher;

    public Long addComment(Long postId, String userId, String content) {
        Long count = redisService.increment(COMMENT_COUNT_KEY + postId);
        publisher.publish(postId, new NotificationEvent("COMMENT", postId, userId, content));
        return count;
    }

    public Long getCommentCount(Long postId) {
        return redisService.getCount(COMMENT_COUNT_KEY + postId);
    }
}

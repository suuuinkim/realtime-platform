package com.practice.realtimeplatform.post.service;

import com.practice.realtimeplatform.global.redis.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class LikeService {

    private static final String LIKE_KEY = "like:post:%d:user:%s";
    private static final String LIKE_COUNT_KEY = "post:likes:";

    private final RedisService redisService;
    private final RankingService rankingService;

    public String like(Long postId, String userId) {
        String key = String.format(LIKE_KEY, postId, userId);

        boolean success = redisService.setIfAbsent(key, "1", Duration.ofDays(1));
        if (!success) {
            return "Already liked";
        }

        redisService.increment(LIKE_COUNT_KEY + postId);
        rankingService.addLikeScore(postId);
        return "Liked";
    }

    public String unlike(Long postId, String userId) {
        String key = String.format(LIKE_KEY, postId, userId);

        if (!redisService.hasKey(key)) {
            return "Like does not exist";
        }

        redisService.delete(key);

        Long count = redisService.getCount(LIKE_COUNT_KEY + postId);
        if (count > 0) {
            redisService.decrement(LIKE_COUNT_KEY + postId);
        }

        rankingService.subtractLikeScore(postId);
        return "Unliked";
    }

    public Long getLikeCount(Long postId) {
        return redisService.getCount(LIKE_COUNT_KEY + postId);
    }

    public boolean hasLiked(Long postId, String userId) {
        return redisService.hasKey(String.format(LIKE_KEY, postId, userId));
    }
}

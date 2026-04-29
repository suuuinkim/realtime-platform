package com.practice.realtimeplatform.service;

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
            return "이미 좋아요를 눌렀습니다.";
        }

        redisService.increment(LIKE_COUNT_KEY + postId);
        rankingService.addLikeScore(postId);
        return "좋아요!";
    }

    public String unlike(Long postId, String userId) {
        String key = String.format(LIKE_KEY, postId, userId);

        if (!redisService.hasKey(key)) {
            return "좋아요를 누르지 않은 게시글입니다.";
        }

        redisService.delete(key);

        Long count = redisService.getCount(LIKE_COUNT_KEY + postId);
        if (count > 0) {
            redisService.decrement(LIKE_COUNT_KEY + postId);
        }

        rankingService.subtractLikeScore(postId);
        return "좋아요 취소!";
    }

    public Long getLikeCount(Long postId) {
        return redisService.getCount(LIKE_COUNT_KEY + postId);
    }

    public boolean hasLiked(Long postId, String userId) {
        return redisService.hasKey(String.format(LIKE_KEY, postId, userId));
    }
}

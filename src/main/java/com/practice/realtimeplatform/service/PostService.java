package com.practice.realtimeplatform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private static final String VIEW_COUNT_KEY = "post:views:";

    private final RedisService redisService;
    private final RankingService rankingService;

    public Long incrementViewCount(Long postId) {
        rankingService.addViewScore(postId);
        return redisService.increment(VIEW_COUNT_KEY + postId);
    }

    public Long getViewCount(Long postId) {
        return redisService.getCount(VIEW_COUNT_KEY + postId);
    }

    public void resetViewCount(Long postId) {
        redisService.delete(VIEW_COUNT_KEY + postId);
    }
}

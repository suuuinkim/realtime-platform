package com.practice.realtimeplatform.post.service;

import com.practice.realtimeplatform.global.redis.service.RedisService;
import com.practice.realtimeplatform.post.dto.PostRankResponse;
import com.practice.realtimeplatform.post.dto.RankingPostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RankingService {

    private static final String RANKING_KEY = "ranking:posts";
    private static final double VIEW_SCORE = 1.0;
    private static final double LIKE_SCORE = 3.0;

    private final RedisService redisService;

    public void addViewScore(Long postId) {
        redisService.incrementZScore(RANKING_KEY, postId.toString(), VIEW_SCORE);
    }

    public void addLikeScore(Long postId) {
        redisService.incrementZScore(RANKING_KEY, postId.toString(), LIKE_SCORE);
    }

    public void subtractLikeScore(Long postId) {
        redisService.incrementZScore(RANKING_KEY, postId.toString(), -LIKE_SCORE);
    }

    public List<RankingPostResponse> getTopPosts(int count) {
        Set<ZSetOperations.TypedTuple<String>> result =
                redisService.getTopRanking(RANKING_KEY, count);

        List<RankingPostResponse> ranking = new ArrayList<>();
        int rank = 1;
        for (ZSetOperations.TypedTuple<String> entry : result) {
            ranking.add(new RankingPostResponse(
                    rank++,
                    entry.getValue(),
                    entry.getScore() != null ? entry.getScore().intValue() : 0
            ));
        }
        return ranking;
    }

    public PostRankResponse getPostRank(Long postId) {
        Long rank = redisService.getRank(RANKING_KEY, postId.toString());
        Double score = redisService.getScore(RANKING_KEY, postId.toString());
        return new PostRankResponse(
                postId,
                rank != null ? rank + 1 : -1,
                score != null ? score.intValue() : 0
        );
    }
}

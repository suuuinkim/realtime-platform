package com.practice.realtimeplatform.controller;

import com.practice.realtimeplatform.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ranking")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    // 인기 게시글 TOP N 조회
    @GetMapping("/posts")
    public ResponseEntity<List<Map<String, Object>>> getTopPosts(
            @RequestParam(defaultValue = "10") int count
    ) {
        return ResponseEntity.ok(rankingService.getTopPosts(count));
    }

    // 특정 게시글의 현재 순위 조회
    @GetMapping("/posts/{postId}")
    public ResponseEntity<Map<String, Object>> getPostRank(@PathVariable Long postId) {
        return ResponseEntity.ok(rankingService.getPostRank(postId));
    }
}

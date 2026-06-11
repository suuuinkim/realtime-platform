package com.practice.realtimeplatform.post.controller;

import com.practice.realtimeplatform.post.dto.PostRankResponse;
import com.practice.realtimeplatform.post.dto.RankingPostResponse;
import com.practice.realtimeplatform.post.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ranking")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    // ?멸린 寃뚯떆湲 TOP N 議고쉶
    @GetMapping("/posts")
    public ResponseEntity<List<RankingPostResponse>> getTopPosts(
            @RequestParam(defaultValue = "10") int count
    ) {
        return ResponseEntity.ok(rankingService.getTopPosts(count));
    }

    // ?뱀젙 寃뚯떆湲???꾩옱 ?쒖쐞 議고쉶
    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostRankResponse> getPostRank(@PathVariable Long postId) {
        return ResponseEntity.ok(rankingService.getPostRank(postId));
    }
}

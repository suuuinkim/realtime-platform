package com.practice.realtimeplatform.controller;

import com.practice.realtimeplatform.dto.PostStatusResponse;
import com.practice.realtimeplatform.dto.PostViewResponse;
import com.practice.realtimeplatform.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 게시글 조회 — 조회할 때마다 조회수 1 증가
    @GetMapping("/{postId}")
    public ResponseEntity<PostViewResponse> getPost(@PathVariable Long postId) {
        Long viewCount = postService.incrementViewCount(postId);
        return ResponseEntity.ok(new PostViewResponse(postId, viewCount));
    }

    // 현재 조회수만 확인 (증가 없음)se
    @GetMapping("/{postId}/views")
    public ResponseEntity<PostViewResponse> getViewCount(@PathVariable Long postId) {
        Long viewCount = postService.getViewCount(postId);
        return ResponseEntity.ok(new PostViewResponse(postId, viewCount));
    }

    // 테스트용 초기화
    @DeleteMapping("/{postId}/views")
    public ResponseEntity<PostStatusResponse> resetViewCount(@PathVariable Long postId) {
        postService.resetViewCount(postId);
        return ResponseEntity.ok(new PostStatusResponse(postId, "초기화됨"));
    }
}

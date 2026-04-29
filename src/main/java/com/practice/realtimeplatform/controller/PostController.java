package com.practice.realtimeplatform.controller;

import com.practice.realtimeplatform.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 게시글 조회 — 조회할 때마다 조회수 1 증가
    @GetMapping("/{postId}")
    public ResponseEntity<Map<String, Object>> getPost(@PathVariable Long postId) {
        Long viewCount = postService.incrementViewCount(postId);
        return ResponseEntity.ok(Map.of(
                "postId", postId,
                "viewCount", viewCount
        ));
    }

    // 현재 조회수만 확인 (증가 없음)se
    @GetMapping("/{postId}/views")
    public ResponseEntity<Map<String, Object>> getViewCount(@PathVariable Long postId) {
        Long viewCount = postService.getViewCount(postId);
        return ResponseEntity.ok(Map.of(
                "postId", postId,
                "viewCount", viewCount
        ));
    }

    // 테스트용 초기화
    @DeleteMapping("/{postId}/views")
    public ResponseEntity<Map<String, Object>> resetViewCount(@PathVariable Long postId) {
        postService.resetViewCount(postId);
        return ResponseEntity.ok(Map.of(
                "postId", postId,
                "status", "초기화됨"
        ));
    }
}

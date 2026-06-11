package com.practice.realtimeplatform.post.controller;

import com.practice.realtimeplatform.post.dto.LikeActionResponse;
import com.practice.realtimeplatform.post.dto.LikeCheckResponse;
import com.practice.realtimeplatform.post.dto.LikeCountResponse;
import com.practice.realtimeplatform.post.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts/{postId}/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<LikeActionResponse> like(
            @PathVariable Long postId,
            @RequestParam String userId
    ) {
        String message = likeService.like(postId, userId);
        Long likeCount = likeService.getLikeCount(postId);
        return ResponseEntity.ok(new LikeActionResponse(postId, userId, message, likeCount));
    }

    @DeleteMapping
    public ResponseEntity<LikeActionResponse> unlike(
            @PathVariable Long postId,
            @RequestParam String userId
    ) {
        String message = likeService.unlike(postId, userId);
        Long likeCount = likeService.getLikeCount(postId);
        return ResponseEntity.ok(new LikeActionResponse(postId, userId, message, likeCount));
    }

    @GetMapping
    public ResponseEntity<LikeCountResponse> getLikeCount(@PathVariable Long postId) {
        return ResponseEntity.ok(new LikeCountResponse(postId, likeService.getLikeCount(postId)));
    }

    @GetMapping("/check")
    public ResponseEntity<LikeCheckResponse> hasLiked(
            @PathVariable Long postId,
            @RequestParam String userId
    ) {
        return ResponseEntity.ok(new LikeCheckResponse(postId, userId, likeService.hasLiked(postId, userId)));
    }
}

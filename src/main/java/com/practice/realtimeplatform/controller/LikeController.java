package com.practice.realtimeplatform.controller;

import com.practice.realtimeplatform.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/posts/{postId}/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> like(
            @PathVariable Long postId,
            @RequestParam String userId
    ) {
        String message = likeService.like(postId, userId);
        Long likeCount = likeService.getLikeCount(postId);
        return ResponseEntity.ok(Map.of(
                "postId", postId,
                "userId", userId,
                "message", message,
                "likeCount", likeCount
        ));
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Object>> unlike(
            @PathVariable Long postId,
            @RequestParam String userId
    ) {
        String message = likeService.unlike(postId, userId);
        Long likeCount = likeService.getLikeCount(postId);
        return ResponseEntity.ok(Map.of(
                "postId", postId,
                "userId", userId,
                "message", message,
                "likeCount", likeCount
        ));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getLikeCount(@PathVariable Long postId) {
        return ResponseEntity.ok(Map.of(
                "postId", postId,
                "likeCount", likeService.getLikeCount(postId)
        ));
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> hasLiked(
            @PathVariable Long postId,
            @RequestParam String userId
    ) {
        return ResponseEntity.ok(Map.of(
                "postId", postId,
                "userId", userId,
                "hasLiked", likeService.hasLiked(postId, userId)
        ));
    }
}

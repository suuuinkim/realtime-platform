package com.practice.realtimeplatform.controller;

import com.practice.realtimeplatform.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> addComment(
            @PathVariable Long postId,
            @RequestParam String userId,
            @RequestParam String content
    ) {
        Long count = commentService.addComment(postId, userId, content);
        return ResponseEntity.ok(Map.of(
                "postId", postId,
                "userId", userId,
                "content", content,
                "commentCount", count
        ));
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getCommentCount(@PathVariable Long postId) {
        return ResponseEntity.ok(Map.of(
                "postId", postId,
                "commentCount", commentService.getCommentCount(postId)
        ));
    }
}

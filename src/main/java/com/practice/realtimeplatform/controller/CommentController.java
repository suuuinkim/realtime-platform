package com.practice.realtimeplatform.controller;

import com.practice.realtimeplatform.dto.CommentCountResponse;
import com.practice.realtimeplatform.dto.CommentResponse;
import com.practice.realtimeplatform.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long postId,
            @RequestParam String userId,
            @RequestParam String content
    ) {
        Long count = commentService.addComment(postId, userId, content);
        return ResponseEntity.ok(new CommentResponse(postId, userId, content, count));
    }

    @GetMapping("/count")
    public ResponseEntity<CommentCountResponse> getCommentCount(@PathVariable Long postId) {
        return ResponseEntity.ok(new CommentCountResponse(postId, commentService.getCommentCount(postId)));
    }
}

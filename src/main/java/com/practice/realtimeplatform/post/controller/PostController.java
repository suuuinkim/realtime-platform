package com.practice.realtimeplatform.post.controller;

import com.practice.realtimeplatform.post.dto.PostStatusResponse;
import com.practice.realtimeplatform.post.dto.PostViewResponse;
import com.practice.realtimeplatform.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 寃뚯떆湲 議고쉶 ??議고쉶???뚮쭏??議고쉶??1 利앷?
    @GetMapping("/{postId}")
    public ResponseEntity<PostViewResponse> getPost(@PathVariable Long postId) {
        Long viewCount = postService.incrementViewCount(postId);
        return ResponseEntity.ok(new PostViewResponse(postId, viewCount));
    }

    // ?꾩옱 議고쉶?섎쭔 ?뺤씤 (利앷? ?놁쓬)se
    @GetMapping("/{postId}/views")
    public ResponseEntity<PostViewResponse> getViewCount(@PathVariable Long postId) {
        Long viewCount = postService.getViewCount(postId);
        return ResponseEntity.ok(new PostViewResponse(postId, viewCount));
    }

    // ?뚯뒪?몄슜 珥덇린??
    @DeleteMapping("/{postId}/views")
    public ResponseEntity<PostStatusResponse> resetViewCount(@PathVariable Long postId) {
        postService.resetViewCount(postId);
        return ResponseEntity.ok(new PostStatusResponse(postId, "珥덇린?붾맖"));
    }
}

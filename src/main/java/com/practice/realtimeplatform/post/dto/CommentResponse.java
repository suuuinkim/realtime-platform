package com.practice.realtimeplatform.post.dto;

public record CommentResponse(Long postId, String userId, String content, Long commentCount) {
}

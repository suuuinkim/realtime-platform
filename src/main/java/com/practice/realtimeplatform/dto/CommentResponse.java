package com.practice.realtimeplatform.dto;

public record CommentResponse(Long postId, String userId, String content, Long commentCount) {
}

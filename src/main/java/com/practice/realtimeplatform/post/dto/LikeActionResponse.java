package com.practice.realtimeplatform.post.dto;

public record LikeActionResponse(Long postId, String userId, String message, Long likeCount) {
}

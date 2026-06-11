package com.practice.realtimeplatform.post.dto;

public record LikeCheckResponse(Long postId, String userId, boolean hasLiked) {
}

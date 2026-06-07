package com.practice.realtimeplatform.dto;

public record LikeCheckResponse(Long postId, String userId, boolean hasLiked) {
}

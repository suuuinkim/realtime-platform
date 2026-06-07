package com.practice.realtimeplatform.dto;

public record LikeActionResponse(Long postId, String userId, String message, Long likeCount) {
}

package com.practice.realtimeplatform.application.dto;

public record QueuePositionResponse(
        String courseId,
        String userId,
        Long position,
        Long waitingCount
) {
}

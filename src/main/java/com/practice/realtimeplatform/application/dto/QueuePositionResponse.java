package com.practice.realtimeplatform.application.dto;

import com.practice.realtimeplatform.application.service.ApplicationStatus;

public record QueuePositionResponse(
        String courseId,
        String userId,
        Long position,
        Long waitingCount,
        ApplicationStatus status,
        Long holdTtlSeconds
) {
}

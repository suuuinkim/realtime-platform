package com.practice.realtimeplatform.application.dto;

import com.practice.realtimeplatform.application.service.ApplicationStatus;

public record ApplicationResponse(
        String courseId,
        String userId,
        String applicationId,
        ApplicationStatus status,
        Long position,
        Long confirmedCount,
        Long waitingCount,
        Long holdTtlSeconds,
        String message
) {
}

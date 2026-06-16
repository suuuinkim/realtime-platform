package com.practice.realtimeplatform.application.event;

import com.practice.realtimeplatform.application.service.ApplicationStatus;

public record ApplicationEvent(
        String eventType,
        String courseId,
        String userId,
        String applicationId,
        ApplicationStatus status,
        Long position,
        String message
) {
}

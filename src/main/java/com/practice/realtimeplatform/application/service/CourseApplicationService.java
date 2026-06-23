package com.practice.realtimeplatform.application.service;

import com.practice.realtimeplatform.application.dto.ApplicationResponse;
import com.practice.realtimeplatform.application.dto.QueuePositionResponse;
import com.practice.realtimeplatform.application.repository.CourseApplicationRedisRepository;
import com.practice.realtimeplatform.global.redis.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
@Service
@RequiredArgsConstructor
public class CourseApplicationService {

    private static final int DEFAULT_CAPACITY = 20;
    private static final Duration HOLD_TTL = Duration.ofMinutes(5);

    private static final String CONFIRMED_KEY = "course:%s:confirmed";
    private static final String QUEUE_KEY = "course:%s:queue";
    private static final String HOLD_KEY = "course:%s:hold:%s";

    private final RedisService redisService;
    private final CourseApplicationRedisRepository applicationRepository;

    public ApplicationResponse requestApplication(String courseId, String userId) {
        var result = applicationRepository.request(courseId, userId, HOLD_TTL, DEFAULT_CAPACITY);
        return switch (result) {
            case ALREADY_CONFIRMED -> response(courseId, userId, ApplicationStatus.CONFIRMED, null, "Already confirmed");
            case ALREADY_HOLDING -> response(courseId, userId, ApplicationStatus.HOLDING, null, "Application hold is active");
            case HOLD_GRANTED -> response(courseId, userId, ApplicationStatus.HOLDING, null, "Application hold granted");
            case QUEUE_ENTERED -> response(courseId, userId, ApplicationStatus.WAITING,
                    queuePosition(courseId, userId), "Entered waiting queue");
            case ALREADY_WAITING -> response(courseId, userId, ApplicationStatus.WAITING,
                    queuePosition(courseId, userId), "Already waiting");
        };
    }

    public ApplicationResponse confirmApplication(String courseId, String userId) {
        var result = applicationRepository.confirm(courseId, userId);
        return switch (result) {
            case ALREADY_CONFIRMED -> response(courseId, userId, ApplicationStatus.CONFIRMED, null, "Already confirmed");
            case HOLD_EXPIRED -> response(courseId, userId, ApplicationStatus.EXPIRED,
                    queuePosition(courseId, userId), "No active application hold");
            case CONFIRMED -> {
                ApplicationResponse confirmed = response(
                        courseId, userId, ApplicationStatus.CONFIRMED, null, "Application confirmed");
                yield confirmed;
            }
        };
    }

    public QueuePositionResponse getQueuePosition(String courseId, String userId) {
        Long position = queuePosition(courseId, userId);
        Long holdTtl = redisService.getTtl(holdKey(courseId, userId));
        ApplicationStatus status;
        if (redisService.isSetMember(confirmedKey(courseId), userId)) {
            status = ApplicationStatus.CONFIRMED;
        } else if (holdTtl != null && holdTtl > 0) {
            status = ApplicationStatus.HOLDING;
        } else if (position != null) {
            status = ApplicationStatus.WAITING;
        } else {
            status = ApplicationStatus.EXPIRED;
        }
        return new QueuePositionResponse(courseId, userId, position, waitingCount(courseId), status,
                holdTtl != null && holdTtl > 0 ? holdTtl : null);
    }

    public ApplicationResponse advanceQueue(String courseId) {
        String userId = applicationRepository.advance(courseId, HOLD_TTL, DEFAULT_CAPACITY);
        if (userId == null) return systemResponse(courseId, "No promotion available");
        return response(courseId, userId, ApplicationStatus.HOLDING, null, "Application hold granted from queue");
    }

    public void reconcileQueue(String courseId) {
        for (int i = 0; i < DEFAULT_CAPACITY; i++) {
            ApplicationResponse result = advanceQueue(courseId);
            if (result.status() == null) return;
        }
    }

    private Long confirmedCount(String courseId) {
        Long count = redisService.getSetSize(confirmedKey(courseId));
        return count == null ? 0L : count;
    }

    private Long waitingCount(String courseId) {
        Long count = redisService.getZSetSize(queueKey(courseId));
        return count == null ? 0L : count;
    }

    private Long queuePosition(String courseId, String userId) {
        Long rank = redisService.getZSetRank(queueKey(courseId), userId);
        return rank == null ? null : rank + 1;
    }

    private ApplicationResponse response(String courseId, String userId, ApplicationStatus status, Long position, String message) {
        Long holdTtl = redisService.getTtl(holdKey(courseId, userId));
        return new ApplicationResponse(
                courseId,
                userId,
                applicationId(courseId, userId),
                status,
                position,
                confirmedCount(courseId),
                waitingCount(courseId),
                holdTtl != null && holdTtl > 0 ? holdTtl : null,
                message
        );
    }

    private ApplicationResponse systemResponse(String courseId, String message) {
        return new ApplicationResponse(courseId, "system", null, null, null, confirmedCount(courseId), waitingCount(courseId), null, message);
    }

    private String applicationId(String courseId, String userId) {
        return courseId + ":" + userId;
    }

    private String confirmedKey(String courseId) {
        return CONFIRMED_KEY.formatted(courseId);
    }

    private String queueKey(String courseId) {
        return QUEUE_KEY.formatted(courseId);
    }

    private String holdKey(String courseId, String userId) {
        return HOLD_KEY.formatted(courseId, userId);
    }

}

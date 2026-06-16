package com.practice.realtimeplatform.application.service;

import com.practice.realtimeplatform.application.dto.ApplicationResponse;
import com.practice.realtimeplatform.application.dto.QueuePositionResponse;
import com.practice.realtimeplatform.application.event.ApplicationEvent;
import com.practice.realtimeplatform.global.kafka.ApplicationEventProducer;
import com.practice.realtimeplatform.global.redis.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CourseApplicationService {

    private static final int DEFAULT_CAPACITY = 20;
    private static final Duration HOLD_TTL = Duration.ofMinutes(5);

    private static final String CAPACITY_KEY = "course:%s:capacity";
    private static final String CONFIRMED_KEY = "course:%s:confirmed";
    private static final String QUEUE_KEY = "course:%s:queue";
    private static final String HOLD_KEY = "course:%s:hold:%s";
    private static final String HOLDS_KEY = "course:%s:holds";

    private final RedisService redisService;
    private final ApplicationEventProducer eventProducer;

    public ApplicationResponse requestApplication(String courseId, String userId) {
        String confirmedKey = confirmedKey(courseId);
        if (redisService.isSetMember(confirmedKey, userId)) {
            return response(courseId, userId, ApplicationStatus.CONFIRMED, null, "Already confirmed");
        }

        String holdKey = holdKey(courseId, userId);
        if (redisService.hasKey(holdKey)) {
            return response(courseId, userId, ApplicationStatus.HOLDING, null, "Application hold is active");
        }

        if (hasAvailableSeat(courseId)) {
            grantHold(courseId, userId);
            ApplicationResponse response = response(courseId, userId, ApplicationStatus.HOLDING, null, "Application hold granted");
            publish("APPLICATION_HOLD_GRANTED", response);
            return response;
        }

        String queueKey = queueKey(courseId);
        redisService.addToZSet(queueKey, userId, System.currentTimeMillis());
        Long position = queuePosition(courseId, userId);
        ApplicationResponse response = response(courseId, userId, ApplicationStatus.WAITING, position, "Entered waiting queue");
        publish("QUEUE_ENTERED", response);
        return response;
    }

    public ApplicationResponse confirmApplication(String courseId, String userId) {
        String confirmedKey = confirmedKey(courseId);
        if (redisService.isSetMember(confirmedKey, userId)) {
            return response(courseId, userId, ApplicationStatus.CONFIRMED, null, "Already confirmed");
        }

        String holdKey = holdKey(courseId, userId);
        if (!redisService.hasKey(holdKey)) {
            return response(courseId, userId, ApplicationStatus.EXPIRED, queuePosition(courseId, userId), "No active application hold");
        }

        if (!hasAvailableSeat(courseId)) {
            redisService.delete(holdKey);
            return enqueueAgain(courseId, userId, "Seat is no longer available");
        }

        redisService.addToSet(confirmedKey, userId);
        redisService.delete(holdKey);
        redisService.removeFromZSet(holdsKey(courseId), userId);
        redisService.removeFromZSet(queueKey(courseId), userId);

        ApplicationResponse response = response(courseId, userId, ApplicationStatus.CONFIRMED, null, "Application confirmed");
        publish("APPLICATION_CONFIRMED", response);
        advanceQueue(courseId);
        return response;
    }

    public QueuePositionResponse getQueuePosition(String courseId, String userId) {
        return new QueuePositionResponse(courseId, userId, queuePosition(courseId, userId), waitingCount(courseId));
    }

    public ApplicationResponse advanceQueue(String courseId) {
        if (!hasAvailableSeat(courseId)) {
            return systemResponse(courseId, "No available seat");
        }

        Set<String> users = redisService.getZSetRange(queueKey(courseId), 0, 0);
        if (users == null || users.isEmpty()) {
            return systemResponse(courseId, "Queue is empty");
        }

        String userId = users.iterator().next();
        redisService.removeFromZSet(queueKey(courseId), userId);
        grantHold(courseId, userId);

        ApplicationResponse response = response(courseId, userId, ApplicationStatus.HOLDING, null, "Application hold granted from queue");
        publish("APPLICATION_HOLD_GRANTED", response);
        return response;
    }

    private ApplicationResponse enqueueAgain(String courseId, String userId, String message) {
        redisService.addToZSet(queueKey(courseId), userId, System.currentTimeMillis());
        Long position = queuePosition(courseId, userId);
        ApplicationResponse response = response(courseId, userId, ApplicationStatus.WAITING, position, message);
        publish("QUEUE_ENTERED", response);
        return response;
    }

    private boolean hasAvailableSeat(String courseId) {
        cleanupExpiredHolds(courseId);
        return confirmedCount(courseId) + activeHoldCount(courseId) < capacity(courseId);
    }

    private void grantHold(String courseId, String userId) {
        long expiresAt = System.currentTimeMillis() + HOLD_TTL.toMillis();
        redisService.set(holdKey(courseId, userId), applicationId(courseId, userId), HOLD_TTL.toSeconds());
        redisService.addToZSet(holdsKey(courseId), userId, expiresAt);
    }

    private void cleanupExpiredHolds(String courseId) {
        redisService.removeZSetRangeByScore(holdsKey(courseId), 0, System.currentTimeMillis());
    }

    private Long activeHoldCount(String courseId) {
        Long count = redisService.getZSetSize(holdsKey(courseId));
        return count == null ? 0L : count;
    }

    private int capacity(String courseId) {
        String value = redisService.get(CAPACITY_KEY.formatted(courseId));
        return value == null ? DEFAULT_CAPACITY : Integer.parseInt(value);
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

    private void publish(String eventType, ApplicationResponse response) {
        eventProducer.publish(new ApplicationEvent(
                eventType,
                response.courseId(),
                response.userId(),
                response.applicationId(),
                response.status(),
                response.position(),
                response.message()
        ));
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

    private String holdsKey(String courseId) {
        return HOLDS_KEY.formatted(courseId);
    }
}

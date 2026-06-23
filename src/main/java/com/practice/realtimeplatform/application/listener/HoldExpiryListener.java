package com.practice.realtimeplatform.application.listener;

import com.practice.realtimeplatform.application.service.CourseApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HoldExpiryListener {

    private static final String HOLD_KEY_PREFIX = "course:";
    private static final String HOLD_KEY_SEGMENT = ":hold:";

    private final CourseApplicationService courseApplicationService;

    public void onExpired(String expiredKey) {
        if (!expiredKey.startsWith(HOLD_KEY_PREFIX) || !expiredKey.contains(HOLD_KEY_SEGMENT)) {
            return;
        }

        // key 형식: course:{courseId}:hold:{userId}
        String withoutPrefix = expiredKey.substring(HOLD_KEY_PREFIX.length());
        int holdIdx = withoutPrefix.indexOf(HOLD_KEY_SEGMENT);
        if (holdIdx == -1) return;

        String courseId = withoutPrefix.substring(0, holdIdx);
        String userId = withoutPrefix.substring(holdIdx + HOLD_KEY_SEGMENT.length());

        log.info("[Hold 만료] courseId={}, userId={} → 다음 대기자 승격 시도", courseId, userId);
        courseApplicationService.reconcileQueue(courseId);
    }
}

package com.practice.realtimeplatform.application.controller;

import com.practice.realtimeplatform.application.dto.ApplicationResponse;
import com.practice.realtimeplatform.application.dto.QueuePositionResponse;
import com.practice.realtimeplatform.application.service.CourseApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/courses/{courseId}/applications")
@RequiredArgsConstructor
public class CourseApplicationController {

    private final CourseApplicationService applicationService;

    @PostMapping
    public ResponseEntity<ApplicationResponse> requestApplication(
            @PathVariable String courseId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(applicationService.requestApplication(courseId, authentication.getName()));
    }

    @PostMapping("/confirm")
    public ResponseEntity<ApplicationResponse> confirmApplication(
            @PathVariable String courseId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(applicationService.confirmApplication(courseId, authentication.getName()));
    }

    @GetMapping("/queue/position")
    public ResponseEntity<QueuePositionResponse> getQueuePosition(
            @PathVariable String courseId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(applicationService.getQueuePosition(courseId, authentication.getName()));
    }
}

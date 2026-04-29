package com.practice.realtimeplatform.pubsub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {
    private String type;    // "COMMENT", "LIKE" 등
    private Long postId;
    private String userId;
    private String content;
}

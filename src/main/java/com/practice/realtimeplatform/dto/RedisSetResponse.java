package com.practice.realtimeplatform.dto;

public record RedisSetResponse(String key, String value, String ttl) {
}

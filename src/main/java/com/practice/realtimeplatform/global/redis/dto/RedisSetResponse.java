package com.practice.realtimeplatform.global.redis.dto;

public record RedisSetResponse(String key, String value, String ttl) {
}

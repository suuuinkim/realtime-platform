package com.practice.realtimeplatform.global.redis.controller;

import com.practice.realtimeplatform.global.redis.dto.RedisSetResponse;
import com.practice.realtimeplatform.global.redis.dto.RedisStatusResponse;
import com.practice.realtimeplatform.global.redis.dto.RedisTtlResponse;
import com.practice.realtimeplatform.global.redis.dto.RedisValueResponse;
import com.practice.realtimeplatform.global.redis.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/redis")
@RequiredArgsConstructor
public class RedisController {

    private final RedisService redisService;

    @PostMapping("/set")
    public ResponseEntity<RedisSetResponse> set(@RequestParam String key, @RequestParam String value,
            @RequestParam(defaultValue = "30") long ttl) {
        redisService.set(key, value, ttl);
        return ResponseEntity.ok(new RedisSetResponse(key, value, ttl + "s"));
    }

    @GetMapping("/get")
    public ResponseEntity<RedisValueResponse> get(@RequestParam String key) {
        String value = redisService.get(key);
        if (value == null) {
            return ResponseEntity.ok(new RedisValueResponse(key, "null (만료됐거나 존재하지 않음)"));
        }
        return ResponseEntity.ok(new RedisValueResponse(key, value));
    }

    @GetMapping("/ttl")
    public ResponseEntity<RedisTtlResponse> ttl(@RequestParam String key) {
        Long remaining = redisService.getTtl(key);
        String status = switch (remaining.intValue()) {
            case -2 -> "키가 존재하지 않음";
            case -1 -> "TTL 없음 (영구 키)";
            default -> remaining + "초 남음";
        };
        return ResponseEntity.ok(new RedisTtlResponse(key, status));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<RedisStatusResponse> delete(@RequestParam String key) {
        redisService.delete(key);
        return ResponseEntity.ok(new RedisStatusResponse(key, "deleted"));
    }
}

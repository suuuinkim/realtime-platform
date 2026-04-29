package com.practice.realtimeplatform.controller;

import com.practice.realtimeplatform.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/redis")
@RequiredArgsConstructor
public class RedisController {

    private final RedisService redisService;

    @PostMapping("/set")
    public ResponseEntity<Map<String, String>> set(@RequestParam String key, @RequestParam String value,
            @RequestParam(defaultValue = "30") long ttl) {
        redisService.set(key, value, ttl);
        return ResponseEntity.ok(Map.of(
                "key", key,
                "value", value,
                "ttl", ttl + "s"
        ));
    }

    @GetMapping("/get")
    public ResponseEntity<Map<String, String>> get(@RequestParam String key) {
        String value = redisService.get(key);
        if (value == null) {
            return ResponseEntity.ok(Map.of("key", key, "value", "null (만료되었거나 존재하지 않음)"));
        }
        return ResponseEntity.ok(Map.of("key", key, "value", value));
    }

    @GetMapping("/ttl")
    public ResponseEntity<Map<String, String>> ttl(@RequestParam String key) {
        Long remaining = redisService.getTtl(key);
        String status = switch (remaining.intValue()) {
            case -2 -> "키가 존재하지 않음";
            case -1 -> "TTL 없음 (영구 저장)";
            default -> remaining + "초 남음";
        };
        return ResponseEntity.ok(Map.of("key", key, "ttl", status));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, String>> delete(@RequestParam String key) {
        redisService.delete(key);
        return ResponseEntity.ok(Map.of("key", key, "status", "삭제됨"));
    }
}

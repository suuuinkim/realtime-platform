package com.practice.realtimeplatform.controller;

import com.practice.realtimeplatform.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/redis")
@RequiredArgsConstructor
public class RedisTestController {
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
}

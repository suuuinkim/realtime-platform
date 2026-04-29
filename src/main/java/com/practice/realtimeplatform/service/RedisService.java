package com.practice.realtimeplatform.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public void set(String key, String value, long ttlSeconds) {
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(ttlSeconds));
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 남은 TTL(초) 반환. -1=만료없음, -2=키없음
    public Long getTtl(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    public Long decrement(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }

    public Long getCount(String key) {
        String value = redisTemplate.opsForValue().get(key);
        return value == null ? 0L : Long.parseLong(value);
    }

    // SETNX: 키가 없을 때만 저장 성공(true), 이미 있으면 실패(false)
    public boolean setIfAbsent(String key, String value, Duration ttl) {
        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, value, ttl);
        return Boolean.TRUE.equals(result);
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // Sorted Set: 점수 증가 (ZINCRBY)
    public void incrementZScore(String key, String member, double delta) {
        redisTemplate.opsForZSet().incrementScore(key, member, delta);
    }

    // Sorted Set: 상위 N개 내림차순 조회 (점수 포함)
    public Set<ZSetOperations.TypedTuple<String>> getTopRanking(String key, long count) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, count - 1);
    }

    // Sorted Set: 특정 멤버의 순위 (0-based, 높은 점수 = 낮은 인덱스)
    public Long getRank(String key, String member) {
        return redisTemplate.opsForZSet().reverseRank(key, member);
    }

    // Sorted Set: 특정 멤버의 점수
    public Double getScore(String key, String member) {
        return redisTemplate.opsForZSet().score(key, member);
    }
}

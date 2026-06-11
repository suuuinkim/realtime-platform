package com.practice.realtimeplatform.global.redis.service;

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

    // ?⑥? TTL(珥? 諛섑솚. -1=留뚮즺?놁쓬, -2=?ㅼ뾾??
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

    // SETNX: ?ㅺ? ?놁쓣 ?뚮쭔 ????깃났(true), ?대? ?덉쑝硫??ㅽ뙣(false)
    public boolean setIfAbsent(String key, String value, Duration ttl) {
        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, value, ttl);
        return Boolean.TRUE.equals(result);
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // Sorted Set: ?먯닔 利앷? (ZINCRBY)
    public void incrementZScore(String key, String member, double delta) {
        redisTemplate.opsForZSet().incrementScore(key, member, delta);
    }

    // Sorted Set: ?곸쐞 N媛??대┝李⑥닚 議고쉶 (?먯닔 ?ы븿)
    public Set<ZSetOperations.TypedTuple<String>> getTopRanking(String key, long count) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, count - 1);
    }

    // Sorted Set: ?뱀젙 硫ㅻ쾭???쒖쐞 (0-based, ?믪? ?먯닔 = ??? ?몃뜳??
    public Long getRank(String key, String member) {
        return redisTemplate.opsForZSet().reverseRank(key, member);
    }

    // Sorted Set: ?뱀젙 硫ㅻ쾭???먯닔
    public Double getScore(String key, String member) {
        return redisTemplate.opsForZSet().score(key, member);
    }
}

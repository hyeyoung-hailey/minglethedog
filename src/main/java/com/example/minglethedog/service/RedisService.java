package com.example.minglethedog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60; // 7일 (초 단위)
    private static final String MESSAGE_QUEUE = "post-queue";

    public void saveRefreshToken(String username, String refreshToken) {
        redisTemplate.opsForValue().set(
                "refresh:" + username,
                refreshToken,
                Duration.ofSeconds(REFRESH_TOKEN_EXPIRE_TIME)
        );
    }

    public String getRefreshToken(String username) {
        return redisTemplate.opsForValue().get("refresh:" + username);
    }

    public void deleteRefreshToken(String username) {
        redisTemplate.delete("refresh:" + username);
    }

    //팔로우관계 redis에 추가
    public void followUser(Long followerId, Long followingId) {
        String key = "user:followers:" + followerId;
        redisTemplate.opsForSet().add(key, followingId.toString());
    }

    //언팔로우
    public void unFollowUser(Long followerId, Long followingId) {
        String key = "user:followers:" + followerId;
        redisTemplate.opsForSet().remove(key, followingId.toString());
    }

    //팔로우 관계 가져오기
    public Set<String> getFollowers(Long followerId) {
        String key = "user:followers:" + followerId;
        return redisTemplate.opsForSet().members(key);
    }

    //newsfeed cache 위해 포스트 작성 후 Redis Queue에 저장
    public void pushMessageToQueue(Long postId){
        redisTemplate.opsForList().leftPush(MESSAGE_QUEUE, postId.toString());
    }


}

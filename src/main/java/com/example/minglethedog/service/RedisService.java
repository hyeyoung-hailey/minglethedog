package com.example.minglethedog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60; // 7일 (초 단위)
    private static final int FOLLOWING_LIMIT = 10; // 예: 최대 10명까지 팔로우 가능


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
        String followersKey = "followers:" + followingId;
        String followingKey = "following:" + followerId;

        // 현재 팔로잉 수 확인
        Long count = redisTemplate.opsForSet().size(followingKey);

        if (count != null && count >= FOLLOWING_LIMIT) {
            // 초과하면 가장 오래된 ID 하나 삭제 (LRU)
            String oldestFollowing = redisTemplate.opsForSet().pop(followingKey);
            System.out.println("팔로잉 초과로 삭제된 ID: " + oldestFollowing);
        }

        redisTemplate.opsForSet().add(followersKey, followerId.toString());
        redisTemplate.opsForSet().add(followingKey, followingId.toString());
    }

    //언팔로우
    public void unFollowUser(Long followerId, Long followingId) {
        String key = "user:followers:" + followerId;
        redisTemplate.opsForSet().remove(key, followingId.toString());
    }

    //팔로우 관계 가져오기
    public Set<String> getFollowers(Long followingId) {
        String key = "followers:" + followingId;
        Set<String> followers = redisTemplate.opsForSet().members(key);

        if (followers == null || followers.isEmpty()) {
            return Collections.emptySet(); // 빈 Set 반환하여 NPE 방지
        }

        return followers.stream().map(String::valueOf).collect(Collectors.toSet());
    }

    //팔로잉 목록 가져오기
    public Set<String> getFollowings(Long followerId) {
        String key = "following:" + followerId;
        return redisTemplate.opsForSet().members(key);
    }

    public void updateCache(Long followerId, Long postId) {
        String key = "newsfeed:user:" + followerId;
        // 최신 게시글 추가
        redisTemplate.opsForList().leftPush(key, postId.toString());
        // 뉴스피드 최대 개수 제한
        redisTemplate.opsForList().trim(key, 0, 19);
    }

    public void deleteCache(Long followerId, Long postId) {
        String key = "newsfeed:user:" + followerId;
        redisTemplate.delete(key);
    }

    //newsfeed cache가져오기
    public List<String> cachedNewsfeed(Long followerId, long start, long end) {
        String key = "newsfeed:user:" + followerId;
        return redisTemplate.opsForList().range(key, start, end);
    }

}

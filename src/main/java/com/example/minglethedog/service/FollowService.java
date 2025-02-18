package com.example.minglethedog.service;

import com.example.minglethedog.common.ErrorCode;
import com.example.minglethedog.entity.Follow;
import com.example.minglethedog.entity.User;
import com.example.minglethedog.exception.FollowNotFoundException;
import com.example.minglethedog.exception.UserNotFoundException;
import com.example.minglethedog.repository.FollowRepository;
import com.example.minglethedog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final RedisService redisService;

    @Transactional
    public void follow(Long followerId, Long followeeId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new UserNotFoundException("팔로우 요청자(User)를 찾을 수 없습니다."));

        User followee = userRepository.findById(followeeId)
                .orElseThrow(() -> new UserNotFoundException("팔로우 대상(User)을 찾을 수 없습니다."));

        if (follower.equals(followee)) {
            throw new IllegalArgumentException("자기 자신을 팔로우 할 수 없습니다.");
        }

        if (!followRepository.existsByFollowerAndFollowee(follower, followee)) {
            Follow follow = new Follow(follower, followee);
            followRepository.save(follow);
            redisService.followUser(followerId, followeeId);
        }
    }

    @Transactional
    public void unfollow(Long followerId, Long followeeId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new UserNotFoundException("요청자를 찾을 수 없습니다."));

        User followee = userRepository.findById(followeeId)
                .orElseThrow(() -> new UserNotFoundException("언팔로우 대상(User)을 찾을 수 없습니다."));

        if (followRepository.deleteByFollowerAndFollowee(follower, followee) == 0) {
            throw new FollowNotFoundException(ErrorCode.FOLLOW_NOT_FOUND.getMessage());
        }

        redisService.unFollowUser(followerId, followeeId);
    }


}

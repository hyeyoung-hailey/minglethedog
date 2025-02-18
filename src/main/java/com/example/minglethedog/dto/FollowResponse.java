package com.example.minglethedog.dto;

import lombok.Getter;

@Getter
public class FollowResponse {
    private final Long followerId;
    private final Long followeeId;

    public FollowResponse(Long followerId, Long followeeId) {
        this.followerId = followerId;
        this.followeeId = followeeId;
    }
}

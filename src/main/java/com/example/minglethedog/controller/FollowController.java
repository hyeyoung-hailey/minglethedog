package com.example.minglethedog.controller;

import com.example.minglethedog.common.ApiResponse;
import com.example.minglethedog.common.EntityType;
import com.example.minglethedog.dto.CustomUserDetails;
import com.example.minglethedog.dto.FollowResponse;
import com.example.minglethedog.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    // 팔로우 요청
    @PostMapping("/{followee_id}")
    public ResponseEntity<ApiResponse<FollowResponse>> follow(@AuthenticationPrincipal CustomUserDetails currentUser, @PathVariable Long followee_id) {
        followService.follow(currentUser.getUser().getId(), followee_id);
        FollowResponse followResponse = new FollowResponse(currentUser.getUser().getId(), followee_id);

        return ResponseEntity.ok(ApiResponse.resource(EntityType.FOLLOW, followResponse));
    }

    //언팔로우
    @DeleteMapping("{followee_id}")
    public ResponseEntity<ApiResponse<FollowResponse>> unfollow(@AuthenticationPrincipal CustomUserDetails currentUser, @PathVariable Long followee_id) {
        followService.unfollow(currentUser.getUser().getId(), followee_id);
        FollowResponse followResponse = new FollowResponse(currentUser.getUser().getId(), followee_id);
        return ResponseEntity.ok(ApiResponse.resource(EntityType.FOLLOW, followResponse));
    }


}

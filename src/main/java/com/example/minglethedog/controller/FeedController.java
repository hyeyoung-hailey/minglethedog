package com.example.minglethedog.controller;

import com.example.minglethedog.common.ApiResponse;
import com.example.minglethedog.common.ListEntityBody;
import com.example.minglethedog.dto.CustomUserDetails;
import com.example.minglethedog.dto.PostResponse;
import com.example.minglethedog.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;

    @GetMapping
    public ResponseEntity<ApiResponse<ListEntityBody<PostResponse>>> getFeed(
            @AuthenticationPrincipal CustomUserDetails user
            , @RequestParam(required = false) Long lastCursor, Pageable pageable) {

        ApiResponse<ListEntityBody<PostResponse>> response = feedService.getFeed(user.getUserId(), lastCursor, pageable);
        return ResponseEntity.ok(response);
    }
}

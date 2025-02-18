package com.example.minglethedog.controller;

import com.example.minglethedog.common.ApiResponse;
import com.example.minglethedog.common.EntityType;
import com.example.minglethedog.common.ListEntityBody;
import com.example.minglethedog.dto.CustomUserDetails;
import com.example.minglethedog.dto.PostRequest;
import com.example.minglethedog.dto.PostResponse;
import com.example.minglethedog.entity.Post;
import com.example.minglethedog.repository.PostQueryRepository;
import com.example.minglethedog.service.PostService;
import com.example.minglethedog.service.RedisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;



@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final RedisService redisService;


    /**
     * 1. [사용자] → POST /api/post (게시글 작성)
     * 2. [PostController] → 게시글을 DB에 저장하고, Redis Queue(`post-queue`)에 게시글 ID 저장
     * 3. [MessageQueueTrigger] → 5초마다 Redis에서 게시글 ID 가져와 Kafka로 전송
     * 4. [Kafka] → `"post-feed"` 토픽에 메시지 저장
     * 5. [Kafka Consumer (PostConsumer)] → Kafka에서 메시지를 감지하고 실행
     * 6. [PostConsumer] → 작성자의 팔로워들에게 뉴스피드 업데이트 (Redis에 저장)
     * 7. [사용자] → GET /api/newsfeed/{userId} (뉴스피드 조회 요청)
     * 8. [Redis] → 저장된 뉴스피드를 반환 (빠른 응답)
     * */
    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> post(@AuthenticationPrincipal CustomUserDetails user, @Valid @RequestBody PostRequest postRequest) {
        Post post = new Post(postRequest.getContent(), user.getUser());
        PostResponse postResponse = postService.writePost(post);

        //redis queue에 저장
        redisService.pushMessageToQueue(post.getId());

        return ResponseEntity.ok(ApiResponse.resource(EntityType.POST, postResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(@PathVariable Long id) {
        PostResponse postResponse = postService.getPost(id);
        return ResponseEntity.ok(ApiResponse.resource(EntityType.POST, postResponse));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ListEntityBody<PostResponse>>> getPosts(
            @RequestParam(required = false) Long lastCursor, Pageable pageable) {

        ApiResponse<ListEntityBody<PostResponse>> response = postService.getPosts(lastCursor, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/likes")
    public <T>ResponseEntity<ApiResponse<T>> like(@AuthenticationPrincipal CustomUserDetails user,@RequestParam Long postId) {
        postService.likePost(user.getUserId(), postId);
        return ResponseEntity.noContent().build();

    }

}

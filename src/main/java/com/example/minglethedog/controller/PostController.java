package com.example.minglethedog.controller;

import com.example.minglethedog.common.ApiResponse;
import com.example.minglethedog.common.EntityType;
import com.example.minglethedog.common.ListEntityBody;
import com.example.minglethedog.dto.CustomUserDetails;
import com.example.minglethedog.dto.PostRequest;
import com.example.minglethedog.dto.PostResponse;
import com.example.minglethedog.entity.Post;
import com.example.minglethedog.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final KafkaTemplate<String, String> kafkaTemplate;

//    private static final String TOPIC = "post-topic";

    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> post(@AuthenticationPrincipal CustomUserDetails user, @Valid @RequestBody PostRequest postRequest) {
        Post post = new Post(postRequest.getContent(), user.getUser());
        PostResponse postResponse = postService.writePost(post);

        //cdc 구현으로 주석처리
//        kafkaTemplate.send(TOPIC, post.getId().toString());

        return ResponseEntity.ok(ApiResponse.resource(EntityType.POST, postResponse));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(@PathVariable Long id) {
        PostResponse postResponse = PostResponse.of(postService.getPost(id));
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

package com.example.minglethedog.service;

import com.example.minglethedog.common.ApiResponse;
import com.example.minglethedog.common.EntityType;
import com.example.minglethedog.common.ListEntityBody;
import com.example.minglethedog.dto.PostResponse;
import com.example.minglethedog.entity.Post;
import com.example.minglethedog.repository.PostQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
    private final RedisService redisService;
    private final PostService postService;
    private final PostQueryRepository postQueryRepository;

    public ApiResponse<ListEntityBody<PostResponse>> getFeed(Long userId, Long lastCursor, Pageable pageable) {
        List<String> cachedPostIds = new ArrayList<>(Optional.ofNullable(redisService.cachedNewsfeed(userId, 0, -1))
                .orElseGet(ArrayList::new));

        int pageSize = pageable.getPageSize();
        int remainingSize = Math.max(0, pageSize - cachedPostIds.size());

        //  1. 캐시에서 조회한 게시물 가져오기
        List<PostResponse> result = getCachedPosts(cachedPostIds);

        //  2. DB에서 부족한 데이터 가져오기
        Set<String> followingIds = redisService.getFollowings(userId);
        Slice<Post> postsSlice = postQueryRepository.getPaginatedFeed(lastCursor, pageable, remainingSize, cachedPostIds, followingIds);
        List<PostResponse> dbPosts = new ArrayList<>(postsSlice.getContent().stream()
                .map(PostResponse::of)
                .toList());

        //  3. DB에서 가져온 postId를 레디스 캐시에 업데이트
        dbPosts.forEach(post -> redisService.updateCache(userId, post.getPostId())); // 캐시 업데이트

        result.addAll(dbPosts);

        //  4. 다음 페이지를 위한 커서 설정
        Long newLastCursor = postsSlice.hasContent()
                ? postsSlice.getContent().get(postsSlice.getNumberOfElements() - 1).getId()
                : null;

        log.info("postResponseList size ::: {}", result.size());
        return ApiResponse.list(EntityType.POST, postsSlice.hasNext(), newLastCursor, result);
    }


    public List<PostResponse> getCachedPosts(List<String> cachedPostIds) {
        return new ArrayList<>(cachedPostIds.stream()
                .map(postId -> PostResponse.of(postService.getPost(Long.valueOf(postId))))
                .toList());
    }
}

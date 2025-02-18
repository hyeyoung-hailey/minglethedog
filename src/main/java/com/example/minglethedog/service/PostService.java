package com.example.minglethedog.service;

import com.example.minglethedog.common.ApiResponse;
import com.example.minglethedog.common.EntityType;
import com.example.minglethedog.common.ErrorCode;
import com.example.minglethedog.common.ListEntityBody;
import com.example.minglethedog.dto.PostResponse;
import com.example.minglethedog.entity.Like;
import com.example.minglethedog.entity.Post;
import com.example.minglethedog.exception.PostNotFoundException;
import com.example.minglethedog.repository.LikeRepository;
import com.example.minglethedog.repository.PostQueryRepository;
import com.example.minglethedog.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostQueryRepository postQueryRepository;
    private final LikeRepository likeRepository;

    /**
     * 새로운 포스트 작성
     */
    @Transactional
    public PostResponse writePost(Post post) {
        return new PostResponse(postRepository.save(post).getId());
    }

    /**
     * 단일 게시글을 조회
     *
     * @param id 조회할 게시글의 ID
     * @return 조회된 게시글 정보
     * @throws PostNotFoundException 게시글이 존재하지 않을 경우 발생
     */
    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.valueOf(id))
        );

        return new PostResponse(post.getId(), post.getContent(), post.getAuthor().getId());
    }

    /**
     *
     * @param lastCursor
     * @param pageable
     * @return 페이지네이션된 게시글
     */
    public ApiResponse<ListEntityBody<PostResponse>> getPosts(Long lastCursor, Pageable pageable) {
        Slice<Post> postSlice = postQueryRepository.getPaginatedPosts(lastCursor, pageable);

        boolean hasNext = postSlice.hasNext();

        List<Post> postList = postSlice.getContent();

        Long newLastCursor = postList.isEmpty()
                ? null
                : postList.get(postList.size() - 1).getId();

        List<PostResponse> postResponses = postList.stream()
                .map(post -> new PostResponse(post.getId(), post.getContent(), post.getAuthor().getId()))
                .toList();

        return ApiResponse.list(EntityType.POST, hasNext, newLastCursor, postResponses);
    }

    //좋아요
    public void likePost(Long userId, Long postId) {
        if(!postRepository.existsById(postId)) {
            throw new EntityNotFoundException(String.valueOf(postId));
        }
        if(likeRepository.existsByUserIdAndPostId(userId, postId)) {
            return;
        }
        likeRepository.save(new Like(userId, postId));
    }


}

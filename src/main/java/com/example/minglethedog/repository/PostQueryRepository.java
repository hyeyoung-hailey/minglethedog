package com.example.minglethedog.repository;

import com.example.minglethedog.entity.*;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Repository
public class PostQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Slice<Post> getPaginatedPosts(Long lastCursor, Pageable pageable) {
        QPost post = QPost.post;

        List<Post> posts = queryFactory
                .selectFrom(post)
                .where(lastCursor != null ? post.id.lt(lastCursor) : null) // 커서 기반 페이지네이션 적용
                .orderBy(post.id.desc()) // 최신 글부터 정렬
                .limit(pageable.getPageSize() + 1) // 다음 페이지 여부 확인을 위해 +1 조회
                .fetch();

        // hasNext 여부 판단
        boolean hasNext = posts.size() > pageable.getPageSize();
        if (hasNext) {
            posts.remove(posts.size() - 1); // `hasNext` 확인용으로 조회한 마지막 데이터 제거
        }

        return new SliceImpl<>(posts, pageable, hasNext);
    }

    public Slice<Post> getPaginatedFeed(Long lastCursor, Pageable pageable, int remainingSize, List<String> cachedPostIds, Set<String> followingIds) {
        if (remainingSize == 0) {
            return new SliceImpl<>(new ArrayList<>(), pageable, false);
        }

        List<Post> posts = fetchPosts(lastCursor, remainingSize, cachedPostIds, followingIds);

        boolean hasNext = posts.size() > remainingSize;
        if (hasNext) {
            posts.remove(posts.size() - 1);
        }

        return new SliceImpl<>(posts, pageable, hasNext);
    }

    private List<Post> fetchPosts(Long lastCursor, int remainingSize, List<String> cachedPostIds, Set<String> followingIds) {
        QPost post = QPost.post;

        Set<Long> followingIdSet = followingIds.stream()
                .map(Long::valueOf)
                .collect(Collectors.toSet());

        return queryFactory
                .selectFrom(post)
                .where(
                        post.id.notIn(cachedPostIds.stream().map(Long::valueOf).toList()),
                        post.author.id.in(followingIdSet),
                        lastCursor != null ? post.id.lt(lastCursor) : null
                )
                .orderBy(post.id.desc())
                .limit(remainingSize + 1)
                .fetch();
    }

    public Optional<Post> findByIdWithAuthorId(Long id) {
        QPost post = QPost.post;
        QUser user = QUser.user;
        Post result = queryFactory
                .selectFrom(post)
                .join(post.author, user).fetchJoin()
                .where(post.id.eq(id))
                .fetchOne();
        return Optional.ofNullable(result);
    }

}

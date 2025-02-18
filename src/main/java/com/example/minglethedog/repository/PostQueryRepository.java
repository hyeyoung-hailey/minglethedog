package com.example.minglethedog.repository;

import com.example.minglethedog.entity.Post;
import com.example.minglethedog.entity.QPost;
import com.example.minglethedog.entity.QUser;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

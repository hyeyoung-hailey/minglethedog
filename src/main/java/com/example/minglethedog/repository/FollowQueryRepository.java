package com.example.minglethedog.repository;

import com.example.minglethedog.entity.QFollow;
import com.example.minglethedog.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class FollowQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<Long> findFolloweeIdsByFollowingId(User user) {
        QFollow follow = QFollow.follow;
        return queryFactory
                .select(follow.followee.id)  // followee의 ID만 선택
                .from(follow)
                .where(follow.follower.eq(user)) // follower가 해당 user인 경우
                .fetch();
    }

}

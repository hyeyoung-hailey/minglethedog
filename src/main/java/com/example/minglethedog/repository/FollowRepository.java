package com.example.minglethedog.repository;

import com.example.minglethedog.entity.Follow;
import com.example.minglethedog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerAndFollowee(User follower, User followee);
    int deleteByFollowerAndFollowee(User follower, User followee);
}

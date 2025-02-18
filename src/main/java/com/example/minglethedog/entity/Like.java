package com.example.minglethedog.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "POST_LIKES", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id","post_id"})
})
public class Like {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    public Like(long userId, long postId) {
        this.userId = userId;
        this.postId = postId;
    }

}

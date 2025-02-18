package com.example.minglethedog.dto;

import com.example.minglethedog.entity.Post;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class PostDto implements Serializable {
    private Long id;
    private String content;
    private Long authorId;
    private LocalDateTime createdAt;


    public static PostDto of(Post post) {
        return new PostDto(post.getId(),post.getContent(),post.getAuthor().getId(), post.getCreateTime());
    }

}

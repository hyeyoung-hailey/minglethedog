package com.example.minglethedog.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) //  null 필드는 JSON에서 제외
public class PostResponse {
    private final Long postId;
    private final String content;
    private final Long authorId;

    public PostResponse(Long postId, String content, Long authorId) {
        this.postId = postId;
        this.content = content;
        this.authorId = authorId;
    }

    public PostResponse(Long postId) {
        this.postId = postId;
        this.content = null;
        this.authorId = null;
    }

}
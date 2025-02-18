package com.example.minglethedog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PostRequest {
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    PostRequest(String content) {
        this.content = content;
    }

}

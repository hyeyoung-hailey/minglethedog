package com.example.minglethedog.dto;

import lombok.Getter;

@Getter
public class SignupResponse {
    private final Long id;

    public SignupResponse(Long id) {
        this.id = id;
    }
}

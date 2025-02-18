package com.example.minglethedog.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) //  null 필드는 JSON에서 제외
public class ErrorResponse {
    private final String code;
    private final String message;
    private final List<String> details; // 유효성 검사 오류 또는 기타 에러 목록

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.details = null;
    }

    public ErrorResponse(String code, String message, List<String> details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }


}

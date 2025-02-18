package com.example.minglethedog.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ApiResponse<T> {
    private String entityType;
    private T entityBody;

    // 단일 리소스 응답
    public static <T> ApiResponse<T> resource(EntityType entityType, T entityBody) {
        return new ApiResponse<>(entityType.getValue(), entityBody);
    }

    // 리스트 응답
    public static <T> ApiResponse<ListEntityBody<T>> list(EntityType entityType, boolean hasNext, Long lastCursor, List<T> items) {

        return ApiResponse.<ListEntityBody<T>>builder()
                .entityType(entityType.getValue() + "-list")
                .entityBody(new ListEntityBody<>(hasNext, lastCursor, items))
                .build();
    }

    // 에러 응답
    public static ApiResponse<ErrorResponse> error(EntityType entityType, String code, String message) {
        return ApiResponse.<ErrorResponse>builder()
                .entityType(entityType.getValue())
                .entityBody(new ErrorResponse(code, message))
                .build();
    }


    //  새로운 에러 응답 (ErrorCode + Validation 오류)
    public static ApiResponse<ErrorResponse> error(ErrorCode errorCode, String message, List<String> errors) {
        return ApiResponse.<ErrorResponse>builder()
                .entityType(EntityType.ERROR.getValue()) // 에러 응답 타입을 통일
                .entityBody(new ErrorResponse(errorCode.getCode(), message, errors))
                .build();
    }


}

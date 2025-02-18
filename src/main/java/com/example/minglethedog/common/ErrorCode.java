package com.example.minglethedog.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.text.MessageFormat;
import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "권한이 없습니다."),
    REFRESH_TOKEN_INVALID(HttpStatus.FORBIDDEN, "REFRESH_TOKEN_INVALID", "Refresh Token이 유효하지 않습니다."),
    ACCOUNT_SUSPENDED(HttpStatus.FORBIDDEN, "ACCOUNT_SUSPENDED", "계정이 정지되었습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "존재하지 않는 회원입니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND,"ENTITY_NOT_FOUND","존재하지 않는 리소스입니다. ID:{0}"),
    FOLLOW_NOT_FOUND(HttpStatus.NOT_FOUND,"FOLLOW_NOT_FOUND","팔로우관계가 존재하지 않습니다."),
    USER_DUPLICATED(HttpStatus.CONFLICT,"USER_DUPLICATED","이미존재하는 회원입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public String getFormattedMessage(Object... args) {
        if (args == null || args.length == 0) {
            return message.replace("{0}", "").trim();
        }
        if (args.length == 1 && args[0] instanceof Object[]) {
            // 배열이 전달된 경우, 첫 번째 요소를 사용하여 올바르게 변환
            args = (Object[]) args[0];
        }
        return MessageFormat.format(message, args);
    }
}

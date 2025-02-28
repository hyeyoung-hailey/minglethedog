package com.example.minglethedog.exception;

import com.example.minglethedog.common.ApiResponse;
import com.example.minglethedog.common.EntityType;
import com.example.minglethedog.common.ErrorCode;
import com.example.minglethedog.common.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // CustomException 처리 (통합)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleCustomException(BusinessException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return buildErrorResponse(errorCode);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleUserNotFoundException(UserNotFoundException ex) {
        return buildErrorResponse(ErrorCode.USER_NOT_FOUND);
    }


    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleDuplicateUserException(DuplicateUsernameException ex) {
        return buildErrorResponse(ErrorCode.USER_DUPLICATED);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleEntityNotFoundException(EntityNotFoundException ex) {
        String[] parts = ex.getMessage().split(", ");
        return buildErrorResponse(ErrorCode.ENTITY_NOT_FOUND, (Object) parts);
    }

    @ExceptionHandler(FollowNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleFollowNotFoundException(FollowNotFoundException ex) {
        return buildErrorResponse(ErrorCode.FOLLOW_NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleBadCredentialsException(BadCredentialsException ex) {
        return buildErrorResponse(ErrorCode.BAD_CREDENTIAL);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException error occurred", ex);

        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return ResponseEntity.status(ErrorCode.INVALID_REQUEST.getHttpStatus())
                .body(ApiResponse.error(ErrorCode.INVALID_REQUEST, "입력값이 올바르지 않습니다.", errors));

    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleResponseStatusException(ResponseStatusException ex) {
        return buildErrorResponse(ErrorCode.INVALID_REQUEST);
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleHttpMessageConversionException(HttpMessageConversionException e) {
        log.error("JSON 변환 오류 발생: {}", e.getMessage(), e);
        return buildErrorResponse(ErrorCode.INVALID_REQUEST);

    }

    @ExceptionHandler(RedisConnectionFailureException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleRedisConnectionFailureException(RedisConnectionFailureException ex) {
        log.error("RedisConnectionFailureException error occurred", ex);
        return buildErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }


    // 이 핸들러는 마지막에 위치
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);

        return buildErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    //  공통 응답 빌더 (HTTP 상태 코드 포함)
    private ResponseEntity<ApiResponse<ErrorResponse>> buildErrorResponse(ErrorCode errorCode, Object... arg) {
        String formattedMessage = errorCode.getFormattedMessage(arg);

        ApiResponse<ErrorResponse> errorResponse = ApiResponse.error(
                EntityType.ERROR,
                errorCode.getCode(),
                formattedMessage
        );

        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
    }
}

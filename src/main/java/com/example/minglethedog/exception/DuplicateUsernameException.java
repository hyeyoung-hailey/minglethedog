package com.example.minglethedog.exception;

import com.example.minglethedog.common.ErrorCode;

public class DuplicateUsernameException extends RuntimeException {
    public DuplicateUsernameException(ErrorCode errorCode) {
        super(String.valueOf(errorCode));
    }
}

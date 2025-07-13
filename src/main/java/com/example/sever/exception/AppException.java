package com.example.sever.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppException extends RuntimeException {
    private final ErrorCode errorCode;

    public AppException(ErrorCode errorCode, String s) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
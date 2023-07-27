package com.nts.global.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException{
    private ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

}

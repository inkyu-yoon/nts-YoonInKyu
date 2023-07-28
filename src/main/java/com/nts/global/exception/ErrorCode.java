package com.nts.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "이미 존재하는 사용자명 입니다."),
    FAIL_ENCRYPT_PASSWORD(HttpStatus.INTERNAL_SERVER_ERROR, "비밀번호 암호화에 실패하였습니다. 다음에 시도해주세요.");

    private HttpStatus httpStatus;
    private String message;
}

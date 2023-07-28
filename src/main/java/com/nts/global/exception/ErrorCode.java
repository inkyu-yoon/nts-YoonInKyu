package com.nts.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "이미 존재하는 사용자명 입니다."),
    FAIL_ENCRYPT_PASSWORD(HttpStatus.INTERNAL_SERVER_ERROR, "비밀번호 암호화에 실패하였습니다. 다음에 시도해주세요."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "등록된 사용자가 아닙니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "등록된 게시글을 찾을 수 없습니다."),
    EXCEED_HASHTAG_SIZE(HttpStatus.BAD_REQUEST, "해시태그 개수는 5개를 초과할 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다.");

    private HttpStatus httpStatus;
    private String message;
}

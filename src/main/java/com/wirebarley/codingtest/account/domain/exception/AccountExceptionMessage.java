package com.wirebarley.codingtest.account.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AccountExceptionMessage {
    ACCOUNT_NUMBER_REQUIRED("계좌번호는 필수입니다.", HttpStatus.BAD_REQUEST),
    INVALID_INITIAL_BALANCE("초기 잔액은 0 이상이어야 합니다.", HttpStatus.BAD_REQUEST),
    ACCOUNT_ALREADY_CLOSED("이미 삭제된 계좌입니다.", HttpStatus.BAD_REQUEST),
    ACCOUNT_INACTIVE("비활성 계좌입니다.", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_BALANCE("잔액이 부족합니다.", HttpStatus.BAD_REQUEST),
    INVALID_AMOUNT("금액은 0보다 커야 합니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus httpStatus;

    AccountExceptionMessage(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}

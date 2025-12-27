package com.wirebarley.codingtest.account.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TransferExceptionMessage {

    DAILY_LIMIT_EXCEEDED(
            "일 송금 한도를 초과했습니다.",
            HttpStatus.BAD_REQUEST
    );

    private final String message;
    private final HttpStatus httpStatus;

    TransferExceptionMessage(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}

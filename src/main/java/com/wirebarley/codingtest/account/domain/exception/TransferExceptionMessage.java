package com.wirebarley.codingtest.account.domain.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TransferExceptionMessage {

    DAILY_LIMIT_EXCEEDED(
            "일 송금 한도를 초과했습니다.",
            HttpStatus.BAD_REQUEST
    ),
    SENDER_ACCOUNT_NOT_EXIST("송금자 계좌가 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    RECEIVER_ACCOUNT_NOT_EXIST("수취자 계좌가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus httpStatus;

    TransferExceptionMessage(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}

package com.wirebarley.codingtest.account.domain.exception;

import lombok.Getter;

@Getter
public enum TransferExceptionMessage {
    DAILY_LIMIT_EXCEEDED("일 송금 한도를 초과했습니다.");

    private final String message;

    TransferExceptionMessage(String message) {
        this.message = message;
    }
}

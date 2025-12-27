package com.wirebarley.codingtest.account.domain.exception;

import lombok.Getter;

@Getter
public enum WithdrawExceptionMessage {
    DAILY_LIMIT_EXCEEDED("일 출금 한도를 초과했습니다.");

    private final String message;

    WithdrawExceptionMessage(String message) {
        this.message = message;
    }

}

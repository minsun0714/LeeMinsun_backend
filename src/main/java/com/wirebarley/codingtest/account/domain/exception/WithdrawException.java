package com.wirebarley.codingtest.account.domain.exception;

import lombok.Getter;

@Getter
public class WithdrawException extends RuntimeException {

    private final WithdrawExceptionMessage type;

    public WithdrawException(WithdrawExceptionMessage type) {
        super(type.getMessage());
        this.type = type;
    }

}
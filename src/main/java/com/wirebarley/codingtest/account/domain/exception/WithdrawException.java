package com.wirebarley.codingtest.account.domain.exception;

import lombok.Getter;

import com.wirebarley.codingtest.common.exception.BaseException;

@Getter
public class WithdrawException extends BaseException {

    private final WithdrawExceptionMessage type;

    public WithdrawException(WithdrawExceptionMessage type) {
        super(type.getMessage(), type.getHttpStatus());
        this.type = type;
    }
}

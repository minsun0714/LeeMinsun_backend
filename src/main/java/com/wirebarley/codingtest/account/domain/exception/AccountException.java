package com.wirebarley.codingtest.account.domain.exception;

import com.wirebarley.codingtest.common.exception.BaseException;
import lombok.Getter;

@Getter
public class AccountException extends BaseException {

    private final AccountExceptionMessage type;

    public AccountException(AccountExceptionMessage type) {
        super(type.getMessage(), type.getHttpStatus());
        this.type = type;
    }
}
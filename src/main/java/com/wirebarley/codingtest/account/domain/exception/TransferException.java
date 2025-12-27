package com.wirebarley.codingtest.account.domain.exception;

import com.wirebarley.codingtest.common.exception.BaseException;
import lombok.Getter;

@Getter
public class TransferException extends BaseException {

    private final TransferExceptionMessage type;

    public TransferException(TransferExceptionMessage type) {
        super(type.getMessage(), type.getHttpStatus());
        this.type = type;
    }
}
package com.wirebarley.codingtest.account.domain.exception;

import lombok.Getter;

@Getter
public class TransferException extends RuntimeException {

    private final TransferExceptionMessage type;

    public TransferException(TransferExceptionMessage type) {
        super(type.getMessage());
        this.type = type;
    }

}
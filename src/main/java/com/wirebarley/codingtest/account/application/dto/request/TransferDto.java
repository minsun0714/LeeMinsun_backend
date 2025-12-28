package com.wirebarley.codingtest.account.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferDto(
        @NotNull
        @Positive
        Long fromAccountId,

        @NotNull
        @Positive
        Long toAccountId,

        @NotNull
        @Positive
        BigDecimal amount
) {
}

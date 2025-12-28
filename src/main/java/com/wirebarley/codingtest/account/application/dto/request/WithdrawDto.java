package com.wirebarley.codingtest.account.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record WithdrawDto(
        @NotNull
        @Positive
        Long accountId,

        @NotNull
        @Positive
        BigDecimal amount
) {
}

package com.wirebarley.codingtest.account.application.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AccountCreateDto(
        @NotNull
        String accountNumber,

        @NotNull
        BigDecimal initialAmount
) {
}

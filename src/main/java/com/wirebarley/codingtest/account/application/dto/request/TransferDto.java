package com.wirebarley.codingtest.account.application.dto.request;

import java.math.BigDecimal;

public record TransferDto(
        Long fromAccountId,
        Long toAccountId,
        BigDecimal amount
) {
}

package com.wirebarley.codingtest.account.application.dto.request;

import java.math.BigDecimal;

public record DepositDto(
        Long accountId,
        BigDecimal amount
) {
}

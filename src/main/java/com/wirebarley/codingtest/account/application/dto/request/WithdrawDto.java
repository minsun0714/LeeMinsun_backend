package com.wirebarley.codingtest.account.application.dto.request;

import java.math.BigDecimal;

public record WithdrawDto(
        Long accountId,
        BigDecimal amount
) {
}

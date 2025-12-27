package com.wirebarley.codingtest.account.application.dto.request;

import java.math.BigDecimal;

public record AccountCreateDto(
    String accountNumber,
    BigDecimal initialAmount
) {
}

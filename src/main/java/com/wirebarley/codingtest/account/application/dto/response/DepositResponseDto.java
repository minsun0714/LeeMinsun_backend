package com.wirebarley.codingtest.account.application.dto.response;

import com.wirebarley.codingtest.account.domain.Account;

import java.math.BigDecimal;

public record DepositResponseDto(
        Long accountId,
        BigDecimal balance
) {
    public static DepositResponseDto from(Account account){
        return new DepositResponseDto(
                account.getId(),
                account.getBalance()
        );
    }
}

package com.wirebarley.codingtest.account.application.dto.response;

import com.wirebarley.codingtest.account.domain.Account;

import java.math.BigDecimal;

public record WithdrawResponseDto(
        Long accountId,
        BigDecimal balance
) {
    public static WithdrawResponseDto from(Account account){
        return new WithdrawResponseDto(
                account.getId(),
                account.getBalance()
        );
    }
}

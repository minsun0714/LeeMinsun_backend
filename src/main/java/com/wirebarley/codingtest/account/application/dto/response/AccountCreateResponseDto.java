package com.wirebarley.codingtest.account.application.dto.response;

import com.wirebarley.codingtest.account.domain.Account;
import com.wirebarley.codingtest.account.domain.AccountStatus;

import java.math.BigDecimal;

public record AccountCreateResponseDto (
        Long accountId,
        String accountNumber,
        BigDecimal balance,
        AccountStatus status
) {
    public static AccountCreateResponseDto from(Account account){
        return new AccountCreateResponseDto(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getStatus()
        );
    }
}

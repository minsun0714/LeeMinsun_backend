package com.wirebarley.codingtest.account.application.dto.response;

import com.wirebarley.codingtest.account.domain.Account;
import com.wirebarley.codingtest.account.domain.AccountStatus;

public record AccountCloseResponseDto(
        Long accountId,
        AccountStatus status
) {
    public static AccountCloseResponseDto from(Account account){
        return new AccountCloseResponseDto(
                account.getId(),
                account.getStatus()
        );
    }
}

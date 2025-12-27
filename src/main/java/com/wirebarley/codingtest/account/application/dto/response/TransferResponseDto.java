package com.wirebarley.codingtest.account.application.dto.response;

import com.wirebarley.codingtest.account.domain.Account;
import com.wirebarley.codingtest.account.domain.policy.transfer.TransferPolicy;

import java.math.BigDecimal;

public record TransferResponseDto(
        Long fromAccountId,
        Long toAccountId,
        BigDecimal transferAmount,
        BigDecimal fee,
        BigDecimal fromAccountBalance,
        BigDecimal toAccountBalance
) {
    public static TransferResponseDto from(Account fromAccount, Account toAccount, TransferPolicy.TransferContext transferCtx){
        return new TransferResponseDto(
                fromAccount.getId(),
                toAccount.getId(),
                transferCtx.totalWithdrawAmount(),
                transferCtx.fee(),
                fromAccount.getBalance(),
                toAccount.getBalance()
        );
    }
}

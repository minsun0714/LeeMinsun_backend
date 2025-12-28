package com.wirebarley.codingtest.account.application.dto.response;

import com.wirebarley.codingtest.account.application.dto.request.TransferDto;
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
    public static TransferResponseDto from(Account fromAccount, Account toAccount, TransferPolicy.TransferContext transferCtx, TransferDto transferDto){
        return new TransferResponseDto(
                fromAccount.getId(),
                toAccount.getId(),
                transferDto.amount(),
                transferCtx.fee(),
                fromAccount.getBalance(),
                toAccount.getBalance()
        );
    }
}

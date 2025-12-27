package com.wirebarley.codingtest.account.application.dto.response;

import com.wirebarley.codingtest.account.domain.TransactionHistory;
import com.wirebarley.codingtest.account.domain.TransactionType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record TransactionHistoryResponseDto(
        Long transactionId,
        Long accountId,
        Long counterpartyId,
        TransactionType type,
        BigDecimal amount,
        OffsetDateTime createdAt
) {
    public static TransactionHistoryResponseDto from(TransactionHistory history) {
        return new TransactionHistoryResponseDto(
                history.getId(),
                history.getAccountId(),
                history.getCounterpartyId(),
                history.getType(),
                history.getAmount(),
                history.getCreatedAt()
        );
    }
}
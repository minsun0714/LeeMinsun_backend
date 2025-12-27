package com.wirebarley.codingtest.account.application.dto.response;

import com.wirebarley.codingtest.account.domain.TransactionHistory;
import org.springframework.data.domain.Page;

import java.util.List;

public record ReceiveHistoryResponseDto(
        Long counterpartyId,
        List<TransactionHistoryResponseDto> histories,
        PageMeta pageMeta
) {

    public static ReceiveHistoryResponseDto from(
            Long counterpartyId,
            Page<TransactionHistory> page
    ) {
        List<TransactionHistoryResponseDto> histories =
                page.getContent().stream()
                        .map(TransactionHistoryResponseDto::from)
                        .toList();

        return new ReceiveHistoryResponseDto(
                counterpartyId,
                histories,
                PageMeta.from(page)
        );
    }
}

package com.wirebarley.codingtest.account.application.dto.response;

import java.util.List;

import com.wirebarley.codingtest.account.domain.TransactionHistory;
import org.springframework.data.domain.Page;

public record SendHistoryResponseDto(
        Long accountId,
        List<TransactionHistoryResponseDto> histories,
        PageMeta pageMeta
) {

    public static SendHistoryResponseDto from(
            Long accountId,
            Page<TransactionHistory> page
    ) {
        List<TransactionHistoryResponseDto> histories =
                page.getContent().stream()
                        .map(TransactionHistoryResponseDto::from)
                        .toList();

        return new SendHistoryResponseDto(
                accountId,
                histories,
                PageMeta.from(page)
        );
    }
}

package com.wirebarley.codingtest.account.application.service;

import com.wirebarley.codingtest.account.application.dto.response.ReceiveHistoryResponseDto;
import com.wirebarley.codingtest.account.application.dto.response.SendHistoryResponseDto;
import com.wirebarley.codingtest.account.domain.TransactionHistory;
import com.wirebarley.codingtest.account.domain.TransactionType;
import com.wirebarley.codingtest.account.infrastructure.TransactionHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TransactionHistoryQueryServiceImpl implements TransactionHistoryQueryService {

    private final TransactionHistoryRepository transactionHistoryRepository;

    @Override
    public SendHistoryResponseDto findSendHistory(Long accountId, int page, int size) {
        page = Math.max(page - 1, 0);

        Pageable pageable = PageRequest.of(page, size);

        Page<TransactionHistory> result =
                transactionHistoryRepository
                        .findByAccountIdAndTypeOrderByCreatedAtDesc(
                                accountId,
                                TransactionType.TRANSFER,
                                pageable
                        );

        return SendHistoryResponseDto.from(
                accountId,
                result
        );
    }

    @Override
    public ReceiveHistoryResponseDto findReceiveHistory(Long accountId, int page, int size) {
        page = Math.max(page - 1, 0);

        Pageable pageable = PageRequest.of(page, size);

        Page<TransactionHistory> result =
                transactionHistoryRepository
                        .findByCounterpartyIdAndTypeOrderByCreatedAtDesc(
                                accountId,
                                TransactionType.TRANSFER,
                                pageable
                        );

        return ReceiveHistoryResponseDto.from(
                accountId,
                result
        );
    }
}

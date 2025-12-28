package com.wirebarley.codingtest.account.application.service;

import com.wirebarley.codingtest.account.application.dto.response.ReceiveHistoryResponseDto;
import com.wirebarley.codingtest.account.application.dto.response.SendHistoryResponseDto;
import com.wirebarley.codingtest.account.domain.TransactionHistory;
import com.wirebarley.codingtest.account.domain.TransactionType;
import com.wirebarley.codingtest.account.infrastructure.TransactionHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TransactionHistoryQueryServiceImplTest {

    private TransactionHistoryRepository repository;
    private TransactionHistoryQueryServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(TransactionHistoryRepository.class);
        service = new TransactionHistoryQueryServiceImpl(repository);
    }

    @Test
    @DisplayName("송금 거래 내역을 조회하면 accountId 기준으로 TRANSFER 타입 내역이 반환된다")
    void findSendHistory_ShouldReturnSendHistories() {
        // given
        Long accountId = 1L;
        int page = 1;
        int size = 10;

        TransactionHistory history = createHistory(accountId, 2L);
        Page<TransactionHistory> pageResult =
                new PageImpl<>(List.of(history), PageRequest.of(0, size), 1);

        when(repository.findByAccountIdAndTypeOrderByCreatedAtDesc(
                eq(accountId),
                eq(TransactionType.TRANSFER),
                any(Pageable.class)
        )).thenReturn(pageResult);

        // when
        SendHistoryResponseDto response =
                service.findSendHistory(accountId, page, size);

        // then
        assertThat(response.accountId()).isEqualTo(accountId);
        assertThat(response.histories()).hasSize(1);
        assertThat(response.pageMeta().totalElements()).isEqualTo(1);

        verify(repository, times(1))
                .findByAccountIdAndTypeOrderByCreatedAtDesc(
                        eq(accountId),
                        eq(TransactionType.TRANSFER),
                        any(Pageable.class)
                );
    }

    @Test
    @DisplayName("수신 거래 내역을 조회하면 counterpartyId 기준으로 TRANSFER 타입 내역이 반환된다")
    void findReceiveHistory_ShouldReturnReceiveHistories() {
        // given
        Long counterpartyId = 2L;
        int page = 1;
        int size = 10;

        TransactionHistory history = createHistory(1L, counterpartyId);
        Page<TransactionHistory> pageResult =
                new PageImpl<>(List.of(history), PageRequest.of(0, size), 1);

        when(repository.findByCounterpartyIdAndTypeOrderByCreatedAtDesc(
                eq(counterpartyId),
                eq(TransactionType.TRANSFER),
                any(Pageable.class)
        )).thenReturn(pageResult);

        // when
        ReceiveHistoryResponseDto response =
                service.findReceiveHistory(counterpartyId, page, size);

        // then
        assertThat(response.accountId()).isEqualTo(counterpartyId);
        assertThat(response.histories()).hasSize(1);
        assertThat(response.pageMeta().totalElements()).isEqualTo(1);

        verify(repository, times(1))
                .findByCounterpartyIdAndTypeOrderByCreatedAtDesc(
                        eq(counterpartyId),
                        eq(TransactionType.TRANSFER),
                        any(Pageable.class)
                );
    }

    private TransactionHistory createHistory(Long accountId, Long counterpartyId) {
        TransactionHistory history = mock(TransactionHistory.class);

        when(history.getId()).thenReturn(1L);
        when(history.getAccountId()).thenReturn(accountId);
        when(history.getCounterpartyId()).thenReturn(counterpartyId);
        when(history.getType()).thenReturn(TransactionType.TRANSFER);
        when(history.getAmount()).thenReturn(BigDecimal.valueOf(1000));
        when(history.getCreatedAt()).thenReturn(OffsetDateTime.now());

        return history;
    }
}

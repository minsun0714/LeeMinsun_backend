package com.wirebarley.codingtest.account.presentation.controller;

import com.wirebarley.codingtest.account.domain.TransactionHistory;
import com.wirebarley.codingtest.account.infrastructure.TransactionHistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TransactionHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    @Test
    @DisplayName("송금 내역 조회 - 정상")
    void findSendHistory_success() throws Exception {
        // given
        Long accountId = 1L;

        transfer(accountId, 2L, BigDecimal.valueOf(10000));
        transfer(accountId, 3L, BigDecimal.valueOf(20000));

        // when & then
        mockMvc.perform(
                        get("/accounts/{accountId}/histories/send", accountId)
                                .param("page", "1")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(accountId))
                .andExpect(jsonPath("$.histories.length()").value(2))
                .andExpect(jsonPath("$.histories[0].type").value("TRANSFER"));
    }

    @Test
    @DisplayName("송금 내역 조회 - 페이징 동작")
    void findSendHistory_paging() throws Exception {
        // given
        Long accountId = 1L;

        for (int i = 0; i < 15; i++) {
            transfer(accountId, 2L, BigDecimal.valueOf(1000));
        }

        // when & then
        mockMvc.perform(
                        get("/accounts/{accountId}/histories/send", accountId)
                                .param("page", "1")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.histories.length()").value(10));

        mockMvc.perform(
                        get("/accounts/{accountId}/histories/send", accountId)
                                .param("page", "2")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.histories.length()").value(5));
    }

    private void transfer(
            Long accountId,
            Long counterpartyId,
            BigDecimal amount
    ) {
        TransactionHistory history = TransactionHistory.transfer(
                accountId,
                counterpartyId,
                amount
        );

        transactionHistoryRepository.save(history);
    }
}
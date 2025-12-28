package com.wirebarley.codingtest.account.presentation.controller;

import com.wirebarley.codingtest.account.domain.TransactionHistory;
import com.wirebarley.codingtest.account.infrastructure.TransactionHistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
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
                .andExpect(jsonPath("$.histories[0].type").value("TRANSFER"))
                .andDo(document("transaction-send-history",
                        pathParameters(
                                parameterWithName("accountId").description("계좌 ID")
                        ),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호 (1부터 시작)"),
                                parameterWithName("size").description("페이지 크기")
                        ),
                        responseFields(
                                fieldWithPath("accountId").description("계좌 ID"),
                                fieldWithPath("histories").description("송금(이체) 내역 목록"),

                                fieldWithPath("histories[].transactionId").description("거래 내역 ID"),
                                fieldWithPath("histories[].accountId").description("송금 계좌 ID"),
                                fieldWithPath("histories[].type").description("거래 타입 (TRANSFER)"),
                                fieldWithPath("histories[].amount").description("송금 금액"),
                                fieldWithPath("histories[].counterpartyId").description("상대 계좌 ID"),
                                fieldWithPath("histories[].createdAt").description("거래 시각"),

                                fieldWithPath("pageMeta").description("페이지 정보"),
                                fieldWithPath("pageMeta.page").description("현재 페이지 번호"),
                                fieldWithPath("pageMeta.size").description("페이지 크기"),
                                fieldWithPath("pageMeta.totalElements").description("전체 데이터 수"),
                                fieldWithPath("pageMeta.totalPages").description("전체 페이지 수"),
                                fieldWithPath("pageMeta.hasNext").description("다음 페이지 존재 여부")
                        )
                ));
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
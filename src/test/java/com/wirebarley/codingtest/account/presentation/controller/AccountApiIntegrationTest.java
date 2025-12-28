package com.wirebarley.codingtest.account.presentation.controller;


import com.wirebarley.codingtest.account.domain.Account;
import com.wirebarley.codingtest.account.domain.exception.TransferExceptionMessage;
import com.wirebarley.codingtest.account.domain.exception.WithdrawExceptionMessage;
import com.wirebarley.codingtest.account.infrastructure.AccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
class AccountApiIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountRepository accountRepository;

    @Test
    @DisplayName("계좌를 등록하면 정상적으로 생성된다")
    void createAccount_success() throws Exception {
        // given
        String request = """
            {
              "accountNumber": "111-111",
              "initialAmount": 100000
            }
            """;

        // when & then
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("111-111"))
                .andExpect(jsonPath("$.balance").value(100000))
                .andDo(document("account-create",
                        requestFields(
                                fieldWithPath("accountNumber").description("계좌 번호"),
                                fieldWithPath("initialAmount").description("초기 잔액")
                        ),
                        responseFields(
                                fieldWithPath("accountId").description("계좌 ID"),
                                fieldWithPath("accountNumber").description("계좌 번호"),
                                fieldWithPath("balance").description("현재 잔액"),
                                fieldWithPath("status").description("현재 계좌 상태")
                        )
                ));
    }

    @Test
    @DisplayName("계좌 생성 시 요청 필드명이 잘못되면 400을 반환한다")
    void createAccount_fail_whenInvalidField() throws Exception {
        String request = """
        {
          "account_number": "111-111",
          "initial_amount": 100000
        }
        """;

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andDo(document("error-invalid-request",
                        responseFields(
                                fieldWithPath("code").description("상태 코드"),
                                fieldWithPath("message").description("에러 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("입금하면 잔액이 증가한다")
    void deposit_success() throws Exception {
        Long accountId = createAccount(100_000);

        String request = """
        {
          "accountId" : %d,
          "amount": 50000
        }
        """.formatted(accountId);

        mockMvc.perform(post("/accounts/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(150000))
                .andDo(document("account-deposit",
                            requestFields(
                                    fieldWithPath("accountId").description("계좌 ID"),
                                    fieldWithPath("amount").description("입금 금액")
                            ),
                            responseFields(
                                    fieldWithPath("accountId").description("계좌 ID"),
                                    fieldWithPath("balance").description("입금 후 잔액")
                            )
                        ));
    }

    @Test
    @DisplayName("출금하면 잔액이 감소한다")
    void withdraw_success() throws Exception {
        Long accountId = createAccount(200_000);

        String request = """
        {
          "accountId" : %d,
          "amount": 50000
        }
        """.formatted(accountId);

        mockMvc.perform(post("/accounts/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(150000))
                .andDo(document("account-withdraw",
                        requestFields(
                                fieldWithPath("accountId").description("계좌 ID"),
                                fieldWithPath("amount").description("출금 금액")
                        ),
                        responseFields(
                                fieldWithPath("accountId").description("계좌 ID"),
                                fieldWithPath("balance").description("출금 후 잔액")
                        )
                ));
    }

    @Test
    @DisplayName("출금 한도를 초과하면 실패한다")
    void withdraw_fail_whenDailyLimitExceeded() throws Exception {
        Long accountId = createAccount(3_000_000);

        String request = """
        {
          "accountId" : %d,
          "amount": 1000001
        }
        """.formatted(accountId);

        mockMvc.perform(post("/accounts/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(WithdrawExceptionMessage.DAILY_LIMIT_EXCEEDED.getMessage()));
    }

    @Test
    @DisplayName("여러 번 출금 후 일 출금 한도를 초과하면 실패한다")
    void withdraw_fail_afterMultipleWithdraws_exceedDailyLimit() throws Exception {
        // given
        Long accountId = createAccount(5_000_000);

        String withdraw300k = """
        {
          "accountId": %d,
          "amount": 300000
        }
        """.formatted(accountId);

        String withdraw200k = """
        {
          "accountId": %d,
          "amount": 200000
        }
        """.formatted(accountId);

        String withdraw300kAgain = """
        {
          "accountId": %d,
          "amount": 300000
        }
        """.formatted(accountId);

        String withdraw200kAgain = """
        {
          "accountId": %d,
          "amount": 200000
        }
        """.formatted(accountId);

        String withdraw100kFail = """
        {
          "accountId": %d,
          "amount": 100000
        }
        """.formatted(accountId);

        // when: 300k 출금 (누적 300k)
        mockMvc.perform(post("/accounts/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(withdraw300k))
                .andExpect(status().isOk());

        // when: 200k 출금 (누적 500k)
        mockMvc.perform(post("/accounts/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(withdraw200k))
                .andExpect(status().isOk());

        // when: 300k 출금 (누적 800k)
        mockMvc.perform(post("/accounts/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(withdraw300kAgain))
                .andExpect(status().isOk());

        // when: 200k 출금 (누적 1000k)
        mockMvc.perform(post("/accounts/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(withdraw200kAgain))
                .andExpect(status().isOk());

        // then: 100k 출금 시도 → 누적 1000k + 100k = 1,000k 초과 → 실패
        mockMvc.perform(post("/accounts/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(withdraw100kFail))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(WithdrawExceptionMessage.DAILY_LIMIT_EXCEEDED.getMessage()));
    }


    @Test
    @DisplayName("이체 시 송금/수신 계좌가 모두 반영된다")
    void transfer_success() throws Exception {
        Long fromId = createAccount(1_000_000);
        Long toId = createAccount(100_000);

        String request = """
        {
          "fromAccountId": %d,
          "toAccountId": %d,
          "amount": 200000
        }
        """.formatted(fromId, toId);

        mockMvc.perform(post("/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andDo(document("account-transfer",
                        requestFields(
                                fieldWithPath("fromAccountId").description("송신자 계좌 ID"),
                                fieldWithPath("toAccountId").description("수신자 계좌 ID"),
                                fieldWithPath("amount").description("송금 금액")
                        ),
                        responseFields(
                                fieldWithPath("fromAccountId").description("송신자 계좌 ID"),
                                fieldWithPath("toAccountId").description("수신자 계좌 ID"),
                                fieldWithPath("transferAmount").description("송금 금액"),
                                fieldWithPath("fee").description("송금 수수료"),
                                fieldWithPath("fromAccountBalance").description("송금 후 잔액")
                        )
                ));
    }

    @Test
    @DisplayName("이체 한도를 초과하면 실패한다")
    void transfer_fail_whenDailyLimitExceeded() throws Exception {
        Long fromId = createAccount(5_000_000);
        Long toId = createAccount(0);

        String request = """
        {
          "fromAccountId": %d,
          "toAccountId": %d,
          "amount": 4000000
        }
        """.formatted(fromId, toId);

        mockMvc.perform(post("/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(TransferExceptionMessage.DAILY_LIMIT_EXCEEDED.getMessage()));
    }

    @Test
    @DisplayName("여러 번 송금 후 일 송금 한도를 초과하면 실패한다")
    void transfer_fail_afterMultipleTransfer_exceedDailyLimit() throws Exception {
        // given
        Long fromId = createAccount(5_000_000);
        Long toId1 = createAccount(0);
        Long toId2 = createAccount(0);

        String transfer1m = """
        {
          "fromAccountId": %d,
          "toAccountId": %d,
          "amount": 1000000
        }
        """.formatted(fromId, toId1);

        String transfer2m = """
        {
          "fromAccountId": %d,
          "toAccountId": %d,
          "amount": 2000000
        }
        """.formatted(fromId, toId2);

        String transfer1mAgainFail = """
        {
          "fromAccountId": %d,
          "toAccountId": %d,
          "amount": 1000000
        }
        """.formatted(fromId, toId1);

        // when: 1m 송금 (누적 1m)
        mockMvc.perform(post("/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transfer1m))
                .andExpect(status().isOk());

        // when: 2m 송금 (누적 3m)
        mockMvc.perform(post("/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transfer2m))
                .andExpect(status().isOk());

        // then: 1m 송금 시도 → 누적 3m + 1m = 3m 초과 → 실패
        mockMvc.perform(post("/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transfer1mAgainFail))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value(TransferExceptionMessage.DAILY_LIMIT_EXCEEDED.getMessage()));
    }

    private Long createAccount(int balance) {
        Account account = Account.create("ACC-" + System.nanoTime(),
                BigDecimal.valueOf(balance));
        return accountRepository.save(account).getId();
    }

}

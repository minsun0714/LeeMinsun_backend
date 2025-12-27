package com.wirebarley.codingtest.account.domain.policy;

import com.wirebarley.codingtest.account.domain.Account;
import com.wirebarley.codingtest.account.domain.TransactionType;
import com.wirebarley.codingtest.account.domain.exception.WithdrawException;
import com.wirebarley.codingtest.account.domain.exception.WithdrawExceptionMessage;
import com.wirebarley.codingtest.account.domain.policy.withdraw.WithdrawPolicy;
import com.wirebarley.codingtest.account.infrastructure.TransactionHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

class DailyWithdrawLimitPolicyTest {

    private TransactionHistoryRepository transactionHistoryRepository;
    private WithdrawPolicy policy;

    @BeforeEach
    void setUp() {
        transactionHistoryRepository = Mockito.mock(TransactionHistoryRepository.class);
        policy = new WithdrawPolicy(transactionHistoryRepository);
    }

    private Account createAccount() {
        return Account.create("1234567890", BigDecimal.valueOf(1_000_000));
    }

    @Test
    @DisplayName("오늘 출금 이력이 없으면 출금이 가능하다")
    void validate_success_whenNoWithdrawHistory() {
        Account account = createAccount();
        LocalDate today = LocalDate.now();

        given(transactionHistoryRepository.sumTransactionAmountBetweenByTransactionType(
                eq(account.getId()),
                eq(TransactionType.WITHDRAW),
                any(),
                any()
        )).willReturn(null);

        policy.validate(account, BigDecimal.valueOf(500_000), today);
    }

    @Test
    @DisplayName("일 출금 한도 이하이면 출금이 가능하다")
    void validate_success_whenUnderDailyLimit() {
        Account account = createAccount();
        LocalDate today = LocalDate.now();

        given(transactionHistoryRepository.sumTransactionAmountBetweenByTransactionType(
                eq(account.getId()),
                eq(TransactionType.WITHDRAW),
                any(),
                any()
        )).willReturn(BigDecimal.valueOf(600_000));

        policy.validate(account, BigDecimal.valueOf(400_000), today);
    }

    @Test
    @DisplayName("일 출금 한도를 초과하면 예외가 발생한다")
    void validate_fail_whenDailyLimitExceeded() {
        Account account = createAccount();
        LocalDate today = LocalDate.now();

        given(transactionHistoryRepository.sumTransactionAmountBetweenByTransactionType(
                eq(account.getId()),
                eq(TransactionType.WITHDRAW),
                any(),
                any()
        )).willReturn(BigDecimal.valueOf(800_000));

        assertThatThrownBy(() ->
                policy.validate(account, BigDecimal.valueOf(300_000), today)
        ).isInstanceOf(WithdrawException.class)
                .extracting(Throwable::getMessage)
                .isEqualTo(WithdrawExceptionMessage.DAILY_LIMIT_EXCEEDED.getMessage());
    }
}

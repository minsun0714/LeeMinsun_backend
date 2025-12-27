package com.wirebarley.codingtest.account.domain.policy.withdraw;

import com.wirebarley.codingtest.account.domain.Account;
import com.wirebarley.codingtest.account.domain.TransactionType;
import com.wirebarley.codingtest.account.domain.exception.WithdrawException;
import com.wirebarley.codingtest.account.domain.exception.WithdrawExceptionMessage;
import com.wirebarley.codingtest.account.infrastructure.TransactionHistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithdrawPolicyTest {

    @Mock
    TransactionHistoryRepository transactionHistoryRepository;

    @InjectMocks
    WithdrawPolicy withdrawPolicy;

    @Test
    @DisplayName("오늘 출금 누적 금액이 한도 이하면 정상 통과된다")
    void validate_shouldPass_whenWithinDailyLimit() {
        // given
        Account account = Account.create("111-111", BigDecimal.valueOf(2_000_000));
        BigDecimal withdrawAmount = BigDecimal.valueOf(300_000);
        LocalDate today = LocalDate.now();

        when(transactionHistoryRepository
                .sumTransactionAmountBetweenByTransactionType(
                        any(),
                        eq(TransactionType.WITHDRAW),
                        any(ZonedDateTime.class),
                        any(ZonedDateTime.class)
                ))
                .thenReturn(BigDecimal.valueOf(600_000));

        // when & then
        assertThatCode(() ->
                withdrawPolicy.validate(account, withdrawAmount, today)
        ).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("오늘 출금 누적 금액이 한도를 초과하면 예외가 발생한다")
    void validate_shouldThrowException_whenDailyLimitExceeded() {
        // given
        Account account = Account.create("111-111", BigDecimal.valueOf(2_000_000));
        BigDecimal withdrawAmount = BigDecimal.valueOf(500_000);
        LocalDate today = LocalDate.now();

        when(transactionHistoryRepository
                .sumTransactionAmountBetweenByTransactionType(
                        any(),
                        eq(TransactionType.WITHDRAW),
                        any(ZonedDateTime.class),
                        any(ZonedDateTime.class)
                ))
                .thenReturn(BigDecimal.valueOf(700_000));

        // when & then
        assertThatThrownBy(() ->
                withdrawPolicy.validate(account, withdrawAmount, today)
        )
                .isInstanceOf(WithdrawException.class)
                .extracting("type")
                .isEqualTo(WithdrawExceptionMessage.DAILY_LIMIT_EXCEEDED);
    }

    @Test
    @DisplayName("오늘 출금 이력이 없으면 누적 금액은 0으로 계산된다")
    void validate_shouldTreatNullSumAsZero() {
        // given
        Account account = Account.create("111-111", BigDecimal.valueOf(2_000_000));
        BigDecimal withdrawAmount = BigDecimal.valueOf(500_000);
        LocalDate today = LocalDate.now();

        when(transactionHistoryRepository
                .sumTransactionAmountBetweenByTransactionType(
                        any(),
                        eq(TransactionType.WITHDRAW),
                        any(ZonedDateTime.class),
                        any(ZonedDateTime.class)
                ))
                .thenReturn(null);

        // when & then
        assertThatCode(() ->
                withdrawPolicy.validate(account, withdrawAmount, today)
        ).doesNotThrowAnyException();
    }
}

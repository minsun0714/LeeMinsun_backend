package com.wirebarley.codingtest.account.domain.policy.transfer;

import com.wirebarley.codingtest.account.domain.Account;
import com.wirebarley.codingtest.account.domain.TransactionType;
import com.wirebarley.codingtest.account.domain.exception.TransferException;
import com.wirebarley.codingtest.account.domain.exception.TransferExceptionMessage;
import com.wirebarley.codingtest.account.infrastructure.TransactionHistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferPolicyTest {

    @Mock
    TransactionHistoryRepository transactionHistoryRepository;

    @InjectMocks
    TransferPolicy transferPolicy;

    @Test
    @DisplayName("송금 시 수수료 1%가 계산되고 출금 총액이 반환된다")
    void prepare_shouldCalculateFeeAndTotalWithdrawAmount() {
        // given
        Account fromAccount = Account.create("111-111", BigDecimal.valueOf(1_000_000));
        BigDecimal transferAmount = BigDecimal.valueOf(100_000);
        LocalDate today = LocalDate.now();

        when(transactionHistoryRepository
                .sumTransactionAmountBetweenByTransactionType(
                        any(),
                        eq(TransactionType.TRANSFER),
                        any(OffsetDateTime.class),
                        any(OffsetDateTime.class)
                ))
                .thenReturn(BigDecimal.ZERO);

        // when
        TransferPolicy.TransferContext context =
                transferPolicy.prepare(fromAccount, transferAmount, today);

        // then
        assertThat(context.fee()).isEqualByComparingTo(BigDecimal.valueOf(1_000));
        assertThat(context.totalWithdrawAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(101_000));
    }

    @Test
    @DisplayName("오늘 송금 누적 금액이 한도를 초과하면 예외가 발생한다")
    void prepare_shouldThrowException_whenDailyLimitExceeded() {
        // given
        Account fromAccount = Account.create("111-111", BigDecimal.valueOf(10_000_000));
        BigDecimal transferAmount = BigDecimal.valueOf(1_000_000);
        LocalDate today = LocalDate.now();

        when(transactionHistoryRepository
                .sumTransactionAmountBetweenByTransactionType(
                        any(),
                        eq(TransactionType.TRANSFER),
                        any(OffsetDateTime.class),
                        any(OffsetDateTime.class)
                ))
                .thenReturn(BigDecimal.valueOf(2_500_000));

        // when & then
        assertThatThrownBy(() ->
                transferPolicy.prepare(fromAccount, transferAmount, today)
        )
                .isInstanceOf(TransferException.class)
                .extracting("type")
                .isEqualTo(TransferExceptionMessage.DAILY_LIMIT_EXCEEDED);
    }

    @Test
    @DisplayName("오늘 송금 이력이 없으면 누적 금액은 0으로 계산된다")
    void prepare_shouldTreatNullSumAsZero() {
        // given
        Account fromAccount = Account.create("111-111", BigDecimal.valueOf(5_000_000));
        BigDecimal transferAmount = BigDecimal.valueOf(500_000);
        LocalDate today = LocalDate.now();

        when(transactionHistoryRepository
                .sumTransactionAmountBetweenByTransactionType(
                        any(),
                        eq(TransactionType.TRANSFER),
                        any(OffsetDateTime.class),
                        any(OffsetDateTime.class)
                ))
                .thenReturn(null);

        // when
        TransferPolicy.TransferContext context =
                transferPolicy.prepare(fromAccount, transferAmount, today);

        // then
        assertThat(context.totalWithdrawAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(505_000));
    }
}

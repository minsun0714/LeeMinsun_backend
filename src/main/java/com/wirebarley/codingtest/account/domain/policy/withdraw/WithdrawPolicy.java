package com.wirebarley.codingtest.account.domain.policy.withdraw;

import com.wirebarley.codingtest.account.domain.Account;
import com.wirebarley.codingtest.account.domain.TransactionType;
import com.wirebarley.codingtest.account.domain.exception.WithdrawException;
import com.wirebarley.codingtest.account.domain.exception.WithdrawExceptionMessage;
import com.wirebarley.codingtest.account.infrastructure.TransactionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.*;

@Component
@RequiredArgsConstructor
public class WithdrawPolicy implements WithdrawLimitPolicy {

    private static final BigDecimal DAILY_LIMIT = BigDecimal.valueOf(1_000_000);
    private static final String ZONE_ID = "Asia/Seoul";

    private final TransactionHistoryRepository transactionHistoryRepository;

    @Override
    public void validate(Account account, BigDecimal amount, LocalDate today) {
        ZonedDateTime startOfDayKst = today.atStartOfDay(ZoneId.of(ZONE_ID));
        ZonedDateTime endOfDayKst = startOfDayKst.plusDays(1);

        OffsetDateTime start = startOfDayKst.toOffsetDateTime();
        OffsetDateTime end = endOfDayKst.toOffsetDateTime();

        BigDecimal todayTotalWithdrawAmount =
                transactionHistoryRepository.sumTransactionAmountBetweenByTransactionType(account.getId(), TransactionType.WITHDRAW, start, end);

        if (todayTotalWithdrawAmount == null) {
            todayTotalWithdrawAmount = BigDecimal.ZERO;
        }

        if (todayTotalWithdrawAmount.add(amount).compareTo(DAILY_LIMIT) > 0) {
            throw new WithdrawException(WithdrawExceptionMessage.DAILY_LIMIT_EXCEEDED);
        }
    }
}

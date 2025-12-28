package com.wirebarley.codingtest.account.domain.policy.transfer;

import com.wirebarley.codingtest.account.domain.Account;
import com.wirebarley.codingtest.account.domain.TransactionType;
import com.wirebarley.codingtest.account.domain.exception.TransferException;
import com.wirebarley.codingtest.account.domain.exception.TransferExceptionMessage;
import com.wirebarley.codingtest.account.infrastructure.TransactionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
@RequiredArgsConstructor
public class TransferPolicy implements TransferLimitPolicy, FeePolicy {

    private static final BigDecimal DAILY_LIMIT = BigDecimal.valueOf(3_000_000);
    private static final BigDecimal FEE_RATE = BigDecimal.valueOf(0.01);
    private static final String ZONE_ID = "Asia/Seoul";

    private final TransactionHistoryRepository transactionHistoryRepository;

    @Override
    public TransferContext prepare(Account fromAccount, BigDecimal amount, LocalDate today) {
        BigDecimal transferFee = calculateFee(amount);
        validate(fromAccount, amount.add(transferFee), today);
        return new TransferContext(transferFee, amount.add(transferFee));
    }

    private void validate(Account fromAccount, BigDecimal amount, LocalDate today){
        ZonedDateTime start = today.atStartOfDay(ZoneId.of(ZONE_ID));
        ZonedDateTime end = start.plusDays(1);

        BigDecimal todayTotalTransferAmount =
                transactionHistoryRepository
                        .sumTransactionAmountBetweenByTransactionType(fromAccount.getId(), TransactionType.TRANSFER, start, end);

        if (todayTotalTransferAmount == null) {
            todayTotalTransferAmount = BigDecimal.ZERO;
        }

        if (todayTotalTransferAmount.add(amount).compareTo(DAILY_LIMIT) > 0) {
            throw new TransferException(TransferExceptionMessage.DAILY_LIMIT_EXCEEDED);
        }
    }

    @Override
    public BigDecimal calculateFee(BigDecimal transferAmount) {
        return transferAmount.multiply(FEE_RATE);
    }

    public record TransferContext (
            BigDecimal fee,
            BigDecimal totalWithdrawAmount
    ) {}
}

package com.wirebarley.codingtest.account.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(
        name = "transaction_histories",
        indexes = {
                @Index(name = "idx_tx_account_id", columnList = "account_id"),
                @Index(name = "idx_tx_counterparty_id", columnList = "counterparty_id"),
                @Index(name = "idx_tx_account_created_at", columnList = "account_id, created_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false)
    private Long counterpartyId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @CreationTimestamp
    @Column(nullable = false)
    private OffsetDateTime createdAt;

    private TransactionHistory(
            Long accountId,
            Long counterpartyId,
            TransactionType type,
            BigDecimal amount
    ) {
        this.accountId = accountId;
        this.counterpartyId = counterpartyId;
        this.type = type;
        this.amount = amount;
    }

    public static TransactionHistory deposit(
            Long accountId,
            BigDecimal amount
    ) {
        return new TransactionHistory(
                accountId,
                accountId,
                TransactionType.DEPOSIT,
                amount
        );
    }

    public static TransactionHistory withdraw(
            Long accountId,
            BigDecimal amount
    ) {
        return new TransactionHistory(
                accountId,
                accountId,
                TransactionType.WITHDRAW,
                amount
        );
    }

    public static TransactionHistory transfer(
            Long fromAccountId,
            Long toAccountId,
            BigDecimal amount
    ) {
        return new TransactionHistory(
                fromAccountId,
                toAccountId,
                TransactionType.TRANSFER,
                amount
        );
    }
}

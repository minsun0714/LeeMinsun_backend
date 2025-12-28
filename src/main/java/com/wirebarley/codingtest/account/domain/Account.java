package com.wirebarley.codingtest.account.domain;

import com.wirebarley.codingtest.account.domain.exception.AccountException;
import com.wirebarley.codingtest.account.domain.exception.AccountExceptionMessage;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String accountNumber;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @CreationTimestamp
    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = true)
    private OffsetDateTime updatedAt;

    public static Account create(
            String accountNumber,
            BigDecimal initialBalance
    ) {
        validateCreatable(accountNumber, initialBalance);

        Account account = new Account();

        account.accountNumber = accountNumber;
        account.balance = initialBalance;
        account.status = AccountStatus.ACTIVE;

        return account;
    }

    private static void validateCreatable(String accountNumber, BigDecimal initialBalance){
        if (accountNumber == null || accountNumber.isBlank()){
            throw new AccountException(AccountExceptionMessage.ACCOUNT_NUMBER_REQUIRED);
        }
        if (initialBalance == null || initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new AccountException(AccountExceptionMessage.INVALID_INITIAL_BALANCE);
        }
    }

    public void close() {
        validateClosable();
        this.status = AccountStatus.CLOSED;
    }

    private void validateClosable() {
        if (this.status == AccountStatus.CLOSED) {
            throw new AccountException(AccountExceptionMessage.ACCOUNT_ALREADY_CLOSED);
        }
    }

    public void deposit(BigDecimal amount) {
        validateActive();
        validateAmount(amount);
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount){
        validateActive();
        validateAmount(amount);
        validateBalanceSufficient(amount);
        this.balance = this.balance.subtract(amount);
    }

    private void validateBalanceSufficient(BigDecimal amount) {
        if (this.balance.compareTo(amount) < 0){
            throw new AccountException(AccountExceptionMessage.INSUFFICIENT_BALANCE);
        }
    }

    private void validateActive() {
        if (this.status != AccountStatus.ACTIVE){
            throw new AccountException(AccountExceptionMessage.ACCOUNT_INACTIVE);
        }
    }

    private static void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AccountException(AccountExceptionMessage.INVALID_AMOUNT);
        }
    }
}

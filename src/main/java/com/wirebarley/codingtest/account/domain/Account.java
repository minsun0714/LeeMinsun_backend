package com.wirebarley.codingtest.account.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.springframework.util.Assert.state;

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
        validate(accountNumber, initialBalance);

        Account account = new Account();

        account.accountNumber = accountNumber;
        account.balance = initialBalance;
        account.status = AccountStatus.ACTIVE;

        return account;
    }

    private static void validate(String accountNumber, BigDecimal initialBalance){
        state(accountNumber != null && !accountNumber.isBlank(), "계좌번호는 필수입니다.");
        state(initialBalance != null, "초기 잔액은 필수입니다.");
        state(initialBalance.compareTo(BigDecimal.ZERO) >= 0, "초기 잔액은 음수가 될 수 없습니다.");
    }

    public void close() {
        validateClosable();
        this.status = AccountStatus.CLOSED;
    }

    private void validateClosable() {
        state(this.status == AccountStatus.ACTIVE, "이미 삭제된 계좌입니다.");
    }

    public void deposit(BigDecimal amount) {
        validateActive();
        validateAmount(amount);
        this.balance = this.balance.add(amount);
    }

    private void validateActive() {
        state(this.status == AccountStatus.ACTIVE, "비활성 계좌입니다.");
    }

    private static void validateAmount(BigDecimal amount) {
        state(amount != null && amount.compareTo(BigDecimal.ZERO) > 0, "입금 금액은 0보다 커야 합니다.");
    }
}

package com.wirebarley.codingtest.account.domain;

import com.wirebarley.codingtest.account.domain.exception.AccountException;
import com.wirebarley.codingtest.account.domain.exception.AccountExceptionMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class AccountTest {

    private Account createAccount(BigDecimal balance) {
        return Account.create("1234567890", balance);
    }

    @Test
    @DisplayName("계좌 생성 시 초기 잔액과 상태가 정상적으로 설정된다")
    void createAccount_success() {
        Account account = createAccount(new BigDecimal("10000"));

        assertThat(account.getAccountNumber()).isEqualTo("1234567890");
        assertThat(account.getBalance()).isEqualByComparingTo("10000");
        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
    }

    @Test
    @DisplayName("초기 잔액이 음수이면 계좌 생성에 실패한다")
    void createAccount_fail_whenNegativeBalance() {
        assertThatThrownBy(() ->
                createAccount(new BigDecimal("-1"))
        ).isInstanceOf(AccountException.class)
                .hasMessageContaining(AccountExceptionMessage.INVALID_INITIAL_BALANCE.getMessage());
    }

    @Test
    @DisplayName("입금 시 잔액이 증가한다")
    void deposit_success() {
        Account account = createAccount(new BigDecimal("10000"));

        account.deposit(new BigDecimal("5000"));

        assertThat(account.getBalance()).isEqualByComparingTo("15000");
    }

    @Test
    @DisplayName("0 이하 금액 입금 시 실패한다")
    void deposit_fail_whenInvalidAmount() {
        Account account = createAccount(new BigDecimal("10000"));

        assertThatThrownBy(() ->
                account.deposit(BigDecimal.ZERO)
        ).isInstanceOf(AccountException.class)
                .hasMessageContaining(AccountExceptionMessage.INVALID_AMOUNT.getMessage());
    }

    @Test
    @DisplayName("출금 시 잔액이 감소한다")
    void withdraw_success() {
        Account account = createAccount(new BigDecimal("10000"));

        account.withdraw(new BigDecimal("3000"));

        assertThat(account.getBalance()).isEqualByComparingTo("7000");
    }

    @Test
    @DisplayName("잔액보다 큰 금액 출금 시 실패한다")
    void withdraw_fail_whenInsufficientBalance() {
        Account account = createAccount(new BigDecimal("10000"));

        assertThatThrownBy(() ->
                account.withdraw(new BigDecimal("20000"))
        ).isInstanceOf(AccountException.class)
                .hasMessageContaining(AccountExceptionMessage.INSUFFICIENT_BALANCE.getMessage());
    }

    @Test
    @DisplayName("비활성 계좌에서는 입금이 불가능하다")
    void deposit_fail_whenAccountClosed() {
        Account account = createAccount(new BigDecimal("10000"));
        account.close();

        assertThatThrownBy(() ->
                account.deposit(new BigDecimal("1000"))
        ).isInstanceOf(AccountException.class)
                .hasMessageContaining(AccountExceptionMessage.ACCOUNT_INACTIVE.getMessage());
    }

    @Test
    @DisplayName("비활성 계좌에서는 출금이 불가능하다")
    void withdraw_fail_whenAccountClosed() {
        Account account = createAccount(new BigDecimal("10000"));
        account.close();

        assertThatThrownBy(() ->
                account.withdraw(new BigDecimal("1000"))
        ).isInstanceOf(AccountException.class)
                .hasMessageContaining(AccountExceptionMessage.ACCOUNT_INACTIVE.getMessage());
    }

    @Test
    @DisplayName("계좌는 한 번만 삭제할 수 있다")
    void close_fail_whenAlreadyClosed() {
        Account account = createAccount(new BigDecimal("10000"));
        account.close();

        assertThatThrownBy(account::close)
                .isInstanceOf(AccountException.class)
                .hasMessageContaining(AccountExceptionMessage.ACCOUNT_ALREADY_CLOSED.getMessage());
    }
}

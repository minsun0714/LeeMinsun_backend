package com.wirebarley.codingtest.account.application.service;

import com.wirebarley.codingtest.account.application.dto.request.DepositDto;
import com.wirebarley.codingtest.account.application.dto.request.TransferDto;
import com.wirebarley.codingtest.account.application.dto.request.WithdrawDto;
import com.wirebarley.codingtest.account.domain.Account;
import com.wirebarley.codingtest.account.domain.TransactionHistory;
import com.wirebarley.codingtest.account.domain.policy.transfer.TransferPolicy;
import com.wirebarley.codingtest.account.domain.policy.transfer.TransferLimitPolicy;
import com.wirebarley.codingtest.account.domain.policy.withdraw.WithdrawLimitPolicy;
import com.wirebarley.codingtest.account.infrastructure.AccountRepository;
import com.wirebarley.codingtest.account.infrastructure.TransactionHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AccountServiceImplTest {

    private AccountRepository accountRepository;
    private TransactionHistoryRepository transactionHistoryRepository;
    private WithdrawLimitPolicy withdrawLimitPolicy;
    private TransferLimitPolicy transferLimitPolicy;

    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        transactionHistoryRepository = mock(TransactionHistoryRepository.class);
        withdrawLimitPolicy = mock(WithdrawLimitPolicy.class);
        transferLimitPolicy = mock(TransferLimitPolicy.class);

        accountService = new AccountServiceImpl(
                accountRepository,
                transactionHistoryRepository,
                withdrawLimitPolicy,
                transferLimitPolicy
        );
    }

    @Test
    @DisplayName("입금 시 계좌에 입금되고 거래 내역이 저장된다")
    void deposit_shouldSaveTransactionHistory() {
        // given
        DepositDto dto = new DepositDto(1L, BigDecimal.valueOf(1000));
        Account account = mock(Account.class);

        when(accountRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(account));

        // when
        accountService.deposit(dto);

        // then
        verify(account).deposit(dto.amount());
        verify(transactionHistoryRepository)
                .save(any(TransactionHistory.class));
    }

    @Test
    @DisplayName("출금 시 한도 검증 후 출금되고 거래 내역이 저장된다")
    void withdraw_shouldValidateAndSaveTransactionHistory() {
        // given
        WithdrawDto dto = new WithdrawDto(1L, BigDecimal.valueOf(500));
        Account account = mock(Account.class);

        when(accountRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(account));

        // when
        accountService.withdraw(dto);

        // then
        verify(withdrawLimitPolicy)
                .validate(eq(account), eq(dto.amount()), any(LocalDate.class));
        verify(account).withdraw(dto.amount());
        verify(transactionHistoryRepository)
                .save(any(TransactionHistory.class));
    }

    @Test
    @DisplayName("이체 시 송금/수신 계좌가 반영되고 거래 내역이 저장된다")
    void transfer_shouldWithdrawDepositAndSaveTransactionHistory() {
        // given
        TransferDto dto = new TransferDto(1L, 2L, BigDecimal.valueOf(1000));

        Account fromAccount = mock(Account.class);
        when(fromAccount.getId()).thenReturn(1L);

        Account toAccount = mock(Account.class);
        when(toAccount.getId()).thenReturn(2L);

        when(accountRepository.findAllByIdForUpdate(List.of(1L, 2L)))
                .thenReturn(List.of(fromAccount, toAccount));

        TransferPolicy.TransferContext ctx = mock(TransferPolicy.TransferContext.class);
        when(ctx.totalWithdrawAmount()).thenReturn(BigDecimal.valueOf(1010));

        when(transferLimitPolicy.prepare(
                eq(fromAccount),
                eq(dto.amount()),
                any(LocalDate.class)
        )).thenReturn(ctx);

        // when
        accountService.transfer(dto);

        // then
        verify(fromAccount).withdraw(ctx.totalWithdrawAmount());
        verify(toAccount).deposit(dto.amount());
        verify(transactionHistoryRepository)
                .save(any(TransactionHistory.class));
    }
}

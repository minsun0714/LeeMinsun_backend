package com.wirebarley.codingtest.account.application.service;

import com.wirebarley.codingtest.account.application.dto.request.*;
import com.wirebarley.codingtest.account.application.dto.response.*;
import com.wirebarley.codingtest.account.domain.Account;
import com.wirebarley.codingtest.account.domain.TransactionHistory;
import com.wirebarley.codingtest.account.domain.policy.transfer.TransferPolicy;
import com.wirebarley.codingtest.account.domain.policy.transfer.TransferLimitPolicy;
import com.wirebarley.codingtest.account.domain.policy.withdraw.WithdrawLimitPolicy;
import com.wirebarley.codingtest.account.infrastructure.AccountRepository;
import com.wirebarley.codingtest.account.infrastructure.TransactionHistoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final WithdrawLimitPolicy withdrawLimitPolicy;
    private final TransferLimitPolicy transferLimitPolicy;

    @Override
    public AccountCreateResponseDto create(AccountCreateDto accountCreateDto) {
        Account account = Account.create(
                accountCreateDto.accountNumber(),
                accountCreateDto.initialAmount()
        );

        accountRepository.save(account);

        log.info(
                "[AccountCreated] accountId={}, initialAmount={}",
                account.getId(),
                accountCreateDto.initialAmount()
        );

        return AccountCreateResponseDto.from(account);
    }

    @Override
    public AccountCloseResponseDto close(AccountCloseDto accountCloseDto) {
        Account account = accountRepository.findByIdForUpdate(accountCloseDto.accountId())
                .orElseThrow(() -> new EntityNotFoundException("해당 계좌가 존재하지 않습니다."));

        account.close();

        log.info(
                "[AccountClosed] accountId={}",
                account.getId()
        );

        return AccountCloseResponseDto.from(account);
    }

    @Override
    public DepositResponseDto deposit(DepositDto depositDto) {
        Account account = accountRepository.findByIdForUpdate(depositDto.accountId())
                .orElseThrow(() -> new EntityNotFoundException("해당 계좌가 존재하지 않습니다."));

        account.deposit(depositDto.amount());

        transactionHistoryRepository.save(
                TransactionHistory.deposit(
                        depositDto.accountId(),
                        depositDto.amount()
                )
        );

        log.info(
                "[DepositSuccess] accountId={}, amount={}",
                depositDto.accountId(),
                depositDto.amount()
        );

        return DepositResponseDto.from(account);
    }

    @Override
    public WithdrawResponseDto withdraw(WithdrawDto withdrawDto) {
        Account account = accountRepository.findByIdForUpdate(withdrawDto.accountId())
                .orElseThrow(() -> new EntityNotFoundException("해당 계좌가 존재하지 않습니다."));

        log.debug(
                "[WithdrawAttempt] accountId={}, amount={}",
                withdrawDto.accountId(),
                withdrawDto.amount()
        );

        withdrawLimitPolicy.validate(account, withdrawDto.amount(), LocalDate.now());

        account.withdraw(withdrawDto.amount());

        transactionHistoryRepository.save(
                TransactionHistory.withdraw(
                        withdrawDto.accountId(),
                        withdrawDto.amount()
                )
        );

        log.info(
                "[WithdrawSuccess] accountId={}, amount={}",
                withdrawDto.accountId(),
                withdrawDto.amount()
        );

        return WithdrawResponseDto.from(account);
    }

    @Override
    public TransferResponseDto transfer(TransferDto transferDto) {
        List<Long> accountIds = Stream.of(
                transferDto.fromAccountId(),
                transferDto.toAccountId()
        ).sorted().toList();

        List<Account> accounts = accountRepository.findAllByIdForUpdate(accountIds);

        Account fromAccount = accounts.stream()
                .filter(a -> a.getId().equals(transferDto.fromAccountId()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("송금자 계좌가 존재하지 않습니다."));

        Account toAccount = accounts.stream()
                .filter(a -> a.getId().equals(transferDto.toAccountId()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("수취자 계좌가 존재하지 않습니다."));

        log.debug(
                "[TransferAttempt] fromAccountId={}, toAccountId={}, amount={}",
                fromAccount.getId(),
                toAccount.getId(),
                transferDto.amount()
        );

        TransferPolicy.TransferContext transferCtx =
                transferLimitPolicy.prepare(fromAccount, transferDto.amount(), LocalDate.now());

        fromAccount.withdraw(transferCtx.totalWithdrawAmount());
        toAccount.deposit(transferDto.amount());

        transactionHistoryRepository.save(
                TransactionHistory.transfer(
                        fromAccount.getId(),
                        toAccount.getId(),
                        transferDto.amount()
                )
        );

        log.info(
                "[TransferSuccess] fromAccountId={}, toAccountId={}, amount={}",
                fromAccount.getId(),
                toAccount.getId(),
                transferDto.amount()
        );

        return TransferResponseDto.from(fromAccount, toAccount, transferCtx);
    }
}


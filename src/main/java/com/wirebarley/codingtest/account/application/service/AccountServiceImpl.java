package com.wirebarley.codingtest.account.application.service;

import com.wirebarley.codingtest.account.application.dto.request.*;
import com.wirebarley.codingtest.account.application.dto.response.*;
import com.wirebarley.codingtest.account.domain.Account;
import com.wirebarley.codingtest.account.domain.policy.transfer.TransferPolicy;
import com.wirebarley.codingtest.account.domain.policy.transfer.TransferLimitPolicy;
import com.wirebarley.codingtest.account.domain.policy.withdraw.WithdrawLimitPolicy;
import com.wirebarley.codingtest.account.infrastructure.AccountRepository;
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
    private final WithdrawLimitPolicy withdrawLimitPolicy;
    private final TransferLimitPolicy transferLimitPolicy;

    @Override
    public AccountCreateResponseDto create(AccountCreateDto accountCreateDto) {
        Account account = Account.create(accountCreateDto.accountNumber(), accountCreateDto.initialAmount());

        accountRepository.save(account);

        return AccountCreateResponseDto.from(account);
    }

    @Override
    public AccountCloseResponseDto close(AccountCloseDto accountCloseDto) {
        Account account = accountRepository.findByIdForUpdate(accountCloseDto.accountId())
                .orElseThrow(() -> new EntityNotFoundException("해당 계좌가 존재하지 않습니다."));

        account.close();

        return AccountCloseResponseDto.from(account);
    }

    @Override
    public DepositResponseDto deposit(DepositDto depositDto) {
        Account account = accountRepository.findByIdForUpdate(depositDto.accountId())
                .orElseThrow(() -> new EntityNotFoundException("해당 계좌가 존재하지 않습니다."));

        account.deposit(depositDto.amount());

        return DepositResponseDto.from(account);
    }

    @Override
    public WithdrawResponseDto withdraw(WithdrawDto withdrawDto) {
        Account account = accountRepository.findByIdForUpdate(withdrawDto.accountId())
                .orElseThrow(() -> new EntityNotFoundException("해당 계좌가 존재하지 않습니다."));

        withdrawLimitPolicy.validate(account, withdrawDto.amount(), LocalDate.now());

        account.withdraw(withdrawDto.amount());

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

        TransferPolicy.TransferContext transferCtx =
                transferLimitPolicy.prepare(fromAccount, transferDto.amount(), LocalDate.now());

        fromAccount.withdraw(transferCtx.totalWithdrawAmount());
        toAccount.deposit(transferDto.amount());

        return TransferResponseDto.from(fromAccount, toAccount, transferCtx);
    }
}

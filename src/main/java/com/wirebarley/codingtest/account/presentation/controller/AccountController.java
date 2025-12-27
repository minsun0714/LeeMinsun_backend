package com.wirebarley.codingtest.account.presentation.controller;

import com.wirebarley.codingtest.account.application.dto.request.*;
import com.wirebarley.codingtest.account.application.dto.response.*;
import com.wirebarley.codingtest.account.application.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountCreateResponseDto> create(
            @RequestBody AccountCreateDto request
    ) {
        AccountCreateResponseDto response = accountService.create(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/close")
    public ResponseEntity<AccountCloseResponseDto> close(
            @RequestBody AccountCloseDto request
    ) {
        AccountCloseResponseDto response = accountService.close(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/deposit")
    public ResponseEntity<DepositResponseDto> deposit(
            @RequestBody DepositDto request
    ) {
        DepositResponseDto response = accountService.deposit(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<WithdrawResponseDto> withdraw(
            @RequestBody WithdrawDto request
    ) {
        WithdrawResponseDto response = accountService.withdraw(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponseDto> transfer(
            @RequestBody TransferDto request
    ) {
        TransferResponseDto response = accountService.transfer(request);
        return ResponseEntity.ok(response);
    }
}

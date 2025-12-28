package com.wirebarley.codingtest.account.presentation.controller;

import com.wirebarley.codingtest.account.application.dto.request.*;
import com.wirebarley.codingtest.account.application.dto.response.*;
import com.wirebarley.codingtest.account.application.service.AccountService;
import jakarta.validation.Valid;
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
            @Valid @RequestBody AccountCreateDto request
    ) {
        AccountCreateResponseDto response = accountService.create(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<AccountCloseResponseDto> close(
            @Valid @RequestBody AccountCloseDto request
    ) {
        AccountCloseResponseDto response = accountService.close(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/deposit")
    public ResponseEntity<DepositResponseDto> deposit(
            @Valid @RequestBody DepositDto request
    ) {
        DepositResponseDto response = accountService.deposit(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<WithdrawResponseDto> withdraw(
            @Valid @RequestBody WithdrawDto request
    ) {
        WithdrawResponseDto response = accountService.withdraw(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponseDto> transfer(
            @Valid @RequestBody TransferDto request
    ) {
        TransferResponseDto response = accountService.transfer(request);
        return ResponseEntity.ok(response);
    }
}

package com.wirebarley.codingtest.account.presentation.controller;

import com.wirebarley.codingtest.account.application.dto.response.ReceiveHistoryResponseDto;
import com.wirebarley.codingtest.account.application.dto.response.SendHistoryResponseDto;
import com.wirebarley.codingtest.account.application.service.TransactionHistoryQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class TransactionHistoryController {

    private final TransactionHistoryQueryService transactionHistoryQueryService;

    @GetMapping("/{accountId}/histories/send")
    public ResponseEntity<SendHistoryResponseDto> getSendHistories(
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        SendHistoryResponseDto response =
                transactionHistoryQueryService.findSendHistory(accountId, page, size);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{accountId}/histories/receive")
    public ResponseEntity<ReceiveHistoryResponseDto> getReceiveHistories(
            @PathVariable Long accountId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        ReceiveHistoryResponseDto response =
                transactionHistoryQueryService.findReceiveHistory(accountId, page, size);

        return ResponseEntity.ok(response);
    }
}

package com.wirebarley.codingtest.account.application.service;

import com.wirebarley.codingtest.account.application.dto.response.ReceiveHistoryResponseDto;
import com.wirebarley.codingtest.account.application.dto.response.SendHistoryResponseDto;

public interface TransactionHistoryQueryService {

    SendHistoryResponseDto findSendHistory(Long accountId, int page, int size);

    ReceiveHistoryResponseDto findReceiveHistory(Long counterpartyId, int page, int size);
}

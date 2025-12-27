package com.wirebarley.codingtest.account.application.service;

import com.wirebarley.codingtest.account.application.dto.request.*;
import com.wirebarley.codingtest.account.application.dto.response.*;

public interface AccountService {
    AccountCreateResponseDto create(AccountCreateDto accountCreateDto);
    AccountCloseResponseDto close(AccountCloseDto accountCloseDto);
    DepositResponseDto deposit(DepositDto depositDto);
    WithdrawResponseDto withdraw(WithdrawDto withdrawDto);
    TransferResponseDto transfer(TransferDto transferDto);
}

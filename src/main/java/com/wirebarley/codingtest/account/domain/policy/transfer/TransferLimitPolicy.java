package com.wirebarley.codingtest.account.domain.policy.transfer;

import com.wirebarley.codingtest.account.domain.Account;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TransferLimitPolicy {
    /**
     * 송금 요청이 정책에 따라 허용 가능한지 검증하고 수수료를 계산한다.
     *
     * @param fromAccount 송금자의 계좌
     * @param amount      송금 금액
     * @param today       기준 날짜 (일 한도 판단용)
     * @return
     */
    TransferPolicy.TransferContext prepare(Account fromAccount, BigDecimal amount, LocalDate today);
}

package com.wirebarley.codingtest.account.domain.policy;

import com.wirebarley.codingtest.account.domain.Account;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface WithdrawPolicy {
    /**
     * 출금 요청이 정책에 따라 허용 가능한지 검증한다.
     *
     * @param account 출금 계좌
     * @param amount 출금 금액
     * @param today 기준 날짜 (일 한도 판단용)
     *
     */
    void validate(Account account, BigDecimal amount, LocalDate today);
}

package com.wirebarley.codingtest.account.domain.policy.transfer;

import java.math.BigDecimal;

public interface FeePolicy {
    BigDecimal calculateFee(BigDecimal transferAmount);
}

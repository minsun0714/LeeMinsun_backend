package com.wirebarley.codingtest.account.infrastructure;

import com.wirebarley.codingtest.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}

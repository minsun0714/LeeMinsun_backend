package com.wirebarley.codingtest.account.infrastructure;

import com.wirebarley.codingtest.account.domain.TransactionHistory;
import com.wirebarley.codingtest.account.domain.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {

    @Query("""
        select sum(th.amount)
        from TransactionHistory th
        where th.accountId = :accountId
          and th.type = :type
          and th.createdAt >= :start
          and th.createdAt < :end
    """)
    BigDecimal sumTransactionAmountBetweenByTransactionType(
            @Param("accountId") Long accountId,
            @Param("type") TransactionType type,
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end
    );

    Page<TransactionHistory> findByAccountIdAndTypeOrderByCreatedAtDesc(
            Long accountId,
            TransactionType type,
            Pageable pageable
    );

    Page<TransactionHistory> findByCounterpartyIdAndTypeOrderByCreatedAtDesc(
            Long counterpartyId,
            TransactionType type,
            Pageable pageable
    );
}

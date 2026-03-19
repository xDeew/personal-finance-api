package com.xdeew.finance.transaction.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.xdeew.finance.transaction.model.Transaction;
import com.xdeew.finance.transaction.model.TransactionType;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("""
        SELECT t
        FROM Transaction t
        WHERE t.user.id = :userId
          AND (:accountId IS NULL OR t.account.id = :accountId)
          AND (:categoryId IS NULL OR t.category.id = :categoryId)
          AND (:type IS NULL OR t.type = :type)
          AND (:startDate IS NULL OR t.transactionDate >= :startDate)
          AND (:endDate IS NULL OR t.transactionDate <= :endDate)
        ORDER BY t.transactionDate DESC
    """)
    List<Transaction> findByFilters(Long userId,
            Long accountId,
            Long categoryId,
            TransactionType type,
            LocalDateTime startDate,
            LocalDateTime endDate);

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM Transaction t
        WHERE t.user.id = :userId
          AND t.type = :type
          AND t.transactionDate >= :startDate
          AND t.transactionDate <= :endDate
    """)
    BigDecimal sumAmountByUserIdAndTypeAndDateRange(Long userId,
            TransactionType type,
            LocalDateTime startDate,
            LocalDateTime endDate);

    long countByUserIdAndTransactionDateBetween(Long userId,
            LocalDateTime startDate,
            LocalDateTime endDate);

    @Query("""
    SELECT COALESCE(SUM(t.amount), 0)
    FROM Transaction t
    WHERE t.user.id = :userId
      AND t.category.id = :categoryId
      AND t.type = com.xdeew.finance.transaction.model.TransactionType.EXPENSE
      AND t.transactionDate >= :startDate
      AND t.transactionDate <= :endDate
""")
    BigDecimal sumExpenseByUserIdAndCategoryIdAndDateRange(Long userId,
            Long categoryId,
            LocalDateTime startDate,
            LocalDateTime endDate);

}

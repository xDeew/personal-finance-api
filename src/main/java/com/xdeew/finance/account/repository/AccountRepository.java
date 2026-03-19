package com.xdeew.finance.account.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.xdeew.finance.account.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findAllByUserId(Long userId);
    Optional<Account> findByIdAndUserId(Long id, Long userId);

    @Query("""
        SELECT COALESCE(SUM(a.balance), 0)
        FROM Account a
        WHERE a.user.id = :userId
          AND a.active = true
    """)
    BigDecimal sumActiveBalancesByUserId(Long userId);
}
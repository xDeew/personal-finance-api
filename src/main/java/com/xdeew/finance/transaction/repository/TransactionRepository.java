package com.xdeew.finance.transaction.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xdeew.finance.transaction.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByUserIdOrderByTransactionDateDesc(Long userId);
}
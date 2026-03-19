package com.xdeew.finance.account.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xdeew.finance.account.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findAllByUserId(Long userId);

    java.util.Optional<Account> findByIdAndUserId(Long id, Long userId);
}

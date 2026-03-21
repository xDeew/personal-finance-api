package com.xdeew.finance.transfer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.xdeew.finance.transfer.model.Transfer;

public interface TransferRepository extends JpaRepository<Transfer, Long> {

    Page<Transfer> findAllByUserIdOrderByTransferDateDesc(Long userId, Pageable pageable);
}

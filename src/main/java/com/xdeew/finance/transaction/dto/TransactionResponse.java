package com.xdeew.finance.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.xdeew.finance.transaction.model.TransactionType;

public record TransactionResponse(
        Long id,
        String description,
        BigDecimal amount,
        TransactionType type,
        LocalDateTime transactionDate,
        Long accountId,
        Long categoryId
) {
}
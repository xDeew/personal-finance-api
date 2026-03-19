package com.xdeew.finance.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.xdeew.finance.transaction.model.TransactionType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTransactionRequest(

        @NotBlank(message = "Description is required")
        @Size(max = 255, message = "Description must be at most 255 characters")
        String description,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        BigDecimal amount,

        @NotNull(message = "Type is required")
        TransactionType type,

        @NotNull(message = "Transaction date is required")
        LocalDateTime transactionDate,

        @NotNull(message = "Account id is required")
        Long accountId,

        @NotNull(message = "Category id is required")
        Long categoryId
) {
}
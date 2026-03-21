package com.xdeew.finance.transfer.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTransferRequest(
        @NotBlank(message = "Description is required")
        @Size(max = 255, message = "Description must be at most 255 characters")
        String description,
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        BigDecimal amount,
        @NotNull(message = "Transfer date is required")
        LocalDateTime transferDate,
        @NotNull(message = "Source account id is required")
        Long sourceAccountId,
        @NotNull(message = "Target account id is required")
        Long targetAccountId
        ) {

}

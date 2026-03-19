package com.xdeew.finance.account.dto;

import java.math.BigDecimal;

import com.xdeew.finance.account.model.AccountType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateAccountRequest(

        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must be at most 100 characters")
        String name,

        @NotNull(message = "Type is required")
        AccountType type,

        @NotNull(message = "Balance is required")
        @DecimalMin(value = "0.00", message = "Balance must be greater than or equal to 0")
        BigDecimal balance,

        @NotBlank(message = "Currency is required")
        @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a valid 3-letter code")
        String currency
) {
}
package com.xdeew.finance.account.dto;

import java.math.BigDecimal;

import com.xdeew.finance.account.model.AccountType;

public record AccountResponse(
        Long id,
        String name,
        AccountType type,
        BigDecimal balance,
        String currency,
        boolean active
) {
}